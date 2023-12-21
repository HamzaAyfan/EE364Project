package com.ee364project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.ee364project.Call.CallState;
import com.ee364project.exceptions.InvalidPhoneNumberException;
import com.ee364project.helpers.*;

/**
 * This class represents a customer in the simulation.
 *
 * @author Team 2
 */
public class Customer extends Person implements CanCall {
    private CustomerState state = CustomerState.IDLE;
    private static ArrayList<Customer> allCustomers = new ArrayList<>();
    private String phoneNumber;
    private CustomerBehaviour behaviour;
    private ProblemInfo problemState = new ProblemInfo();
    private CallInfo callInfo = new CallInfo();
    private static final String CLSNAME = "Customer";
    private static final String[] HEADERS = new String[] { "phone_number", "behaviour", "name" };
    private int faqsSteps = -1;

    /**
     * This enum represents the possible states that a customer can be in.
     */
    public enum CustomerState {
        /**
         * Idle state
         */
        IDLE,
        /**
         * In-call state
         */
        INCALL,
        /**
         * Checking faqs
         */
        CHECK_FAQS,
        /**
         * Waiting on-hold
         */
        WAITING
    }

    /**
     * Gets the CallInfo associated with this instance of CustomerBehaviour.
     *
     * @return The CallInfo object representing information about calls associated
     *         with this CustomerBehaviour.
     */
    public CallInfo getCallInfo() {
        return callInfo;
    }

    /**
     * Gets the maximum wait time for this specific customer.
     * 
     * @return The maximum wait time in seconds for the customer.
     */
    public long getMaxWaitTime() {
        CallInfo callInfo = this.callInfo;
        return callInfo.getMaxWaitTime();
    }

    /**
     * Gets the minimum wait time for this specific customer.
     * 
     * @return The minimum wait time in seconds for the customer.
     */
    public long getMinWaitTime() {
        CallInfo callInfo = this.callInfo;
        return callInfo.getMinWaitTime();
    }

    /**
     * Gets the current state of the customer.
     *
     * @return the current state of the customer
     */
    public CustomerState getState() {
        return this.state;
    }

    /**
     * Sets the state of the customer.
     * 
     * @param state the new state of the customer
     */
    public void setState(CustomerState state) {
        this.state = state;
    }

    /**
     * Returns an array containing all the customers in the simulation.
     *
     * @return an array containing all the customers in the simulation
     */
    public static Customer[] allCustomers() {
        return allCustomers.toArray(new Customer[allCustomers.size()]);
    }

    /**
     * Creates a new customer with the specified phone number, customer behaviour,
     * and name.
     *
     * @param phoneNumber the phone number of the customer
     * @param behaviour   the customer behaviour of the customer
     * @param name        the name of the customer
     * @throws InvalidPhoneNumberException if the specified phone number is not a
     *                                     valid phone number
     */
    public Customer(String phoneNumber, CustomerBehaviour behaviour, String name) throws InvalidPhoneNumberException {
        super(name);
        if (!Utilities.validatePhone(phoneNumber)) {
            throw new InvalidPhoneNumberException(phoneNumber);
        }
        this.behaviour = behaviour;
        this.phoneNumber = phoneNumber;
        allCustomers.add(this);
    }

    /**
     * Constructs a new Customer with default values.
     * The default values include a phone number, random customer behavior, and a
     * default name.
     *
     * @throws InvalidPhoneNumberException If the default phone number is invalid.
     */
    public Customer() throws InvalidPhoneNumberException {
        this("0500000000", CustomerBehaviour.getRandomCustomerBehaviour(), Vars.NONE);
    }

    /**
     * Returns the ProblemInfo object associated with this customer.
     *
     * @return The ProblemInfo object associated with this customer.
     */
    public ProblemInfo getProblemInfo() {
        return problemState;
    }

    /**
     * Clones this customer.
     */
    public Customer clone() {
        try {
            return new Customer(this.phoneNumber, this.behaviour, this.getName());
        } catch (InvalidPhoneNumberException e) {
            return null;
        }
    }

    /**
     * Returns the phone number of the customer.
     *
     * @return The phone number of the customer.
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Sets the phone number of the customer.
     * 
     * @param phoneNumber the new phone number of the customer
     * @throws InvalidPhoneNumberException if the specified phone number is not a
     *                                     valid phone number
     */
    public void setPhoneNumber(String phoneNumber) throws InvalidPhoneNumberException {
        if (!Utilities.validatePhone(phoneNumber)) {
            throw new InvalidPhoneNumberException(phoneNumber);
        }
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the CustomerBehaviour of the customer.
     *
     * @return the CustomerBehaviour of the customer
     */
    public CustomerBehaviour getBehaviour() {
        return this.behaviour;
    }

    /**
     * Sets the behaviour of the customer.
     * 
     * @param behaviour the new behaviour of the customer
     */
    public void setBehaviour(CustomerBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return Utilities.prettyToString(CLSNAME, this.phoneNumber, this.behaviour, getName());
    }

    /**
     * Returns the name of the data type that this object represents.
     *
     * @return the name of the data type that this object represents.
     */
    @Override
    public String getDataTypeName() {
        return CLSNAME;
    }

    /**
     * Returns an array containing the headers of the CSV file.
     *
     * @return an array containing the headers of the CSV file
     */
    @Override
    public String[] getHeaders() {
        return HEADERS;
    }

    /**
     * Returns the average wait time of all calls associated with this customer.
     * 
     * @return the average wait time of all calls associated with this customer
     */
    public long getAverageWaiTime() {
        return this.callInfo.getAverageWaitTime();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String getShortInfo() {
        return "Name: " + getName() +
                "\n(" + this.behaviour.getName() + ")";
    }

    /**
     * Returns an array containing the data of the customer in CSV format.
     *
     * @return an array containing the data of the customer in CSV format
     */
    @Override
    public String[][] getData() {
        String[][] arr = new String[1][3];
        arr[0] = new String[] {
                this.phoneNumber,
                this.behaviour.getName(),
                this.getName()
        };
        return arr;
    }

    /**
     * Returns the customer with the longest wait time.
     * 
     * @return the customer with the longest wait time
     */
    public static Customer getAllMaxWaitTime() {
        Customer[] customers = allCustomers();
        int currnetIndex = 0;
        int bestIndex = 0;
        long bestMax = 0;
        long currentMax = 0;
        for (int i = 0; i < customers.length; i++) {
            currentMax = customers[i].getMaxWaitTime();
            currnetIndex++;
            if (currentMax > bestMax) {
                bestMax = currentMax;
                bestIndex = currnetIndex;
            }
        }

        if (bestIndex < customers.length) {
            return customers[bestIndex];

        } else {
            return customers[0];
        }
    }

    /**
     * Returns the customer with the longest wait time.
     * 
     * @return the customer with the longest wait time
     */
    public static Customer getAllMinWaitTime() {
        Customer[] customers = allCustomers();
        int currnetIndex = 0;
        int bestIndex = 0;
        long bestMin = 0;
        long currentMin = 0;
        for (int i = 0; i < customers.length; i++) {
            currentMin = customers[i].getMinWaitTime();
            if (currentMin < bestMin) {
                bestMin = currentMin;
                bestIndex = currnetIndex;
            }
        }
        if (bestIndex < customers.length) {
            return customers[bestIndex];
        } else {
            return customers[0];
        }
    }

    /**
     * Returns the total wait time of all customers in the simulation.
     * 
     * @return the total wait time of all customers in the simulation
     */

    public static long getAllTotalWaitTime() {
        long sum = 0;
        for (Customer customer : allCustomers) {
            sum += customer.callInfo.getTotalWaitTime();
        }
        return sum;
    }

    /**
     * Returns the total number of calls made by all customers in the simulation.
     * 
     * @return the total number of calls made by all customers in the simulation
     */
    public static long getAllCallCount() {
        int sum = 0;
        for (Customer customer : allCustomers) {
            sum += customer.callInfo.getCallCount();
        }
        return sum;
    }

    /**
     * Returns the average wait time of all calls associated with this customer.
     * 
     * @return the average wait time of all calls associated with this customer
     */
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

    /**
     * Parses an array of data fields to populate the customer's attributes.
     * Expects an array containing phone number, customer behavior, and name fields.
     *
     * @param dataFields An array of data fields representing customer information.
     * @return The updated Customer instance after parsing the data.
     */
    public Customer parseData(String[] dataFields) {
        this.phoneNumber = dataFields[0];
        String customerBehaviour = dataFields[1];
        this.behaviour = CustomerBehaviour.customerBehaviour(customerBehaviour);
        this.setName(dataFields[2]);
        return this;
    }

    /**
     * Shuffles the customer's name, phone number, and behaviour.
     */
    @Override
    public Customer shuffle() {
        this.setName(Utilities.faker.name().firstName()); // chain: external.
        this.phoneNumber = "05" + Utilities.faker.number().digits(8);
        this.behaviour = CustomerBehaviour.getRandomCustomerBehaviour();
        return this;
    }

    /**
     * Makes a call to the customer's phone number.
     */
    @Override
    public void makeCall() {
        Call call = new Call(this);
        this.state = CustomerState.WAITING;
        this.callInfo.newCall(call);
    }

    /**
     * Checks if the customer should visit the FAQs page and, if so, decrements the
     * number of steps remaining. If the number of steps remaining is zero, the
     * problem is solved and the customer returns to the idle state.
     */
    private void checkFaqs() {
        if (this.faqsSteps > 0) {
            this.faqsSteps--;

        } else {
            this.problemState.solve();
            this.state = CustomerState.IDLE;
            this.faqsSteps = -1;

        }
    }

    /**
     * This method is the default routine of the customer. It checks if the customer
     * should visit the FAQs page and, if so, decrements the number of steps
     * remaining. If the number of steps remaining is zero, the problem is solved
     * and the customer returns to the idle state.
     */
    private void defaultRoutine() {
        if (this.state == CustomerState.CHECK_FAQS) {
            checkFaqs();
            return;
        }

        if (this.problemState.isGotProblem()) {
            Ratio faqsChance = this.behaviour.getFaqsChance();
            if (faqsChance.check() && Vars.projectPhase) {
                if (faqsChance.check()) {
                    this.state = CustomerState.CHECK_FAQS;
                    this.faqsSteps = Utilities.random.nextInt(50, 200); // chain: java

                }
            }

            Ratio callChanceRatio = this.behaviour.getCallChance();
            if (callChanceRatio.check()) {
                makeCall();

                return;
            } else {

                return;
            }
        } else {
            Ratio problemAffinityRatio = this.behaviour.getProblemAffinity();
            if (problemAffinityRatio.check()) {
                this.problemState.acquireRandomProblem();
                return;
            } else {
                return;
            }
        }
    }

    /**
     * Returns the tag that should be prepended to all log messages generated by
     * this object.
     *
     * @return the tag that should be prepended to all log messages generated by
     *         this object
     */
    @Override
    protected String getTag() {
        return "Customer: ";
    }

    /**
     * The step method of the customer is responsible for updating the customer's
     * state based on the most recent call and the default routine.
     * 
     * The step method first updates the call information by calling the
     * updateInformation method on the callInfo object. This method updates the
     * maximum and minimum wait times, as well as the last call.
     * 
     * If the last call is null, the default routine is executed. The default
     * routine checks if the customer should visit the FAQs page, solve the problem,
     * or make a call. If the customer should visit the FAQs page, the number of
     * steps remaining is decremented. If the number of steps remaining is zero, the
     * problem is solved and the customer returns to the idle state. If the customer
     * should make a call, a new call is created and added to the callInfo object.
     * 
     * Finally, the switch statement checks the state of the last call. If the call
     * is waiting or in call, the method returns without doing anything. Otherwise,
     * the default routine is executed again.
     * 
     * Overall, the step method ensures that the customer's state is updated based
     * on the most recent call and the default routine.
     * 
     */
    @Override
    public void step() {
        this.callInfo.updateInformation();

        if (this.callInfo.getLastCall() == null) {
            defaultRoutine();
            return;
        }
        Call callInfo = this.callInfo.getLastCall();
        switch (callInfo.getState()) {
            case WAITING:
                return;

            case INCALL:
                return;

            default:
                defaultRoutine();
                return;
        }
    }

    /**
     * Generates and returns a formatted string containing information about the
     * customer.
     *
     * @return A formatted string with phone number, name, and behavior information.
     */
    public String getStringInfo() {
        return "Phone Number: " + getPhoneNumber() +
                "\nName: " + getName() +
                "\nBehaviour: " + this.behaviour.getName();
    }
}

/**
 * This class represents a customer behaviour in the simulation.
 *
 * @author Team 2
 */
class CustomerBehaviour {
    private String name;
    private Ratio problemAffinity;
    private long problemAffinityPeriod;
    private Ratio callChance;
    private long callChancePeriod;

    private Ratio faqsVisitChance;
    private long faqsVisitChancePeriod;
    private Ratio faqsSolveChance;
    private long faqsSolveChancePeriod;
    private static HashMap<String, CustomerBehaviour> customerBehaviourByName = new HashMap<>();

    /**
     * Gets the name associated with this instance of CustomerBehaviour.
     *
     * @return The name of the CustomerBehaviour.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the CustomerBehaviour associated with the specified name.
     *
     * @param name The name of the customer behavior to retrieve.
     * @return The CustomerBehaviour associated with the given name, or null if not
     *         found.
     */
    public static CustomerBehaviour customerBehaviour(String name) {
        return customerBehaviourByName.get(name);
    }

    static {
        customerBehaviourByName.put(PreMade.DEFAULT.name, PreMade.DEFAULT);
        customerBehaviourByName.put(PreMade.SAVVY.name, PreMade.SAVVY);
        customerBehaviourByName.put(PreMade.CHALLENGED.name, PreMade.CHALLENGED);
    }

    /**
     * Returns the CustomerBehaviour associated with a random name from the set of
     * available CustomerBehaviours.
     * 
     * @return the CustomerBehaviour associated with a random name from the set of
     *         available CustomerBehaviours.
     */
    public static CustomerBehaviour getRandomCustomerBehaviour() {
        Set<String> keys = customerBehaviourByName.keySet();
        return customerBehaviourByName.get(
                keys.toArray()[Utilities.random.nextInt(customerBehaviourByName.size())]); // java
    }

    /**
     * This class stores premade behavoiurs for the customer.
     * - default.
     * - savvy.
     * - challenged.
     * 
     * @author Team 2
     */
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

    /**
     * Constructs a CustomerBehaviour instance with specified parameters.
     *
     * @param name                  The name of the customer.
     * @param problemAffinity       The problem affinity ratio.
     * @param problemAffinityPeriod The period for adjusting problem affinity.
     * @param callChance            The call chance ratio.
     * @param callChancePeriod      The period for adjusting call chance.
     * @param faqsVisitChance       The FAQs visit chance ratio.
     * @param faqsVisitChancePeriod The period for adjusting FAQs visit chance.
     * @param faqsSolveChance       The FAQs solve chance ratio.
     * @param faqsSolveChancePeriod The period for adjusting FAQs solve chance.
     */
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
        this.faqsVisitChancePeriod = faqsVisitChancePeriod;
        this.faqsSolveChance = faqsSolveChance;
        this.faqsSolveChancePeriod = faqsSolveChancePeriod;
    }

    /**
     * Gets the adjusted problem affinity ratio based on the current time.
     *
     * @return The adjusted problem affinity ratio.
     */
    Ratio getProblemAffinity() {
        return Timekeeper.adjustedChance(problemAffinity, problemAffinityPeriod);
    }

    /**
     * Gets the adjusted call chance ratio based on the current time.
     *
     * @return The adjusted call chance ratio.
     */
    Ratio getCallChance() {
        return Timekeeper.adjustedChance(callChance, callChancePeriod);
    }

    /**
     * Gets the adjusted FAQs visit chance ratio based on the current time.
     *
     * @return The adjusted FAQs visit chance ratio.
     */
    Ratio getFaqsChance() {
        return Timekeeper.adjustedChance(faqsVisitChance, faqsVisitChancePeriod);
    }

    /**
     * Gets the adjusted FAQs solve chance ratio based on the current time.
     *
     * @return The adjusted FAQs solve chance ratio.
     */
    Ratio getFaqsSolveChance() {
        return Timekeeper.adjustedChance(faqsSolveChance, faqsSolveChancePeriod);
    }

    /**
     * Default constructor that creates a CustomerBehaviour instance with
     * random parameters.
     */
    public CustomerBehaviour() {
        this(
                Utilities.faker.brand().toString(), // chain: external.
                Ratio.getRandRatio(),
                Utilities.random.nextLong(),
                Ratio.getRandRatio(),
                Utilities.random.nextLong(),
                Ratio.getRandRatio(),
                Utilities.random.nextLong(),
                Ratio.getRandRatio(),
                Utilities.random.nextLong());
    }

    /**
     * Returns a string representation of the CustomerBehaviour.
     *
     * @return A string containing the class name and customer name.
     */
    @Override
    public String toString() {
        return Utilities.prettyToString(
                "CBehaviour",
                this.name);
    }

}

/**
 * Represents information about a customer's current problem status.
 * 
 * @author Team 2
 * 
 */
class ProblemInfo {

    /**
     * The last problem encountered by the customer.
     */
    private Problem lastProblem = Problem.NO_PROBLEM;

    /**
     * Solves the current problem, setting it to NO_PROBLEM.
     */
    public void solve() {
        this.lastProblem = Problem.NO_PROBLEM;
    }

    /**
     * Acquires a random problem and sets it as the current problem.
     */
    public void acquireRandomProblem() {
        this.lastProblem = (Problem) Utilities.getRandomFromArray(Problem.getProblemsList());
    }

    /**
     * Gets the last problem encountered by the customer.
     *
     * @return The last problem instance.
     */
    public Problem getLastProblem() {
        return this.lastProblem;
    }

    /**
     * Checks if the customer currently has a problem.
     *
     * @return True if the customer has a problem, otherwise false.
     */
    public boolean isGotProblem() {
        if (this.lastProblem != Problem.NO_PROBLEM) {
            return true;
        }
        return false;
    }
}

/**
 * Represents information about a customer's call interactions.
 * This class tracks statistics and details related to customer calls.
 */
class CallInfo {
    /**
     * The maximum wait time experienced by the customer.
     */
    private long maxWaitTime = 0;
    /**
     * The minimum wait time experienced by the customer.
     */
    private long minWaitTime = 0;
    /**
     * The last call made by the customer.
     */
    private Call lastCall = null;
    /**
     * The latest wait time for the current call.
     */
    private long latestWaitTime;
    /**
     * The tally for the average wait time across all calls.
     */
    private long tallyAverageWaitTime;
    /**
     * The total wait time across all calls.
     */
    private long tallyTotalWaitTime;
    /**
     * The total number of calls made by the customer.
     */
    private long tallyCallCount = 0;

    /**
     * Gets the maximum wait time for this specific customer.
     *
     * @return The maximum wait time in seconds for the customer.
     */
    public long getMaxWaitTime() {
        return maxWaitTime;
    }

    /**
     * Gets the minimum wait time for this specific customer.
     *
     * @return The minimum wait time in seconds for the customer.
     */
    public long getMinWaitTime() {
        return minWaitTime;
    }

    /**
     * Records a new call made by the customer.
     *
     * @param call The new call instance.
     */
    public void newCall(Call call) {
        this.lastCall = call;
    }

    /**
     * Checks if the customer is currently in a call or waiting for one.
     *
     * @return True if the customer is in a call or waiting, otherwise false.
     */
    public boolean isInCall() {
        if (lastCall == null) {
            return false;
        }
        return (lastCall.getState() == CallState.INCALL)
                || (lastCall.getState() == CallState.WAITING);
    }

    /**
     * Ends the current call and updates relevant information.
     */
    public void endCall() {

        long lastCallWaitTime = this.lastCall.getWaitTime();

        if (lastCallWaitTime > this.maxWaitTime) {
            this.maxWaitTime = lastCallWaitTime;
        }

        if (lastCallWaitTime < this.minWaitTime) {
            if (lastCallWaitTime != 0) {
                this.minWaitTime = lastCallWaitTime;
            }
        }
        this.tallyAverageWaitTime = (this.tallyAverageWaitTime * this.tallyCallCount + lastCallWaitTime)
                / (this.tallyCallCount + 1);
        this.tallyCallCount++;
        this.tallyTotalWaitTime += lastCallWaitTime;
        this.latestWaitTime = 0;
    }

    /**
     * Updates call information based on the current state of the call.
     */
    public void updateInformation() {
        if (this.lastCall == null) {
            return;
        }
        if (this.lastCall.getState() != CallState.INCALL) {
            this.latestWaitTime = this.lastCall.getWaitTime();
        }
    }

    /**
     * Gets the last call made by the customer.
     *
     * @return The last call instance.
     */
    public Call getLastCall() {
        return this.lastCall;
    }

    /**
     * Gets the total number of calls made by the customer.
     *
     * @return The total number of calls.
     */
    public long getCallCount() {
        long n = this.tallyCallCount;
        if (this.lastCall != null) {
            if (this.lastCall.getState() != CallState.ENDED) {
                n++;
            }
        }
        return n;
    }

    /**
     * Gets the average wait time experienced by the customer.
     *
     * @return The average wait time in seconds.
     */
    public long getAverageWaitTime() {
        if (this.latestWaitTime > 0) {
            return (this.tallyAverageWaitTime * this.tallyCallCount + this.latestWaitTime) / (this.tallyCallCount + 1);
        }
        return this.tallyAverageWaitTime;
    }

    /**
     * Gets the total wait time experienced by the customer.
     *
     * @return The total wait time in seconds.
     */
    public long getTotalWaitTime() {
        return this.tallyTotalWaitTime + this.latestWaitTime;
    }
}