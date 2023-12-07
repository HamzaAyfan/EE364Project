package com.ee364project;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.concurrent.Phaser;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import com.ee364project.helpers.Utilities;

public class Call implements Simulated {
    public static LinkedList<Call> activeCalls = new LinkedList<>();

    public static final long MAXWAITTIME = 60;

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static enum CallState {
        WAITING,
        INCALL,
        ENDED,
        EXIRED
    }

    private static LinkedList<Call> callQueue = new LinkedList<>();
    LinkedHashMap<String, Person> sentencesHashMap = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> lengthsSaved = new LinkedHashMap<>();
    private static long callCount = 0;

    private long startTime;
    private long endTime;
    private long answerTime;
    private int callTimeElapsed;
    public int totalTime;
    private Customer caller;
    private Agent receiver;
    private CallState state;
    private CallCenter callCenter;
    Timeline callTime = new Timeline();

    public static Call getACall() {
        applyExpiry();
        Call call = callQueue.poll();
        if (call != null) {
            call.state = CallState.INCALL;
        }
        return call;
    }

    public static void endACall(Call call) {
        call.state = CallState.ENDED;
    }

    public void increaseTime() {

        if (callTimeElapsed++ > totalTime) {
            for (Entry<CheckBox, Call> entry : Test.linkCBtoCall.entrySet()) {
                if (this.equals(entry.getValue())) {
                    Platform.runLater(() -> Test.vbox.getChildren().remove(entry.getKey()));
                }
            }
            callQueue.remove(this);
            // receiver.setFree();
        }
    }

    public int getTimeElapsed() {
        return callTimeElapsed;
    }

    public LinkedList<Solution> makeLinkedList(Customer caller) {
        HashSet<Solution> HSsolutions = caller.problemState.getProblem().solutions;
        LinkedList<Solution> LLsolutions = new LinkedList<>();
        for (Solution soultion : HSsolutions) {
            try {
                LLsolutions.add((Solution) (soultion.clone()));
            } catch (CloneNotSupportedException e) {
                // To be handled later
            }
        }
        return LLsolutions;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void connectCall(CallCenter callCenter) {
        this.callCenter = callCenter;
        LinkedList<Solution> solutionsCopy = this.makeLinkedList(caller);
        MockDialoge dialoge = new MockDialoge(caller, receiver, this, solutionsCopy);
        this.answerTime = Timekeeper.getTime();
        this.endTime = this.answerTime + dialoge.getContentlength();
        activeCalls.add(this);
    }

    public synchronized void terminateCall() {
        this.callCenter.releaseAgent(this.receiver);
        Call.activeCalls.remove(this);
        Utilities.log(this, "ended", "", "");
        this.caller.problemState.solve();
        this.endTime = Timekeeper.getTime();
        this.state = Call.CallState.ENDED;
    }

    private static void applyExpiry() { // run after every step.
        Call call;
        while (callQueue.size() > 0 && (Timekeeper.getTime() - callQueue.peek().startTime >= MAXWAITTIME)) {
            call = callQueue.poll();
            call.state = CallState.EXIRED;
            Utilities.log(call, "expired", "", "");
        }
    }

    public Call(Customer caller) {
        callCount++;
        this.caller = caller;
        this.startTime = Timekeeper.getTime();
        this.answerTime = 0;
        // this.callDuration = 0;
        this.endTime = 0;
        this.caller = caller;
        this.receiver = null;
        this.state = CallState.WAITING;
        callQueue.add(this);

    }

    public long getCallDuration() {
        return this.endTime - this.answerTime;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getAnswerTime() {
        return this.answerTime;
    }

    public long getWaitTime() {
        return this.answerTime - this.startTime;
    }

    public Customer getCaller() {
        return this.caller;
    }

    public Agent getReceiver() {
        return this.receiver;
    }

    public void setReciever(Agent agent) {
        this.receiver = agent;
    }

    public CallState getState() {
        return this.state;
    }

    @Override
    public String toString() {
        return Utilities.prettyToString("Call" + callCount, this.caller, this.getCallDuration());
    }

    @Override
    public void step() {
        if (this.state == CallState.INCALL) {
            if (this.endTime <= Timekeeper.getTime()) {
                this.terminateCall();
            } else {
                Utilities.log(this, "continues", activeCalls, (this.endTime - Timekeeper.getTime()) + " remaining...");
            }
        }
        applyExpiry();
    }
}

class MockDialoge {
    LinkedList<Solution> solutions = new LinkedList<>();
    public static int ActiveCallNumber;
    private Call currentCall;
    private Customer caller;
    private Agent receiver;
    private boolean firstSolutionSeeked = true;
    LinkedList<String> content = new LinkedList<>();

    int discussionLength;

    public MockDialoge(Customer caller, Agent receiver, Call currentCall, LinkedList<Solution> solutions) {
        this.solutions = solutions;
        this.caller = caller;
        this.receiver = receiver;
        this.currentCall = currentCall;
    }

    public int getContentlength() {
        this.getWords();
        for (String sentence : currentCall.sentencesHashMap.keySet()) {
            int length = sentence.split("\\s+").length;
            discussionLength += length;
            currentCall.lengthsSaved.put(sentence, length);
        }

        // System.out.println(currentCall.sentencesHashMap.keySet());
        return discussionLength;
    }

    private int getSolution() {
        int length = solutions.size(); // print(length + " from call " + z + " agent is " + receiver.getlevel());
        switch (receiver.getlevel()) {
            case SAVEY:
                return 0;
            case CHALLENGED:
                return RandomInt.generateWithinRange(length / 2, length);
            default:
                return RandomInt.generateWithinRange(0, length / 2);
        }
    }

    private void getWords() {
        int i = 0;
        do {
            i = getSolution();
            Solution selectedSolution = solutions.get(i);
            solutions.remove(i);
            introOrTransition(selectedSolution);
            getSteps(selectedSolution);
        } while (i != 0);
    }

    private void getSteps(Solution selectedSolution) {
        for (int j = 0; j < selectedSolution.agentResponses.length; j++) {
            currentCall.sentencesHashMap.put(selectedSolution.agentResponses[j], receiver);
            currentCall.sentencesHashMap.put(selectedSolution.customerResponses[j], caller);
        }
    }

    private void introOrTransition(Solution selectedSolution) {
        if (firstSolutionSeeked == true) {
            currentCall.sentencesHashMap.put(selectedSolution.getRandomIntro(receiver) + "!!!", receiver);
            currentCall.sentencesHashMap.put(selectedSolution.getRandomIntro(caller) + "???", caller);
            firstSolutionSeeked = false;
        } else {
            currentCall.sentencesHashMap.put("I just did that but it did not work", caller);
            currentCall.sentencesHashMap.put("sorry it did not work let me seek an alternative", receiver);
        }
    }
}

class DialogeBox extends Thread {
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    Stage stage = new Stage();
    VBox root = new VBox(10);

    public static LinkedList<DialogeBox> windows = new LinkedList<>();
    public DialogeBox thisWindow;
    private LinkedList<Solution> solutions;
    public static int ActiveCallNumber;
    private Call currentCall;
    private Customer caller;
    private Agent receiver;
    ProgressBar customerVoice;
    ProgressBar agentVoice;
    Timeline timeline;
    LinkedList<String> content = new LinkedList<>();
    KeyFrame kfTalking;
    KeyFrame kfTransitionalPause;
    KeyFrame kfStopTalking;
    ScrollPane scrollPane;
    boolean scrollDown = true;
    boolean scrollDownCheck = true;
    int index;
    public static int numberOfThreads = 1;
    Phaser phaser;
    private boolean stop;
    String startFrom;
    CheckBox checkBox;
    String lastLine;

    public DialogeBox(String callNumber, Phaser phaser, CheckBox checkBox) {

        this.checkBox = checkBox;
        currentCall = Test.linkCBtoCall.get(checkBox);
        this.phaser = phaser;
        phaser.register();

        windows.add(this);

        this.openEmptyWindow(callNumber, 500, 200);
        System.out.println("works");
        // this.run();
    }

    public DialogeBox() {
        this("empty for now", null, null);
    }

    public int resumeDialogeFrom() {

        int startLength = 0;
        for (Map.Entry<String, Integer> entry : currentCall.lengthsSaved.entrySet()) {
            startLength += entry.getValue();
            Person person = currentCall.sentencesHashMap.get(entry.getKey());
            TextField textField = new TextField(person.getTag() + entry.getKey());
            textField.setEditable(false);
            Platform.runLater(() -> root.getChildren().add(textField));
            if (startLength > currentCall.getTimeElapsed()) {
                startFrom = entry.getKey();
                break;
            }

        }
        return startLength;
    }

    public void exit() {
        stop = true;
    }

    public void startShowing(int startLength) {
        while (currentCall.totalTime < startLength) {
            phaser.arriveAndAwaitAdvance();

        }
        Platform.runLater(() -> stage.show());
    }

    private void pacedPrint(String sentence) {
        Person speaker = currentCall.sentencesHashMap.get(sentence);
        TextField textField = this.createTextField(speaker);
        String[] words = sentence.split("\\s+");

        for (String word : words) {
            if (scrollDown) {
                scrollPane.setVvalue(1.0);
            }
            Test.waiting = true;
            phaser.arriveAndAwaitAdvance();
            if (stop) {
                break;
            }

            Platform.runLater(() -> textField.appendText(word + " "));
        }
    }

    private TextField createTextField(Person person) {
        TextField textField = new TextField(person.getTag());
        textField.setEditable(false);
        int identifier = 0;
        if (person instanceof Agent) {
            textField.setStyle("-fx-alignment: CENTER-RIGHT;");
        } else {
            identifier = 1;
        }
        Platform.runLater(() -> {
            root.getChildren().add(textField);
        });
        return textField;
    }

    @Override
    public void run() {
        int resume = this.resumeDialogeFrom();
        this.startShowing(resume);
        boolean start = false;
        Iterator<String> iterator = currentCall.lengthsSaved.keySet().iterator();

        while (iterator.hasNext() && !stop) {
            String key = iterator.next();
            if (start) {
                pacedPrint(key);
            }
            if (startFrom.equals(key)) {
                start = true;
            }
        }
        Platform.runLater(() -> stage.close());

        phaser.arriveAndDeregister();
        windows.remove(this);
    }

    public void openEmptyWindow(String windowTitle, double x, double y) {
        scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        // try {
        // Image volume = new
        // Image("call_center\\src\\main\\java\\com\\ee364project\\image\\volume.png");
        // ImageView volumeIcon1 = new ImageView(volume);
        // ImageView volumeIcon2 = new ImageView(volume);
        // // Further code using the image
        // } catch (Exception e) {
        // e.getStackTrace();
        // }

        agentVoice = new ProgressBar();
        agentVoice.setRotate(-90);
        VBox agentVolumeBarBox = new VBox(agentVoice);
        agentVoice.setProgress(0);
        customerVoice = new ProgressBar();
        customerVoice.setRotate(-90);
        customerVoice.setProgress(0);
        VBox customerVolumeBarBox = new VBox(customerVoice);

        agentVolumeBarBox.setAlignment(Pos.CENTER);
        customerVolumeBarBox.setAlignment(Pos.CENTER);
        HBox mainPane = new HBox(customerVolumeBarBox, scrollPane, agentVolumeBarBox);
        agentVolumeBarBox.prefWidthProperty().bind(mainPane.widthProperty().divide(5));
        agentVolumeBarBox.prefHeightProperty().bind(mainPane.heightProperty());
        customerVolumeBarBox.prefWidthProperty().bind(mainPane.widthProperty().divide(5));
        customerVolumeBarBox.prefHeightProperty().bind(mainPane.heightProperty());
        scrollPane.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.6));

        scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Check if the scroll pane is at the bottom
                if (scrollDownCheck) {
                    scrollDown = true;
                } else {
                    scrollDown = false;

                }
                if (newValue.doubleValue() == scrollPane.getVmax()) {
                    scrollDownCheck = true;
                } else {
                    scrollDownCheck = false;
                }
            }
        });

        System.out.print(scrollDown);
        stage.setOnCloseRequest(e -> {
            this.exit();
            checkBox.setSelected(false);
        });

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(mainPane, 700, 200);

        mainPane.prefWidthProperty().bind(scene.widthProperty());
        mainPane.prefHeightProperty().bind(scene.heightProperty());
        stage.setScene(scene);

        // Set the title and position of the Stage
        stage.setTitle(windowTitle);
        stage.setX(x);
        stage.setY(y);

        // Show the Stage
        stage.setResizable(false);
    }

    public void closeWindow() {
        stage.hide();
    }

    public void showWindow() {
        stage.show();
    }

}

class ControlProgressBar {
    private ProgressBar progressBar;

    ControlProgressBar() {
        this.progressBar = new ProgressBar();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}

class RandomInt {
    public static int generateRandom(int options) {
        return generateWithinRange(0, options);
    }

    public static int generateWithinRange(int start, int end) {
        int number = start + (int) (Math.random() * (end - start));
        return number;
    }
}

class DummyClass extends Thread {
    private Call call;
    private long endTime;

    public DummyClass(Call call) {
        this.call = call;
        this.endTime = Utilities.random.nextInt(100, 1000) + Timekeeper.getTime();
    }

    public void run() {
        while (this.endTime >= Timekeeper.getTime()) {
            Utilities.log(call, "is", "executing", "ends after " + (this.endTime - Timekeeper.getTime()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.call.terminateCall();
    }
}