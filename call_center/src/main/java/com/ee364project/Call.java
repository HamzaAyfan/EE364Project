package com.ee364project;

import java.util.ArrayList;
import java.util.HashMap;
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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.concurrent.Phaser;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.ee364project.Fx.MainSceneController;
import com.ee364project.helpers.Utilities;
import java.util.concurrent.Phaser;

public class Call implements Simulated {
    //public static LinkedList<Call> activeCalls = new LinkedList<>();
    private CheckBox checkBox;
    public static VBox vBox;
    public static HashMap<CheckBox,Call> CheckBoxAndCall =new HashMap<CheckBox,Call>();

    public HBox hbox;

    public static final long MAXWAITTIME = 60;

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public static enum CallState {
        WAITING,
        INCALL,
        ENDED,
        EXIRED
    }

    public static LinkedList<Call> activeCalls = new LinkedList<>();
    private static LinkedList<Call> callQueue = new LinkedList<>();
    private static LinkedList<Call> callsToRemove = new LinkedList<>();
    LinkedHashMap<String, Person> sentencesHashMap = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> lengthsSaved = new LinkedHashMap<>();
    private static long callCount = 0;
    public static Phaser phaser;
    public static HashMap<CheckBox,DialogeBox> linkCBtoDB = new HashMap<>();

    private int startTime;//
    private int endTime;//
    private int answerTime;//
    public int totalTime;
    private Customer caller;
    private Agent receiver;
    private CallState state;
    private CallCenter callCenter;
    public static boolean newThreadAdded;
    Timeline callTime = new Timeline();

    public static Call getACall() {
        //applyExpiry();
        Call call = callQueue.poll();
        if (call != null) {
            call.state = CallState.INCALL;
        }
        return call;
    }

    public static void endACall(Call call) {
        call.state = CallState.ENDED;
    }

    // public void increaseTime() {
    //     timeela
    // }

    public int getTimeElapsed() {
        return (Timekeeper.getTime()-startTime);
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
        totalTime = dialoge.getContentlength();
        this.answerTime = Timekeeper.getTime();
        this.endTime = this.answerTime + totalTime;
        activeCalls.add(this);
        MainSceneController msc = new MainSceneController();
            Node[] nodes = msc.createHbox();
            hbox = (HBox)nodes[0];
            checkBox = (CheckBox)nodes[1];
        Platform.runLater(()->{ 
            vBox.getChildren().add(hbox);
            });
            CheckBoxAndCall.put(checkBox, this);

    }

    public static void terminateCalls() {
        for (Call call : Call.callsToRemove) {
            call.callCenter.releaseAgent(call.receiver);
            //int call_index = indexOf(activeCalls, this);
            int index = activeCalls.indexOf(call);
            Call.activeCalls.remove(call);
        
        try{
            if (index >= 0 && vBox != null && vBox.getChildren().size() > 0){
            Platform.runLater(() -> {
            vBox.getChildren().remove(index);});
            }
        }catch(Exception e){

        }
        Utilities.log(call, "ended", "", "");
        call.caller.problemState.solve();
        call.endTime = Timekeeper.getTime();
            }
            callsToRemove.clear();
        }
        
        // this.state = Call.CallState.ENDED;
 
    private static <T> int indexOf(LinkedList<T> list, T target) {
        int index = 0;
        for (T element : list) {
            if (element.equals(target)) {
                return index;
            }
            index++;
        }
        return -1; // Element not found
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
                this.state = Call.CallState.ENDED;  
                callsToRemove.add(this);              
            } else {
                Utilities.log(this, "continues", activeCalls, (this.endTime - Timekeeper.getTime()) + " remaining...");
            }
        }
        applyExpiry();
    }

    public CheckBox getCheckBox() {
        return checkBox;
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
        // this.call.terminateCall();
    }
}