package com.ee364project;


import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.ee364project.exceptions.InvalidIdException;
import com.ee364project.exceptions.InvalidPhoneNumberException;



import javafx.application.Application;
import javafx.stage.Stage;


public class Test extends Application {
    LinkedList<Customer> customers = new LinkedList<Customer>();
	LinkedList<Agent> agents = new LinkedList<Agent>();
	LinkedList<Call> calls = new LinkedList<Call>();
    


    public void start(Stage primaryStage) {		
		for (int i = 0;i<20;i++){
            try {
                Customer customer = new Customer();
                customer.shuffle();
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
        
        for (int i =1;i<=2;i++){
            DialogeBox dialoge = new DialogeBox();
            dialoge.openEmptyWindow("Call "+i,500,200);            
        }
        
        // new Thread(()->{
		for (int i = 0;i<20;i++) {
			customers.get(i).problemState.acquireProblem();
            customers.get(i).problemState.getProblem().shuffle();
            calls.add(new Call(customers.get(i)));
            calls.get(i).connectCall(customers.get(i), agents.get(i));}
		// }).start();

	}
    public static void main(String[] args) {
        launch(args);
    }   
}
