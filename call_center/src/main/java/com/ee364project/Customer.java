package com.ee364project;

import java.util.ArrayList;
import java.util.HashMap;

import com.ee364project.Call.CallState;
import com.ee364project.exceptions.InvalidPhoneNumberException;
import com.ee364project.helpers.*;

public class Customer extends Person implements CanCall {

    public enum CustomerState {
        IDLE,
        INCALL,
        CHECK_FAQS,
        WAITING
    }

    public CustomerState getState() {
        return this.state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }

    private CustomerState state = CustomerState.IDLE;
    static int i;
    static int j;
    private static ArrayList<Customer> allCustomers = new ArrayList<>();

    public static Customer[] allCustomers() {
        return allCustomers.toArray(new Customer[allCustomers.size()]);
    }

    private static final String CLSNAME = "Customer";
    private static final String[] HEADERS = new String[] { "phone_number", "behaviour", "name" };

    private String phoneNumber;
    private CustomerBehaviour behaviour;
    public ProblemInfo problemState = new ProblemInfo();
    public CallInfo callInfo = new CallInfo();

    public Customer(String phoneNumber, CustomerBehaviour behaviour, String name) throws InvalidPhoneNumberException {
        super(name);
        if (!Utilities.validatePhone(phoneNumber)) {
            throw new InvalidPhoneNumberException(phoneNumber);
        }
        this.behaviour = behaviour;
        this.phoneNumber = phoneNumber;
        allCustomers.add(this);
    }

    public Customer() throws InvalidPhoneNumberException {
        this("0500000000", CustomerBehaviour.getRandomCustomerBehaviour(), Vars.NONE);
    }

    public Customer clone() {
        try {
            return new Customer(this.phoneNumber, this.behaviour, this.getName());
        } catch (InvalidPhoneNumberException e) {
            return null;
        }
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) throws InvalidPhoneNumberException {
        if (!Utilities.validatePhone(phoneNumber)) {
            throw new InvalidPhoneNumberException(phoneNumber);
        }
        this.phoneNumber = phoneNumber;
    }

    public CustomerBehaviour getBehaviour() {
        return this.behaviour;
    }

    public void setBehaviour(CustomerBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    @Override
    public String toString() {
        return Utilities.prettyToString(CLSNAME, this.phoneNumber, this.behaviour, getName());
    }

    @Override
    public String getDataTypeName() {
        return CLSNAME;
    }

    @Override
    public String[] getHeaders() {
        return HEADERS;
    }

    @Override
    public String[][] getData() {
        String[][] arr = new String[1][3];
        arr[0] = new String[] {
                this.phoneNumber,
                this.behaviour.name,
                this.getName()
        };
        return arr;
    }

    public static long getAllTotalWaitTime() {
        long sum = 0;
        for (Customer customer : allCustomers) {
            sum += customer.callInfo.getTotalWaitTime();
        }
        return sum;
        // CallInfo.addToTotal(sum);
        // return CallInfo.getTotalCallWaitTime();
    }

    public static long getAllCallCount() {
        int sum = 0;
        for (Customer customer : allCustomers) {
            sum += customer.callInfo.getCallCount();
        }
        return sum;
    }

    public static long getAllAverageWaitTime() {
        long sum = 0;
        long n = 0;
        for (Customer customer : allCustomers) {
            if (customer.callInfo.getCallCount() > 0) {
                sum += customer.callInfo.getAverageWaitTime();
                n++;
            }
        }
        if (n != 0) {
            return sum / n;
        } else {
            return 0;
        }
    }

    public Customer parseData(String[] dataFields) {
        this.phoneNumber = dataFields[0];
        this.behaviour = CustomerBehaviour.customerBehaviourByName.get(dataFields[1]);
        this.setName(dataFields[2]);
        return this;
    }

    @Override
    public Customer shuffle() {
        this.setName(Utilities.faker.name().firstName());
        this.phoneNumber = "05" + Utilities.faker.number().digits(8);
        this.behaviour = CustomerBehaviour.getRandomCustomerBehaviour();
        return this;
    }

    @Override
    public void makeCall() {
        Call call = new Call(this);
        this.state = CustomerState.WAITING;
        this.callInfo.newCall(call);
    }

    private void idle(String msg) {
        Utilities.log(this, "idles", "", msg);
    }

    private void checkFaqs() {
        if (this.behaviour.getFaqsSolveChance().check()) {
            this.problemState.solve();
            this.state = CustomerState.IDLE;
            Utilities.log(this, "solved thier own", this.problemState.getLastProblem(), "");
        } else {
            Utilities.log(this, "could not solve thier own", this.problemState.getLastProblem(), "they will try again");
        }
    }

    private void defaultRoutine() {
        if (this.state == CustomerState.CHECK_FAQS) {
            checkFaqs();
            return;
        }

        if (this.problemState.isGotProblem()) {
            if (this.behaviour.getFaqsChance().check()) {
                if (this.behaviour.getFaqsChance().check()) {
                    this.state = CustomerState.CHECK_FAQS;
                    Utilities.log(this, "visted faqs", "", "");
                }
            }

            if (this.behaviour.getCallChance().check()) {
                makeCall();
                Utilities.log(this, "calls", "", "");
                return;
            } else {
                idle("");
                return;
            }
        } else {
            if (this.behaviour.getProblemAffinity().check()) {
                this.problemState.acquireRandomProblem();
                Utilities.log(this, "got", this.problemState.getLastProblem(), "");
                return;
            } else {
                idle("");
                return;
            }
        }
    }

    @Override
    protected String getTag() {
        return "Customer: ";
    }

    @Override
    public void step() {
        this.callInfo.updateInformation();

        if (this.callInfo.getLastCall() == null) {
            defaultRoutine();
            return;
        }
        switch (this.callInfo.getLastCall().getState()) {
            case WAITING:
                idle("waiting for answer");
                return;

            case INCALL:
                idle("in-call");
                return;

            default:
                defaultRoutine();
                return;
        }
    }

    public String getStringInfo() {
        return "Phone Number: " + getPhoneNumber() +
                "\nName: " + getName() +
                "\nBehaviour: " + this.behaviour.name;
    }
}

class CustomerBehaviour {
    public static HashMap<String, CustomerBehaviour> customerBehaviourByName = new HashMap<>();
    static {
        customerBehaviourByName.put(PreMade.DEFAULT.name, PreMade.DEFAULT);
        customerBehaviourByName.put(PreMade.SAVVY.name, PreMade.SAVVY);
        customerBehaviourByName.put(PreMade.CHALLENGED.name, PreMade.CHALLENGED);
    }

    public static CustomerBehaviour getRandomCustomerBehaviour() {
        return customerBehaviourByName.get(
                customerBehaviourByName.keySet().toArray()[Utilities.random.nextInt(customerBehaviourByName.size())]);
    }

    public static final class PreMade {
        public static final CustomerBehaviour DEFAULT = new CustomerBehaviour( // 90% chance to get a problem every
                                                                               // month and 50% chance to call in every
                                                                               // 2 days.
                "default",
                new Ratio(0.5), // new Ratio(0.9),
                1, // Timekeeper.getSecondsInMonth(1),
                new Ratio(0.5),
                1, // Timekeeper.getSecondsInDay(2),
                new Ratio(0.5), // new Ratio(0.2),
                1, // Timekeeper.getSecondsInDay(1),
                new Ratio(0.5), // new Ratio(0.5),
                1 // Timekeeper.getSecondsInDay(1)
        );
        public static final CustomerBehaviour SAVVY = new CustomerBehaviour( // 50% chance to get a problem every 2
                                                                             // months and 50% chance to call in every
                                                                             // week.
                "savvy",
                new Ratio(0.1), // new Ratio(0.5),
                1, // Timekeeper.getSecondsInMonth(2),
                new Ratio(0.2), // new Ratio(0.5),
                1, // Timekeeper.getSecondsInWeek(1),
                new Ratio(0.9), // new Ratio(0.9),
                1, // Timekeeper.getSecondsInDay(1),
                new Ratio(0.9), // new Ratio(0.9),
                1 // Timekeeper.getSecondsInDay(1)
        );
        public static final CustomerBehaviour CHALLENGED = new CustomerBehaviour( // 90% chance to get a problem every 2
                                                                                  // days and 90% chance to call every
                                                                                  // day.
                "challenged",
                new Ratio(0.7), // new Ratio(0.9),
                1, // Timekeeper.getSecondsInDay(2),
                new Ratio(0.9), // new Ratio(0.9),
                1, // Timekeeper.getSecondsInDay(1),
                new Ratio(0.1), // new Ratio(0.1),
                1, // Timekeeper.getSecondsInDay(1),
                new Ratio(0.1), // new Ratio(0.2),
                1 // Timekeeper.getSecondsInDay(1)
        );
    }

    public String name;
    private Ratio problemAffinity;
    private long problemAffinityPeriod;
    private Ratio callChance;
    private long callChancePeriod;

    private Ratio faqsVisitChance;
    private long faqsVisitChancePeriod;
    private Ratio faqsSolveChance;
    private long faqsSolveChancePeriod;

    public CustomerBehaviour(
            String name, Ratio problemAffinity,
            long problemAffinityPeriod,
            Ratio callChance,
            long callChancePeriod,
            Ratio faqsVisitChance,
            long faqsVisitChancePeriod,
            Ratio faqsSolveChance,
            long faqsSolveChancePeriod) {
        this.name = name;
        this.problemAffinity = problemAffinity;
        this.problemAffinityPeriod = problemAffinityPeriod;
        this.callChance = callChance;
        this.callChancePeriod = callChancePeriod;
        this.faqsVisitChance = faqsVisitChance;
        this.faqsSolveChance = faqsSolveChance;
        this.faqsVisitChancePeriod = faqsVisitChancePeriod;
    }

    Ratio getProblemAffinity() {
        return Timekeeper.adjustedChance(problemAffinity, problemAffinityPeriod);
    }

    Ratio getCallChance() {
        return Timekeeper.adjustedChance(callChance, callChancePeriod);
    }

    Ratio getFaqsChance() {
        return Timekeeper.adjustedChance(faqsVisitChance, faqsVisitChancePeriod);
    }

    Ratio getFaqsSolveChance() {
        return Timekeeper.adjustedChance(faqsSolveChance, faqsSolveChancePeriod);
    }

    public CustomerBehaviour() {
        this(
                Utilities.faker.brand().toString(),
                Ratio.getRandRatio(),
                Utilities.random.nextLong(),
                Ratio.getRandRatio(),
                Utilities.random.nextLong(),
                Ratio.getRandRatio(),
                Utilities.random.nextLong(),
                Ratio.getRandRatio(),
                Utilities.random.nextLong());
    }

    @Override
    public String toString() {
        return Utilities.prettyToString(
                "CBehaviour",
                this.name);
    }

}

class ProblemInfo {
    private Problem lastProblem = Problem.NO_PROBLEM;

    public void solve() {
        this.lastProblem = Problem.NO_PROBLEM;
    }

    public void acquireRandomProblem() {
        this.lastProblem = (Problem) Utilities.getRandomFromArray(Problem.allProblems);
    }

    public Problem getLastProblem() {
        return this.lastProblem;
    }

    public boolean isGotProblem() {
        if (this.lastProblem != Problem.NO_PROBLEM) {
            return true;
        }
        return false;
    }
}

class CallInfo {
    private Call lastCall = null;
    private long latestWaitTime;
    private long tallyAverageWaitTime;
    private long tallyTotalWaitTime;
    private long tallyCallCount = 0;

    public void newCall(Call call) {
        this.lastCall = call;
    }

    public boolean isInCall() {
        if (lastCall == null) {
            return false;
        }
        return (lastCall.getState() == CallState.INCALL)
                || (lastCall.getState() == CallState.WAITING);
    }

    public void endCall() {
        this.tallyAverageWaitTime = (this.tallyAverageWaitTime * this.tallyCallCount + this.lastCall.getWaitTime())
                / (this.tallyCallCount + 1);
        this.tallyCallCount++;
        this.tallyTotalWaitTime += this.lastCall.getWaitTime();
        this.latestWaitTime = 0;
    }

    public void updateInformation() {
        if (this.lastCall == null) {
            return;
        }
        if (this.lastCall.getState() != CallState.INCALL) {
            this.latestWaitTime = this.lastCall.getWaitTime();
        }
    }

    public Call getLastCall() {
        return this.lastCall;
    }

    public long getCallCount() {
        long n = this.tallyCallCount;
        if (this.lastCall != null) {
            if (this.lastCall.getState() != CallState.ENDED) {
                n++;
            }
        }
        return n;
    }

    public long getAverageWaitTime() {
        if (this.latestWaitTime > 0) {
            return (this.tallyAverageWaitTime * this.tallyCallCount + this.latestWaitTime) / (this.tallyCallCount + 1);
        }
        return this.tallyAverageWaitTime;
    }

    public long getTotalWaitTime() {
        return this.tallyTotalWaitTime + this.latestWaitTime;
    }
}