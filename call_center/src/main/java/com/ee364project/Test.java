package com.ee364project;

import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Test {
    public static void main(String[] args) {
        Utilities.getFakeData(5, Vars.DataClasses.Department);
        Utilities.getFakeData(20, Vars.DataClasses.Problem);
        Customer[] customers = new Customer[10];
        int i = 0;
        for (HasData datum : Utilities.getFakeData(10, Vars.DataClasses.Customer)) {
            customers[i++] = (Customer) datum;
        }
        ;
        Agent[] agents = new Agent[3];
        i = 0;
        for (HasData datum : Utilities.getFakeData(3, Vars.DataClasses.Agent)) {
            agents[i++] = (Agent) datum;
        }
        ;

        CallCenter callCenter = new CallCenter(agents);

        while (true) {
            for (Customer customer : customers) {
                customer.step();
            }

            for (Agent agent : agents) {
                agent.step();
            }

            callCenter.step();

            Call.step();
            Timekeeper.step();
        }
    }
}
