package com.ee364project;

import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Test {
    public static void main(String[] args) {
        Utilities.getFakeData(2, Vars.DataClasses.Department);
        Utilities.getFakeData(3, Vars.DataClasses.Problem);
        Customer[] customers = new Customer[3];
        int i = 0;
        for (HasData datum : Utilities.getFakeData(3, Vars.DataClasses.Customer)) {
            customers[i++] = (Customer) datum;
        }
        ;
        Agent[] agents = new Agent[2];
        i = 0;
        for (HasData datum : Utilities.getFakeData(2, Vars.DataClasses.Agent)) {
            agents[i++] = (Agent) datum;
        }
        ;

        CallCenter callCenter = new CallCenter(agents);

        while (true) {
            for (Customer customer : customers) {
                customer.step();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            for (Agent agent : agents) {
                agent.step();
            }

            callCenter.step();

            Call.step();
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
