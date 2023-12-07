package com.ee364project;

import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Main {
    
    public static void main(String[] args) {
        int i = 0;
        int n = 1;
        Department[] departments = new Department[n];
        for (HasData department : Utilities.getFakeData(n, Vars.DataClasses.Department)) {
            departments[i++] = (Department) department;
        };

        i = 0;
        n = 1;
        Problem[] problems = new Problem[n];
        for (HasData problem : Utilities.getFakeData(n, Vars.DataClasses.Problem)) {
            problems[i++] = (Problem) problem;
        }

        i = 0;
        n = 1;
        Agent[] agents = new Agent[n];
        for (HasData agent : Utilities.getFakeData(n, Vars.DataClasses.Agent)) {
            agents[i++] = (Agent) agent;
        }

        i = 0;
        n = 1;
        Customer[] customers = new Customer[n];
        for (HasData customer : Utilities.getFakeData(n, Vars.DataClasses.Customer)) {
            customers[i++] = (Customer) customer;
        }

        CallCenter callCenter = new CallCenter(agents);


        while (true) {
            for (Customer customer : customers) {
                customer.step();
            } 
            
            for (Call call : Call.activeCalls) {
                call.step();
            }

            callCenter.step();


            Timekeeper.step();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
