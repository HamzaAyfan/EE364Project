package com.ee364project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.animation.Timeline;
import javafx.application.Platform;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.concurrent.Phaser;

import com.ee364project.Customer.CustomerState;
import com.ee364project.Fx.MainSceneController;
import com.ee364project.helpers.Utilities;

public class Call implements Simulated {
    // public static LinkedList<Call> activeCalls = new LinkedList<>();
    private CheckBox checkBox;
    public static VBox vBox;
    public static HashMap<CheckBox, Call> CheckBoxAndCall = new HashMap<CheckBox, Call>();

    public HBox hbox;

    

    public static final long MAXWAITTIME = 1000000;

    public static enum CallState {
        WAITING,
        INCALL,
        ENDED,
        EXIRED
    }

    public static LinkedList<Call> activeCalls = new LinkedList<>();
    private static LinkedList<Call> callQueue = new LinkedList<>();
    private static LinkedList<Call> callsToRemove = new LinkedList<>();

    private ArrayList<String> sentences = new ArrayList<>();
    private ArrayList<Integer> lengthsSaved = new ArrayList<>();
    private ArrayList<Person> speaker = new ArrayList<>();
    
    public static long callCount;
    public static Phaser phaser;
    public static HashMap<CheckBox, DialogeBox> linkCBtoDB = new HashMap<>();

    private int startTime;
    private int endTime;
    private int answerTime;
    public int totalTime;
    private Customer caller;
    private Agent receiver;
    private CallState state;
    private CallCenter callCenter;
    public static boolean newThreadAdded;
    Timeline callTime = new Timeline();
    public Text callNumber;
    // Text callNumber;



    
    public static Call getACall() {
        // applyExpiry();
        Call call = callQueue.poll();
        if (call != null) {
            call.caller.setState(CustomerState.INCALL);
            call.state = CallState.INCALL;
        }
        return call;
    }

    public int getTimeElapsed() {
        return (Timekeeper.getTime() - startTime);
    }

    public LinkedList<Solution> makeLinkedList(Customer caller) {
        ArrayList<Solution> HSsolutions = caller.getProblemInfo().getLastProblem().solutions;
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
        System.out.println("Call CONNECTED: ");
        Call.callQueue.remove(this);
        receiver.assignLevel(caller.getProblemInfo().getLastProblem());
        this.callCenter = callCenter;
        LinkedList<Solution> solutionsCopy = this.makeLinkedList(caller);
        MockDialoge dialoge = new MockDialoge(caller, receiver, this, solutionsCopy);
        totalTime = dialoge.getContentlength();
        this.answerTime = Timekeeper.getTime();
        this.endTime = this.answerTime + totalTime;
        activeCalls.add(this);
    }

    public void addLine(String sentenceString, Person person, int length){
        sentences.add(sentenceString);
        speaker.add(person);
        lengthsSaved.add(length);
    }


    public void terminateCall() {
        this.state = CallState.ENDED;
        this.caller.callInfo.endCall();
        this.callCenter.releaseAgent(this.receiver);
        Call.activeCalls.remove(this);
        Platform.runLater(() -> {        
                    vBox.getChildren().remove(this.hbox);
                });
        CheckBoxAndCall.remove(this.checkBox);            
        this.caller.getProblemInfo().solve();
        this.caller.setState(CustomerState.IDLE);
        this.endTime = Timekeeper.getTime();
    }
    
    public static void terminateCalls() {
        for (Call call : Call.callsToRemove) {
            call.terminateCall();
            
        }
        System.gc();
        callsToRemove.clear();
    }

    private static void applyExpiry() { 
        Call call;
        while (callQueue.size() > 0 && (Timekeeper.getTime() - callQueue.peek().startTime >= MAXWAITTIME)) {
            call = callQueue.poll();
            call.state = CallState.EXIRED;
        }
    }

    public Call(Customer caller) {
        callCount++;
        this.caller = caller;
        this.startTime = Timekeeper.getTime();
        this.answerTime = 0;
        this.endTime = 0;
        this.caller = caller;
        this.receiver = null;
        this.state = CallState.WAITING;
        callQueue.add(this);
        MainSceneController msc = new MainSceneController();
        Node[] nodes = msc.createHbox();
        hbox = (HBox) nodes[0];
        checkBox = (CheckBox) nodes[1];
        callNumber = (Text) nodes[2];
        int randomIndex = (int) (Math.random() * (vBox.getChildren().size() + 1));
        Platform.runLater(() -> {
            vBox.getChildren().add(randomIndex,hbox);
        });
        CheckBoxAndCall.put(checkBox, this);
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
        if (this.state != CallState.ENDED) {
            return Timekeeper.getTime() - this.startTime;
        }
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
                this.state = Call.CallState.ENDED;
                callsToRemove.add(this);
                System.out.println("Adding to removed list: ");
            } 
        }
        applyExpiry();
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public ArrayList<Integer> getLengths() {
        return lengthsSaved;
    }

    public ArrayList<String> getSentences() {
        return sentences;
    }
    public String getSentences(int i) {
        return sentences.get(i);
    }

    public int getlengths(int i) {
        return lengthsSaved.get(i);
    }

    public Person getPerson(int i) {
        return speaker.get(i);
    }
}

class MockDialoge {
    LinkedList<Solution> solutions = new LinkedList<>();
    public static int ActiveCallNumber;
    private Call currentCall;
    private Customer caller;
    private Agent receiver;
    private boolean firstSolutionSeeked = true;

    private int discussionLength;

    public MockDialoge(Customer caller, Agent receiver, Call currentCall, LinkedList<Solution> solutions) {
        this.solutions = solutions;
        this.caller = caller;
        this.receiver = receiver;
        this.currentCall = currentCall;
    }

    public int getContentlength() {        
        this.getWords();
        for (Integer length : currentCall.getLengths()) {
            discussionLength += length;
        }
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
        for (int j = 0; j < selectedSolution.getAgentResponse().length; j++) {
            currentCall.addLine(selectedSolution.getAgentResponse()[j], receiver, MockDialoge.getlength(selectedSolution.getAgentResponse()[j]));
            currentCall.addLine(selectedSolution.getCustomerResponse()[j], caller, MockDialoge.getlength(selectedSolution.getCustomerResponse()[j]));
        }
    }

    private static int getlength(String string) {
        return string.split("\\s+").length;
    }

    private void introOrTransition(Solution selectedSolution) {
        if (firstSolutionSeeked == true) {
            currentCall.addLine(selectedSolution.getRandomIntro(receiver), receiver, MockDialoge.getlength(selectedSolution.getRandomIntro(receiver)));
            currentCall.addLine(selectedSolution.getRandomIntro(caller), caller, MockDialoge.getlength(selectedSolution.getRandomIntro(caller)));
            firstSolutionSeeked = false;
        } else {
            currentCall.addLine("I just did that but it did not work", caller, 9);
            currentCall.addLine("sorry it did not work let me seek an alternative", receiver, 10);
        }
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

// class DummyClass extends Thread {
//     private Call call;
//     private long endTime;

//     public DummyClass(Call call) {
//         this.call = call;
//         this.endTime = Utilities.random.nextInt(100, 1000) + Timekeeper.getTime();
//     }

//     public void run() {
//         while (this.endTime >= Timekeeper.getTime()) {
//             Utilities.log(call, "is", "executing", "ends after " + (this.endTime - Timekeeper.getTime()));
//             try {
//                 Thread.sleep(1000);
//             } catch (InterruptedException e) {
//                 // TODO Auto-generated catch block
//                 e.printStackTrace();
//             }
//         }
//         // this.call.terminateCall();
//     }
// }