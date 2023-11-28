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
    public static enum CallState {
        WAITING,
        INCALL,
        ENDED,
        EXIRED
    }
    public static final long MAXWAITTIME = 60;
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
        call.state = CallState.INCALL;
        return call;
    }

    public static void endACall(Call call) {
        call.state = CallState.ENDED;
    }

    public void connectCall(Customer caller, Agent receiver){
        SentenceWriterService window = new SentenceWriterService(caller, receiver);
		window.start();

    }
    public static void terminateCall(){

    }

    private static void applyExpiry() {  // run after every step.
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
}


class SentenceWriterService extends Service<Void> {
	private LinkedList<String[]> sentences;
	public static int counter;
	public Call call;

    public SentenceWriterService(Customer caller, Agent receiver) {
        sentences = new LinkedList<>();
    }

    @Override
    protected Task<Void> createTask() {
    	
		Object[] window = openEmptyWindow("Call "+ ++counter,100,100);
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (String[] sentence : sentences) {       
                	TextField textField = createTextField();
                	VBox root = (VBox)window[1];                	
                    textField.setEditable(false);
                    
                    Platform.runLater(() -> root.getChildren().add(textField));
                                       
                    for (String word : sentence) {
                        // Update UI on JavaFX Application Thread
                        Platform.runLater(() -> textField.appendText(word + " "));
                        // Sleep for 500 milliseconds
                        Thread.sleep(500);
                    }    
                }
                Call.terminateCall();// method after ending the call
                Stage stage = (Stage)window[0];
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
        Object[] pointers = {stage, root};
        return pointers;
    }
    private TextField createTextField() {
        TextField textField = new TextField("");
        textField.setEditable(false);
        return textField;
    }
}
