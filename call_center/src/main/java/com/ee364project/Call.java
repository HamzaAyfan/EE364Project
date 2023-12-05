package com.ee364project;

import java.text.BreakIterator;
import java.util.HashSet;
import java.util.LinkedList;
import javafx.scene.input.KeyCode;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import com.ee364project.helpers.Utilities;

public class Call {
    public static final long MAXWAITTIME = 60;

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    public static enum CallState {
        WAITING,
        INCALL,
        ENDED,
        EXIRED
    }

    private static LinkedList<Call> callQueue = new LinkedList<>();
    private static LinkedList<Call> calls = new LinkedList<>();
    private static long callCount = 0;

    private long startTime;
    private long answerTime;
    private long callDuration;
    private long waitTime;
    private Customer caller;
    private Agent receiver;
    private CallState state;
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
    
    public void connectCall(Customer caller, Agent receiver){
        HashSet<Solution> HSsolutions = caller.problemState.getProblem().solutions;
        LinkedList<Solution> LLsolutions = new LinkedList<>();
        for(Solution soultion:HSsolutions){
            try {
                LLsolutions.add((Solution)(soultion.clone()));
            } catch (CloneNotSupportedException e) {
                // To be handled later
            }         
        }        
        try{
            // Runnable dialoge = DialogeBox.windows.poll();
            // ((DialogeBox)dialoge).setupCall(caller, receiver, this, LLsolutions);
            // // dialoge.start(); 
            // executor.execute(dialoge);
        }catch (NullPointerException e ){            
            MockDialoge dialoge = new MockDialoge(caller, receiver, this, LLsolutions);
            int numberOfWords = dialoge.getContentlength();
            int time = numberOfWords*1;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(time), run -> {terminateCall();} );
            callTime.getKeyFrames().add(keyFrame);
            // callTime = new Timeline(new KeyFrame(Duration.millis(time), run -> {terminateCall();} ));
            callTime.setCycleCount(1);
            callTime.play();
            
        }catch (Exception e){System.out.println("error");}
        
    }

    // public static void closeExecutor(){
    //     executor.shutdown();
    //     DialogeBox.closeExecutor();
    //     }

    public synchronized void terminateCall(){
        System.out.println(" done");
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
        this.callDuration = 0;
        this.waitTime = 0;
        this.caller = caller;
        this.receiver = null;
        this.state = CallState.WAITING;
        callQueue.add(this);
        calls.add(this);
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
    private static void print(String string){
    System.out.println(string);
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

        public MockDialoge(Customer caller, Agent receiver, Call currentCall, LinkedList<Solution> solutions) {
            this.solutions = solutions;
            this.caller =caller;
            this.receiver = receiver;
            this.currentCall = currentCall;
        }
        public int getContentlength(){
            getWords();
            return content.size();
        }

        private void pacedPrint(int ID, String sentence){    
        String[] words = sentence.split("\\s+");    
        for(String word:words){   
            content.add(word);
        }   
    }

        private void getWords(){
            int i = 0;
                do {
                    int length = solutions.size();//print(length + " from call " + z + " agent is " + receiver.getlevel());
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
                    if (firstSolutionSeeked == true){                             
                            pacedPrint(0, selectedSolution.agentIntro[0]);                            
                            pacedPrint(1, selectedSolution.customerIntro[0]);   
                            firstSolutionSeeked = false;
                        }else{                            
                            pacedPrint(1, "I just did that but it did not work");                         
                            pacedPrint(0, "sorry it did not work let me seek an alternative");                           
                        }

                    for (int j = 0; j<selectedSolution.agentResponses.length;j++){                        
                        pacedPrint(0, selectedSolution.agentResponses[j]);                        
                        pacedPrint(1, selectedSolution.customerResponses[j]);
                    }               
		        }while(i != 0);	      
            }
}

class DialogeBox extends Thread{
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
        private boolean firstSolutionSeeked = true; 
        ProgressBar customerVoice;
        ProgressBar agentVoice;
        String customerTag = "Customer: ";
        String agentTag = "Agent: ";
        Timeline timeline;
        LinkedList<String> content = new LinkedList<>();
        KeyFrame kfTalking;
        KeyFrame kfTransitionalPause;
        KeyFrame kfStopTalking;
        ScrollPane scrollPane;
        boolean scrollDown=true;
        boolean scrollDownCheck=true;
        int index;
        AtomicBoolean state;
        CyclicBarrier[] cyclicBarrier;
        public static int numberOfThreads = 1;
        Phaser phaser;
        private boolean stop;

        public DialogeBox(String callNumber, Phaser phaser){
            if (DialogeBox.windows.size()<=4){
                // this.state=state;
                // ++Test.threadsNumber;
                // this.cyclicBarrier=cyclicBarrier;
                // cyclicBarrier[1]=cyclicBarrier[0];
                // cyclicBarrier[0]=new CyclicBarrier(++numberOfThreads);
                this.phaser=phaser; 
                phaser.register();
                
                windows.add(this);
                this.openEmptyWindow(callNumber,500,200);
                index = windows.indexOf(this);                
            }else{
                System.out.println("You can view a maximuim of 5 calls at a time");
            }
        }
        public DialogeBox(){
            this("empty for now",null);
        }
        public void exit(){
            stop=true;
            
        }

        public void setupCall(Call currentCall){
            this.currentCall = currentCall;
            this.caller = currentCall.getCaller();
            this.receiver = currentCall.getReceiver();
            
            HashSet<Solution> HSsolutions = caller.problemState.getProblem().solutions;
            LinkedList<Solution> LLsolutions = new LinkedList<>();
            for(Solution soultion:HSsolutions){
            try {
                LLsolutions.add((Solution)(soultion.clone()));
            } catch (CloneNotSupportedException e) {
                // To be handled later
            }         
        }   
            this.solutions = LLsolutions;
            
        }

        @Override
        public void run(){            
            int i = 0;
                do {
                    int length = solutions.size();//print(length + " from call " + z + " agent is " + receiver.getlevel());
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
                    if (firstSolutionSeeked == true){
                            this.createTextField(receiver,selectedSolution.getRandomIntro(receiver),agentTag);
                            this.createTextField(caller,selectedSolution.getRandomIntro(caller),customerTag);           
                            firstSolutionSeeked = false;
                        }else{
                            this.createTextField(caller,"I just did that but it did not work",customerTag); 
                            this.createTextField(receiver,"sorry it did not work let me seek an alternative",agentTag);                            
                        }

                    for (int j = 0; j<selectedSolution.agentResponses.length;j++){ 
                        this.createTextField(receiver,selectedSolution.agentResponses[j],agentTag);
                        this.createTextField(caller,selectedSolution.customerResponses[j],customerTag);                                    
                    }  
                                
		        }while(i != 0 && !stop);	                
                ActiveCallNumber--;
                Platform.runLater(() -> stage.close());
                Platform.runLater(() -> root.getChildren().clear());
                phaser.arriveAndDeregister();
                windows.remove(this);              
            }

        private void createTextField(Person person,String sentence, String tag) {
            TextField textField = new TextField(tag);
            textField.setEditable(false);
            int identifier = 0;
            if (person instanceof Agent){
                textField.setStyle("-fx-alignment: CENTER-RIGHT;");                            
            }else{
                identifier=1;
            }
            Platform.runLater(() -> {root.getChildren().add(textField); });
            
            pacedPrint(identifier, sentence, textField);        
        }
            
    
    private void pacedPrint(int ID, String sentence,TextField textField){
        // ProgressBar selectedVoice = selectVoice(ID);
        
        String[] words = sentence.split("\\s+");
        
        
        for(String word:words){ 
            if (scrollDown){scrollPane.setVvalue(1.0);}          
                // while(cyclicBarrier[0].await())
                Test.waiting =true;
                phaser.arriveAndAwaitAdvance();
                if(stop){break;} 
            // executor.submit(()->this.runProgressBar(selectedVoice)); 
            // this.playFromKeyframe(kfTalking);   
            // this.playFromKeyframe(kfTransitionalPause);
                Platform.runLater(() -> textField.appendText(word + " "));            
        }
        
        // this.playFromKeyframe(kfStopTalking);  
    }
    

    public void openEmptyWindow(String windowTitle, double x, double y) {
		scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        // try {
        // Image volume = new Image("call_center\\src\\main\\java\\com\\ee364project\\image\\volume.png");
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
        HBox mainPane = new HBox(customerVolumeBarBox,scrollPane,agentVolumeBarBox); 
        agentVolumeBarBox.prefWidthProperty().bind(mainPane.widthProperty().divide(5));  
        agentVolumeBarBox.prefHeightProperty().bind(mainPane.heightProperty());  
        customerVolumeBarBox.prefWidthProperty().bind(mainPane.widthProperty().divide(5));  
        customerVolumeBarBox.prefHeightProperty().bind(mainPane.heightProperty());  
        scrollPane.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.6));

        scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {                
                // Check if the scroll pane is at the bottom
                if (scrollDownCheck){
                    scrollDown = true;
                }else{
                    scrollDown=false;

                }
                if (newValue.doubleValue() == scrollPane.getVmax()) {
                    scrollDownCheck=true;
                } else {
                    scrollDownCheck=false;               
                }}});
                
                System.out.print(scrollDown);
            

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
        stage.show();
        
    }
        
}

class ControlProgressBar{
    private ProgressBar progressBar;
    ControlProgressBar(){
        this.progressBar = new ProgressBar();
    }
    public ProgressBar getProgressBar(){
        return progressBar;
    }
    
    // private void runProgressBar(ProgressBar selectedProgressBar) {
    //     kfTalking = new KeyFrame(Duration.seconds(0.1), e -> Platform.runLater(()->{selectedProgressBar.setProgress(0);}));
    //     kfTransitionalPause = new KeyFrame(Duration.seconds(0.1), e -> decreaseProgressBar(selectedProgressBar));
    //     kfStopTalking = new KeyFrame(Duration.seconds(0.1), e -> Platform.runLater(()->{selectedProgressBar.setProgress(0);}));


    //     timeline = new Timeline(kfTalking,kfTransitionalPause,kfStopTalking);
    //     timeline.setCycleCount(Animation.INDEFINITE);
    //     timeline.play();
    // }

    // private void decreaseProgressBar(ProgressBar selectedProgressBar) {
    //     double currentProgress = selectedProgressBar.getProgress();

    //     double newProgress = currentProgress - 0.01;
    //     Platform.runLater(()->selectedProgressBar.setProgress(newProgress));  
                   
    // }
    // private void stabalizeProgressBar(int value){
    //     Platform.runLater(()->selectedProgressBar.setProgress(newProgress));
    // }
    // private void playFromKeyframe(KeyFrame keyFrame) {

    //     timeline.stop();
    //     timeline.playFrom(keyFrame.getTime());
    // }

    // private ProgressBar selectVoice(int ID){
    //     if (ID==0){
    //         return agentVoice;
    //     }else{
    //         return customerVoice;
    //     }
    // }
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
