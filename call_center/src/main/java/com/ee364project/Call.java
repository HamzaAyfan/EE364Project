package com.ee364project;

import java.util.HashSet;
import java.util.LinkedList;

import com.ee364project.helpers.Utilities;



import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
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
        HashSet<Solution> HSsolutions = caller.problemState.getProblem().solutions;
        LinkedList<Solution> LLsolutions = new LinkedList<>();
        for(Solution soultion:HSsolutions){
            try {
                print(soultion.agentIntro[0]);
                LLsolutions.add((Solution)(soultion.clone()));
            } catch (CloneNotSupportedException e) {
                // To ba handled later
            }         
        }
        SentenceWriterService window = new SentenceWriterService(caller, receiver, this, LLsolutions);
		window.start();

    }
    public void terminateCall(){
        print("done");
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
    private static void print(String string){
    System.out.println(string);
    }
}


class SentenceWriterService extends Service<Void> {
	private LinkedList<Solution> solutions;
	public static int ActiveCallNumber;
	private Call currentCall;
    private Customer caller;
    private Agent receiver;
    private int seekedSolutionNumber; 

    public SentenceWriterService(Customer caller, Agent receiver, Call currentCall, LinkedList<Solution> solutions) {
        this.solutions = solutions;
        this.caller =caller;
        this.receiver = receiver;
        this.currentCall = currentCall;
    }

    @Override
    protected Task<Void> createTask() {
    	
		Object[] window = openEmptyWindow("Call "+ ++ActiveCallNumber,100,100);
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                int i = 0;
                do {
                    int length = solutions.size();
                    switch(receiver.getlevel()) {
				        case SAVEY:
					        i=0;
					        break;
				        case CHALLENGED:
					        i=RandomInt.generateWithinRange(length/2,length);
					        break;					
				        default:
					        i=RandomInt.generateWithinRange(0,length/2);				
			        }
                    Solution selectedSolution = solutions.get(i);
                    solutions.remove(i);
                    VBox root = (VBox)window[1];

                    if (seekedSolutionNumber == 0){
                            TextArea agentGreets = createTextArea();
                            agentGreets.setStyle("-fx-alignment: CENTER-RIGHT;");
                            Platform.runLater(() -> root.getChildren().add(agentGreets));
                            pacedPrint("Agent: ", selectedSolution.agentIntro[0], agentGreets);
                            TextField customerGivesProblem = createTextField();
                            Platform.runLater(() -> root.getChildren().add(customerGivesProblem));
                            pacedPrint("Customer: ", selectedSolution.customerIntro[0], customerGivesProblem);                            
                        }

                    for (int j = 0; j<selectedSolution.agentResponses.length;j++){
                        
                        TextField agentTextField = createTextField();
                        agentTextField.setStyle("-fx-alignment: CENTER-RIGHT;");
                        Platform.runLater(() -> root.getChildren().add(agentTextField));
                        pacedPrint("Agent: ", selectedSolution.agentResponses[j], agentTextField);
                        TextField customerTextField = createTextField();
                        Platform.runLater(() -> root.getChildren().add(customerTextField));
                        pacedPrint("Customer: ", selectedSolution.customerResponses[j], customerTextField);
                    }               
		        }while(i != 0);	

                currentCall.terminateCall();// method after ending the call
                Stage stage = (Stage)window[0];
                ActiveCallNumber--;
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
    private TextArea createTextArea() {
        TextArea textArea = new TextArea("");
        textArea.setEditable(false);
        textArea.setPrefRowCount(1);
        return textArea;
    }
    private void pacedPrint(String speakerID, String sentence,TextField textField){
        String[] words = sentence.split("\\s+");
        Platform.runLater(() -> textField.appendText(speakerID));
        for(String word:words){            
            Platform.runLater(() -> textField.appendText(word + " "));
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // To be added later
            }
        }
    }
    private void pacedPrint(String speakerID, String sentence,TextArea textaArea){
        String[] words = sentence.split("\\s+");
        Platform.runLater(() -> textaArea.appendText(speakerID));
        for(String word:words){            
            Platform.runLater(() -> textaArea.appendText(word + " "));
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // To be added later
            }
        }
    }
    private static void print(String string){
        System.out.println(string);
    }
}

class RandomInt {
	public static int generateRandom(int options) {
		return generateWithinRange(0, options);
	}
	public static int generateWithinRange(int start, int end) {
		int number = start + (int) (Math.random()*(end-start));
		return number;
	}
}