package com.ee364project;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.ee364project.exceptions.InvalidIdException;
import com.ee364project.exceptions.InvalidPhoneNumberException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.w3c.dom.css.Counter;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Test extends Application {
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    Button btPause = new Button("pause");
    Button btPlay = new Button("Play");
    LinkedList<Customer> customers = new LinkedList<Customer>();
	LinkedList<Agent> agents = new LinkedList<Agent>();
	LinkedList<Call> calls = new LinkedList<Call>();  
    AtomicBoolean state = new AtomicBoolean(false);
    CountDownLatch latch = new CountDownLatch(1);
    static Integer threadsNumber = 1;
    static int numberOfThreads=1;
    public static boolean waiting = false;
    CyclicBarrier[] cyclicBarrier= {new CyclicBarrier(threadsNumber),null};  
    static VBox vbox;
    int checkboxCount;
    Phaser phaser = new Phaser(0);
    public static boolean newThreadAdded;
    private boolean running = true;
    private Thread pausePlay = new Thread();;
    
    public static HashMap<CheckBox,Call> linkCBtoCall = new HashMap<CheckBox,Call>();
    public static HashMap<CheckBox,DialogeBox> linkCBtoDB = new HashMap<>();
    boolean endThread ;


    public void start(Stage primaryStage) {	
        primaryStage.setOnCloseRequest(e -> {running = false;});
        vbox = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(vbox);

        vbox.getChildren().add(btPause);
        vbox.getChildren().add(btPlay);
        
        btPause.setOnAction(e -> {this.pause();});
        btPlay.setDisable(true);
        

        Scene scene = new Scene(scrollPane,200,300);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Mainpage");
        primaryStage.show();


		for (int i = 0;i<20;i++){
            try {
                Customer customer = new Customer();
                customer.shuffle();
                System.out.println(customer);
                customers.add(customer);
                Agent agent = new Agent();
                agent.shuffle();
                agents.add(agent);
            } catch (InvalidPhoneNumberException e) {
                e.printStackTrace();
            } catch (InvalidIdException e) {
                e.printStackTrace();
            }          
        }


		for (int i = 0;i<10;i++) {
            
			customers.get(i).problemState.acquireProblem();
            customers.get(i).problemState.getProblem().shuffle();
            Call call = new Call(customers.get(i));
            calls.add(call);
            call.setReciever(agents.get(i));
            call.connectCall();

            CheckBox checkBox = this.ui_addCall("Call " + i);
            linkCBtoCall.put(checkBox, call);
        }
        
        new Thread(()->{
            phaser.register();
        while(running){  
             System.out.println("no error");          
            try {                
                Thread.sleep(100);                       

                phaser.arriveAndAwaitAdvance();
            } catch (InterruptedException e) {  
                e.printStackTrace();
            }                                 
            for (Call call:Call.Activecalls){
                if (call == null){
                    continue;
                }
                call.increaseTime();
            }
        }    }).start();
    }

    public void pause() {
        btPause.setDisable(true);
        btPlay.setDisable(false);
        pausePlay = new Thread(()->{
            phaser.register();
            btPlay.setOnAction(e -> {phaser.arriveAndDeregister();endThread=true;btPause.setDisable(false);btPlay.setDisable(true);});
            while(!endThread){}endThread=false;});  
            pausePlay.start();       
    }
    
    public static void main(String[] args) {
        launch(args);
    }  
    public void resetNumberOfThreads(){
        numberOfThreads = executor.getPoolSize();
    }

    public CheckBox ui_addCall(String callNumber){
        CheckBox checkbox = new CheckBox(callNumber);
        vbox.getChildren().add(checkbox);
        checkbox.setOnAction(e -> handleCheckboxAction(callNumber, checkbox));
        return checkbox;
    }
    private void handleCheckboxAction(String callNumber,CheckBox checkbox) { 
        int checkedCount = 0;

        // Count the number of checked checkboxes
        for (int i = 2; i < vbox.getChildren().size(); i++) {
            CheckBox currentCheckBox = (CheckBox) vbox.getChildren().get(i);
            if (currentCheckBox.isSelected()) {
                checkedCount++;
            }
        }

        // If more than the allowed checkboxes are checked, uncheck the current checkbox
        if (checkedCount > 3) {
            checkbox.setSelected(false);
        }       
        if (checkbox.isSelected()) { 
            if(linkCBtoDB.containsKey(checkbox)){if (!endThread){linkCBtoDB.get(checkbox).showWindow();return;}}
            newThreadAdded = true;
            Runnable dialoge = new DialogeBox(callNumber,phaser,checkbox);
            linkCBtoDB.put(checkbox,(DialogeBox)dialoge);
            
            Call call = linkCBtoCall.get(checkbox);
            // ((DialogeBox)dialoge).setupCall(call);
            System.out.println(call.getCaller());//.problemState.getProblem().solutions
            executor.execute(dialoge);
            System.out.println("Selected");
            } else {
                if (!endThread){linkCBtoDB.get(checkbox).closeWindow();return;}
                try{linkCBtoDB.get(checkbox).exit();}catch(NullPointerException e){}
                
            }            
        } 
}

