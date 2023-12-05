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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.ee364project.helpers.Utilities.*;

public class Test extends Application {
    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    LinkedList<Customer> customers = new LinkedList<Customer>();
	LinkedList<Agent> agents = new LinkedList<Agent>();
	LinkedList<Call> calls = new LinkedList<Call>();  
    AtomicBoolean state = new AtomicBoolean(false);
    CountDownLatch latch = new CountDownLatch(1);
    Counter sharedCounter = new Counter(executor.getPoolSize());
    static Integer threadsNumber = 1;
    static int numberOfThreads=1;
    public static boolean waiting = false;
    // CyclicBarrier[] cyclicBarrier= {new CyclicBarrier(1,sharedCounter::reset)};
    CyclicBarrier[] cyclicBarrier= {new CyclicBarrier(threadsNumber),null};  
    VBox vbox;
    int checkboxCount;
    Phaser phaser = new Phaser(0);
    public static boolean newThreadAdded;
    
    public static HashMap<CheckBox,Call> linkCBtoCall = new HashMap<CheckBox,Call>();
    public static HashMap<CheckBox,DialogeBox> linkCBtoDB = new HashMap<>();


    public void start(Stage primaryStage) {	
        vbox = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(vbox);

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
        // for (int i =1;i<=5;i++){
        //     new Thread(()->{while(true){try {
        //         cyclicBarrier.await();
        //     } catch (InterruptedException e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     } catch (BrokenBarrierException e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }}}
        // //     );

        // }
        // for (int i =1;i<=2;i++){
        //     DialogeBox dialoge = new DialogeBox();
        //     dialoge.openEmptyWindow("Call "+i,500,200);            
        // }
        
        // new Thread(()->{
		for (int i = 0;i<20;i++) {
            
			customers.get(i).problemState.acquireProblem();
            customers.get(i).problemState.getProblem().shuffle();
            Call call = new Call(customers.get(i));
            calls.add(call);
            call.setReciever(agents.get(i));
            // System.out.println(call.getCaller());
            // System.out.println(customers.get(i));
            // calls.get(i).connectCall(customers.get(i), agents.get(i));
            CheckBox checkBox =this.ui_addCall("Call " + i) ;
            linkCBtoCall.put(checkBox, call);
        }
        // cyclicBarrier[0]= new CyclicBarrier(1);
        new Thread(()->{
            phaser.register();
        while(true){  
            // latch = new CountDownLatch(1);
             System.out.println("no error");          
            try {
                
                Thread.sleep(10);
                
                // cyclicBarrier.wait();
                // this.signalToThreads();
                phaser.arriveAndAwaitAdvance();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            
           
            // state.set(true);
            // state.set(false);                         
        }    }
        ).start();
       
            
		// }).start();

	}
    // public void signalToThreads(){        
    //             try {
    //                 if (newThreadAdded){
    //                     // if (!waiting)
    //                     // {}
    //                     // else if (cyclicBarrier[1]!=null)
    //                     {cyclicBarrier[1].await();}
    //                     newThreadAdded = false;}
    //                 cyclicBarrier[0].await();
    //             } catch (InterruptedException | BrokenBarrierException e) {
    //                 // TODO Auto-generated catch block
    //                 e.printStackTrace();
    //             }
    //             waiting = false;
    // }
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
        for (int i = 0; i < vbox.getChildren().size(); i++) {
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
            newThreadAdded = true;
            Runnable dialoge = new DialogeBox(callNumber,phaser);
            linkCBtoDB.put(checkbox,(DialogeBox)dialoge);
            
            Call call = linkCBtoCall.get(checkbox);
            ((DialogeBox)dialoge).setupCall(call);
            System.out.println(call.getCaller());//.problemState.getProblem().solutions
            executor.execute(dialoge);
            System.out.println("Selected");
            } else {
                linkCBtoDB.get(checkbox).exit();
            }
            
        } 
static class Counter {
        private int count;

        Counter(int initialCount) {
            this.count = initialCount;
        }

        synchronized void reset() {
            count = 1; // Reset the counter to 1
            System.out.println("Counter reset to 1");
        }

        synchronized int getCount() {
            return count;
        }
    }
}

