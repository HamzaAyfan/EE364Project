package com.ee364project;

import java.util.LinkedList;

import com.ee364project.helpers.Utilities;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Call {
    public static final long MAXWAITTIME = 60;

    public static enum CallState {
        WAITING,
        INCALL,
        ENDED,
        EXIRED
    }

    private static LinkedList<Call> callQueue = new LinkedList<>();
    private static long callCount = 0;

    private long startTime;
    private long answerTime;
    private long callDuration;
    private long waitTime;
    private Customer caller;
    private Agent receiver;
    private CallState state;

    public static Call getACall() {
        applyExpiry();
        Call call = callQueue.poll();
        if (call != null) {
            call.state = CallState.INCALL;
        }
        return call;
    }

    public void endCall() {
        this.state = CallState.ENDED;
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
        this.startTime = Timekeeper.getTime();
        this.answerTime = 0;
        this.callDuration = 0;
        this.waitTime = 0;
        this.caller = caller;
        this.receiver = null;
        this.state = CallState.WAITING;
        callQueue.add(this);
    }

    public long getCallDuration() {
        return this.callDuration;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getAnswerTime() {
        return this.answerTime;
    }

    public long getWaitTime() {
        return this.waitTime;
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
        return Utilities.prettyToString("Call" + callCount, this.caller, this.callDuration);
    }

    static void step() {
        applyExpiry();
    }

    public void startCall(CallCenter callCenter) {
        new CallSession(this, callCenter).start();
    }
}

class CallSession extends Thread {
    private Call call;
    private CallCenter callCenter;

    public CallSession(Call call, CallCenter callCenter) {
        this.call = call;
        this.callCenter = callCenter;
    }

    @Override
    public void run() {
        try {
                        Solution solution = call.getCaller().problemState.getProblem().getRandomSolution();
                        String[] cR = solution.customerResponses;
            String[] aR = solution.agentResponses;
            int n = Math.min(cR.length, aR.length);
            for (int i = 0; i < n; i++) {
                System.out.print("\n\nC(" + this.call.getCaller().getName() + "): ");
                for (String word : cR[i].split(" ")) {
                    System.out.print(word + " ");
                    Thread.sleep(10 * Utilities.random.nextInt(1, 6));
                }
                Thread.sleep(10 * Utilities.random.nextInt(1, 11));
                System.out.print("\n\nA(" + this.call.getReceiver().getName() + "): ");
                for (String word : aR[i].split(" ")) {
                    System.out.print(word + " ");
                    Thread.sleep(10 * Utilities.random.nextInt(1, 6));
                }
                Thread.sleep(100 * Utilities.random.nextInt(1, 11));
            }
            System.out.println("\nDone Call...\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        call.endCall();
        this.callCenter.releaseAgent(call.getReceiver());
    }
}

class SentenceWriterService extends Service<Void> {
    private LinkedList<String[]> sentences;
    public static int counter;
    public Call call;

    public SentenceWriterService(Call call) {
        this.call = call;
        sentences = new LinkedList<>();
    }

    @Override
    protected Task<Void> createTask() {

        Object[] window = openEmptyWindow("Call " + ++counter, 100, 100);
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (String[] sentence : sentences) {
                    TextField textField = createTextField();
                    VBox root = (VBox) window[1];
                    textField.setEditable(false);

                    Platform.runLater(() -> root.getChildren().add(textField));

                    for (String word : sentence) {
                        // Update UI on JavaFX Application Thread
                        Platform.runLater(() -> textField.appendText(word + " "));
                        // Sleep for 500 milliseconds
                        Thread.sleep(500);
                    }
                }
                Stage stage = (Stage) window[0];
                Platform.runLater(() -> stage.close());

                return null;
            }
        };
    }

    private Object[] openEmptyWindow(String windowTitle, double x, double y) {
        VBox root = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Stage stage = new Stage();
        // Create a Scene and set it on the Stage
        Scene scene = new Scene(scrollPane, 500, 200);
        stage.setScene(scene);

        // Set the title and position of the Stage
        stage.setTitle(windowTitle);
        stage.setX(x);
        stage.setY(y);

        // Show the Stage
        stage.show();
        Object[] pointers = { stage, root };
        return pointers;
    }

    private TextField createTextField() {
        TextField textField = new TextField("");
        textField.setEditable(false);
        return textField;
    }
}
