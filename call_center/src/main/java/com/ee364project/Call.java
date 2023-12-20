package com.ee364project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.concurrent.Phaser;
import javax.sound.midi.Receiver;
import com.ee364project.Customer.CustomerState;
import com.ee364project.Fx.MainSceneController;
import com.ee364project.helpers.Utilities;


/**
 * The {@code Call} class represents a simulated call in a call center.
 * It encapsulates information about the call, including its state,
 * participants (caller and receiver), duration, and associated UI elements.
 * <p>
 * This class implements the {@link Simulated} interface, providing a method
 * for simulation steps to manage the call's lifecycle.
 * <p>
 * The class includes static fields to manage call states, a checkbox associated with
 * the call in the UI, a timeline for call duration, and various lists to store
 * sentences, lengths, and speakers for the call's dialogues.
 *
 * <p><b>Fields:</b>
 * <ul>
 *     <li>{@code CallState}: An enumeration representing the possible states of a call.
 *     <li>{@code CheckBox checkBox}: The checkbox associated with the call in the UI.
 *     <li>{@code VBox vBox}: The VBox container for UI elements.
 *     <li>{@code HashMap<CheckBox, Call> CheckBoxAndCall}: A mapping between checkboxes and calls.
 *     <li>{@code long MAXWAITTIME}: The maximum wait time for a call.
 *     <li>{@code LinkedList<Call> activeCalls}: A list of currently active calls.
 *     <li>{@code LinkedList<Call> callQueue}: A queue of calls waiting to be answered.
 *     <li>{@code LinkedList<Call> callsToRemove}: A list of calls to be removed.
 *     <li>{@code ArrayList<String> sentences}: A list of sentences exchanged during the call.
 *     <li>{@code ArrayList<Integer> lengthsSaved}: A list of lengths corresponding to each sentence.
 *     <li>{@code ArrayList<Person> speaker}: A list of speakers for each sentence.
 *     <li>{@code long callCount}: A count of created calls.
 *     <li>{@code Phaser phaser}: A phaser for synchronization.
 *     <li>{@code HashMap<CheckBox, DialogeBox> linkCBtoDB}: A mapping between checkboxes and dialogue boxes.
 *     <li>{@code int startTime}: The start time of the call.
 *     <li>{@code int endTime}: The end time of the call.
 *     <li>{@code int answerTime}: The time when the call was answered.
 *     <li>{@code int totalTime}: The total duration of the call.
 *     <li>{@code Customer caller}: The customer initiating the call.
 *     <li>{@code Agent receiver}: The agent receiving the call.
 *     <li>{@code CallState state}: The current state of the call.
 *     <li>{@code CallCenter callCenter}: The associated call center.
 *     <li>{@code boolean newThreadAdded}: A flag indicating if a new thread was added.
 *     <li>{@code Timeline callTime}: A timeline for call duration.
 *     <li>{@code Text callNumber}: The text element displaying the call number.
 * </ul>
 * */
public class Call implements Simulated {
    private CheckBox checkBox;
    public static VBox vBox;
    private static HashMap<CheckBox, Call> CheckBoxAndCall = new HashMap<CheckBox, Call>();
    private HBox hbox;
    private static final long MAXWAITTIME = 1000000;  
    private static LinkedList<Call> activeCalls = new LinkedList<>();
    private static LinkedList<Call> callQueue = new LinkedList<>();
    private static LinkedList<Call> callsToRemove = new LinkedList<>();
    private ArrayList<String> sentences = new ArrayList<>();
    private ArrayList<Integer> lengthsSaved = new ArrayList<>();
    private ArrayList<Person> speaker = new ArrayList<>();    
    private static long callCount;    
    public static Phaser phaser;
    private static HashMap<CheckBox, DialogeBox> linkCBtoDB = new HashMap<>();
    private int startTime;
    private int endTime;
    private int answerTime;
    private int totalTime;
    private Customer caller;
    private Agent receiver;
    private CallState state;
    private CallCenter callCenter;
    private static boolean newThreadAdded;
    private Timeline callTime = new Timeline();
    private Text callNumber;
/**
 * Enum representing the possible states of a phone call.
 * Each constant represents a specific state of a call.
 * 
 * <p>Possible states:
 * <ul>
 *   <li>{@link #WAITING}: The call is in a waiting state, waiting to be picked up or connected.</li>
 *   <li>{@link #INCALL}: The call is currently in progress.</li>
 *   <li>{@link #ENDED}: The call has ended, either by being completed or terminated.</li>
 *   <li>{@link #EXPIRED}: The call has expired, indicating a state where a time limit has been exceeded.</li>
 * </ul>
 */
    public static enum CallState {
        WAITING,
        INCALL,
        ENDED,
        EXIRED
    }
/**
 * Retrieves the HBox instance associated with this class.
 * 
 * <p>This method is used to obtain the HBox instance that is managed by the class.
 * The returned HBox may be used for adding or modifying child nodes within the layout.
 * 
 * @return The HBox instance managed by this class.
 */
    public HBox getHbox(){
        return hbox;
    }
/**
 * Retrieves the Text instance representing the call number.
 * 
 * <p>This method is used to obtain the Text instance associated with the call number
 * displayed or managed by this class. The returned Text instance may be used to
 * retrieve or update the text content related to the call number.
 * 
 * @return The Text instance representing the call number.
 */
    public Text getCallNumber(){
        return callNumber;
    }
/**
 * Retrieves a LinkedList containing the active call instances.
 *
 * <p>This method is used to obtain a LinkedList that contains the currently active
 * call instances. The returned list provides access to information about ongoing calls
 * and can be used to iterate over the active calls or perform other operations on them.
 *
 * @return A LinkedList containing the active call instances.
 */
    public static LinkedList<Call> getActiveCalls(){
        return activeCalls;
    }
/**
 * Retrieves a HashMap linking CheckBox instances to associated DialogeBox instances.
 *
 * <p>This method returns a HashMap that establishes a link between CheckBox instances and
 * their corresponding DialogeBox instances. The CheckBoxes are used to control or represent
 * certain actions, and the associated DialogeBoxes provide additional information or
 * interaction for each CheckBox.
 *
 * @return A HashMap linking CheckBox instances to associated DialogeBox instances.
 */    
    public static HashMap<CheckBox, DialogeBox> getLinkBetweenCheckBoxesAndDialoge(){
        return linkCBtoDB;
    }
/**
 * Retrieves a HashMap linking CheckBox instances to associated Call instances.
 *
 * <p>This method returns a HashMap that establishes a link between CheckBox instances and
 * the corresponding Call instances. The CheckBoxes may represent specific actions or states,
 * and the associated Call instances provide information about calls associated with each CheckBox.
 *
 * @return A HashMap linking CheckBox instances to associated Call instances.
 */
    public static HashMap<CheckBox, Call> getLinkBetweenCheckBoxesAndCalls(){
        return CheckBoxAndCall;
    }
/**
 * Retrieves and dequeues a Call instance from the call queue.
 *
 * <p>This method retrieves and removes the head of the call queue, updating the state
 * of the caller and the call itself. If the queue is empty, it returns null.
 *
 * <p>The caller's state is set to {@link CustomerState#INCALL}, and the call state is set to
 * {@link CallState#INCALL} after dequeuing the call.
 *
 * @return A Call instance representing the dequeued call, or null if the queue is empty.
 */    
    public static Call getACall() {
        Call call = callQueue.poll();
        if (call != null) {
            call.caller.setState(CustomerState.INCALL);
            call.state = CallState.INCALL;
        }
        return call;
    }
/**
 * Calculates and retrieves the elapsed time since the start of an operation.
 *
 * <p>This method calculates the time elapsed in milliseconds between the current
 * time obtained from {@link Timekeeper#getTime()} and the start time set during the
 * initialization of the operation.
 *
 * @return The elapsed time in milliseconds.
 */
    public int getTimeElapsed() {
        return (Timekeeper.getTime() - startTime);
    }
/**
 * Creates a linked list of solutions associated with the last problem of a customer.
 *
 * <p>This method takes a customer as a parameter, retrieves the last problem
 * from the customer's problem information, and creates a linked list of solutions
 * associated with that problem. The solutions are obtained from the last problem's
 * solutions list and added to the linked list.
 *
 * @param caller The customer for whom the linked list of solutions is created.
 * @return A linked list of solutions associated with the last problem of the customer.
 */
    public LinkedList<Solution> makeLinkedList(Customer caller) {
        ProblemInfo problemInfo = caller.getProblemInfo();
        Problem lastProblem = problemInfo.getLastProblem();
        ArrayList<Solution> HSsolutions = lastProblem.getSolutionsList();
        LinkedList<Solution> LLsolutions = new LinkedList<>();
        for (Solution soultion : HSsolutions) {
            LLsolutions.add(soultion);
        }
        return LLsolutions;
    }
/**
 * Retrieves the end time associated with an operation.
 *
 * <p>This method returns the end time of an operation, represented in milliseconds
 * since the epoch. The end time is a value that indicates when the operation was completed
 * or when the end time was explicitly set.
 *
 * @return The end time of the operation in milliseconds since the epoch.
 */
    public long getEndTime() {
        return this.endTime;
    }
/**
 * Connects the call and initializes associated parameters.
 *
 * <p>This method is responsible for connecting the call, updating various parameters,
 * and adding the call to the list of active calls. It performs the following steps:
 *
 * <ol>
 *   <li>Removes the call from the call queue.</li>
 *   <li>Retrieves the last problem and assigns the appropriate level to the receiver.</li>
 *   <li>Associates the call with the specified call center.</li>
 *   <li>Creates a copy of the solutions linked list for the call's dialog.</li>
 *   <li>Initializes a mock dialog with the caller, receiver, call, and solutions.</li>
 *   <li>Sets the total time for the call based on the dialog content length.</li>
 *   <li>Sets the answer time and calculates the end time for the call.</li>
 *   <li>Adds the call to the list of active calls.</li>
 * </ol>
 */
    public void connectCall(CallCenter callCenter) {
        System.out.println("Call CONNECTED: ");
        Call.callQueue.remove(this);
        ProblemInfo problemInfo = caller.getProblemInfo();
        Problem lastProblem = problemInfo.getLastProblem();
        receiver.assignLevel(lastProblem);
        this.callCenter = callCenter;
        LinkedList<Solution> solutionsCopy = this.makeLinkedList(caller);
        MockDialoge dialoge = new MockDialoge(caller, receiver, this, solutionsCopy);
        totalTime = dialoge.getContentlength();
        this.answerTime = Timekeeper.getTime();
        this.endTime = this.answerTime + totalTime;
        activeCalls.add(this);
    }
/**
 * Adds a line of dialogue to the conversation log.
 *
 * <p>This method appends a sentence, the corresponding speaker, and the length
 * of the sentence to the conversation log. Each parameter is added to its respective
 * list, maintaining the order of the conversation.
 *
 * @param sentenceString The string representing the dialogue line to be added.
 * @param person The person who delivered the dialogue.
 * @param length The length of the sentence, typically measured in characters or words.
 */
    public void addLine(String sentenceString, Person person, int length){
        sentences.add(sentenceString);
        speaker.add(person);
        lengthsSaved.add(length);
    }
/**
 * Terminates the current call and performs cleanup operations.
 *
 * <p>This method is responsible for ending the call, updating various parameters,
 * and performing cleanup tasks. It performs the following steps:
 *
 * <ol>
 *   <li>Sets the state of the call to {@link CallState#ENDED}.</li>
 *   <li>Ends the call for the caller by invoking {@link CustomerInfo#endCall()}.</li>
 *   <li>Releases the assigned agent back to the call center using {@link CallCenter#releaseAgent(Receiver)}.</li>
 *   <li>Removes the call from the list of active calls.</li>
 *   <li>Removes the associated GUI components from the JavaFX application thread.</li>
 *   <li>Removes the call's entry from the mapping of CheckBox instances to Call instances.</li>
 *   <li>Solves the problem associated with the caller using {@link ProblemInfo#solve()}.</li>
 *   <li>Sets the caller's state to {@link CustomerState#IDLE}.</li>
 *   <li>Sets the end time of the call to the current time obtained from {@link Timekeeper#getTime()}.</li>
 * </ol>
 */
    public void terminateCall() {
        this.state = CallState.ENDED;
        this.caller.callInfo.endCall();
        this.callCenter.releaseAgent(this.receiver);
        Call.activeCalls.remove(this);
        Platform.runLater(() -> {        
                    vBox.getChildren().remove(this.hbox);
                });
        CheckBoxAndCall.remove(this.checkBox);
        ProblemInfo problemInfo = this.caller.getProblemInfo();
        problemInfo.solve();            
        this.caller.setState(CustomerState.IDLE);
        this.endTime = Timekeeper.getTime();
    }
/**
 * Terminates multiple calls and performs cleanup operations.
 *
 * <p>This method iterates through a list of calls to be terminated, invokes the
 * {@link #terminateCall()} method for each call, and clears the list of calls to be removed.
 *
 * <p>Note: This method assumes the existence of certain classes and methods, such as
 * {@link Call#terminateCall()}, {@link LinkedList#clear()}.
 */    
    public static void terminateCalls() {
        for (Call call : Call.callsToRemove) {
            call.terminateCall();
            
        }
        callsToRemove.clear();
    }
/**
 * Applies call expiry to calls in the call queue that have exceeded the maximum waiting time.
 *
 * <p>This method checks the call queue for calls that have been waiting for a duration
 * longer than the specified maximum waiting time (MAXWAITTIME). Calls that have exceeded
 * this threshold are marked with the state {@link CallState#EXIRED} and removed from the queue.
 */
    private static void applyExpiry() { 
        Call call;
        while (callQueue.size() > 0 && (Timekeeper.getTime() - callQueue.peek().startTime >= MAXWAITTIME)) {
            call = callQueue.poll();
            call.state = CallState.EXIRED;
        }
    }
/**
 * Constructor for the Call class, representing a phone call initiated by a customer.
 *
 * <p>Creates a new Call instance with the specified customer as the caller. The constructor
 * initializes various attributes, such as start time, end time, and state, and adds the call
 * to the call queue. It also creates GUI components and adds them to the application's user
 * interface for visual representation.
 *
 * <p>This constructor assumes the existence of certain classes and methods, such as
 * {@link Timekeeper#getTime()}, {@link CallState#WAITING}, {@link MainSceneController#createHbox()},
 * {@link Platform#runLater(Runnable)}, {@link HashMap#put(Object, Object)}.
 *
 * @param caller The customer initiating the call.
 */
    public Call(Customer caller) {
        callCount++;
        this.caller = caller;
        this.startTime = Timekeeper.getTime();
        this.answerTime = 0;
        this.endTime = 0;
        this.caller = caller;
        this.receiver = null;
        this.state = CallState.WAITING;
        callQueue.add(this);
        MainSceneController msc = new MainSceneController();
        Node[] nodes = msc.createHbox(this.getCaller());
        hbox = (HBox) nodes[0];
        checkBox = (CheckBox) nodes[1];
        callNumber = (Text) nodes[2];
        int randomIndex = (int) (Math.random() * (vBox.getChildren().size() + 1));  // chain: java
        Platform.runLater(() -> {
            vBox.getChildren().add(randomIndex,hbox);
        });
        CheckBoxAndCall.put(checkBox, this);
    }

/**
 * Calculates and retrieves the duration of the call.
 *
 * <p>This method calculates the duration of the call by subtracting the answer time
 * from the end time. The result represents the time elapsed during the call.
 *
 * @return The duration of the call in milliseconds.
 */
    public long getCallDuration() {
        return this.endTime - this.answerTime;
    }
/**
 * Retrieves the start time of the call.
 *
 * <p>This method returns the time at which the call was initiated, represented
 * in milliseconds since the epoch. The start time indicates when the call was started.
 *
 * @return The start time of the call in milliseconds since the epoch.
 */
    public long getStartTime() {
        return this.startTime;
    }
/**
 * Retrieves the time at which the call was answered.
 *
 * <p>This method returns the time at which the call was answered, represented
 * in milliseconds since the epoch. The answer time indicates when the call was
 * picked up or accepted.
 *
 * @return The time at which the call was answered in milliseconds since the epoch.
 */
    public long getAnswerTime() {
        return this.answerTime;
    }
/**
 * Calculates and retrieves the wait time of the call.
 *
 * <p>This method calculates the wait time of the call, which is the duration the call spent
 * in the waiting state before being answered or ending. If the call has ended, the wait time
 * is calculated from the start time to the answer time. Otherwise, it is calculated from the
 * start time to the current time.
 *
 * @return The wait time of the call in milliseconds.
 */
    public long getWaitTime() {
        if (this.state != CallState.ENDED) {
            return Timekeeper.getTime() - this.startTime;
        }
        return this.answerTime - this.startTime;
    }
/**
 * Retrieves the customer associated with the call.
 *
 * <p>This method returns the customer who initiated the call. The caller is an instance
 * of the {@link Customer} class, representing the individual making the phone call.
 *
 * @return The customer associated with the call.
 */
    public Customer getCaller() {
        return this.caller;
    }
/**
 * Retrieves the agent assigned to handle the call.
 *
 * <p>This method returns the agent assigned to handle the call. The receiver is an instance
 * of the {@link Agent} class, representing the individual responsible for managing the call.
 * If the call has not been assigned to an agent, the method returns null.
 *
 * @return The agent assigned to handle the call, or null if not assigned.
 */
    public Agent getReceiver() {
        return this.receiver;
    }
/**
 * Sets the agent to handle the call.
 *
 * <p>This method assigns the specified agent to handle the call. The agent, an instance
 * of the {@link Agent} class, represents the individual responsible for managing the call.
 *
 * @param agent The agent to be assigned to handle the call.
 */
    public void setReciever(Agent agent) {
        this.receiver = agent;
    }
/**
 * Retrieves the current state of the call.
 *
 * <p>This method returns the current state of the call, represented by an instance
 * of the {@link CallState} enum. The call state provides information about the current
 * status or stage of the call, such as whether it's waiting, in progress, ended, or expired.
 *
 * @return The current state of the call.
 */
    public CallState getState() {
        return this.state;
    }
/**
 * Returns a string representation of the call for debugging or logging purposes.
 *
 * <p>This method generates a string that includes the call identifier, caller information,
 * and the duration of the call. The result is intended for debugging, logging, or other
 * informational purposes.
 *
 * @return A string representation of the call.
 */
    @Override
    public String toString() {
        return Utilities.prettyToString("Call" + callCount, this.caller, this.getCallDuration());
    }
/**
 * Performs a single step or iteration of the call's lifecycle.
 *
 * <p>This method advances the state of the call based on its current state and other conditions.
 * If the call is currently in the {@link CallState#INCALL} state and the end time has been reached,
 * the state is updated to {@link CallState#ENDED}, and the call is added to the list of calls to be removed.
 * The method also checks for calls in the waiting state that have exceeded the maximum waiting time
 * using {@link Call#applyExpiry()}.
 */
    @Override
    public void step() {
        if (this.state == CallState.INCALL) {
            if (this.endTime <= Timekeeper.getTime()) {
                this.state = Call.CallState.ENDED;
                callsToRemove.add(this);
                System.out.println("Adding to removed list: ");
            } 
        }
        applyExpiry();
    }
/**
 * Retrieves the CheckBox associated with the call.
 *
 * <p>This method returns the CheckBox instance associated with the call. The CheckBox is a
 * graphical user interface component used to represent or control the state of the call
 * in the user interface.
 *
 * @return The CheckBox associated with the call.
 */
    public CheckBox getCheckBox() {
        return checkBox;
    }
/**
 * Retrieves the list of saved lengths associated with the call's dialogue lines.
 *
 * <p>This method returns an ArrayList of integers representing the lengths of
 * dialogue lines associated with the call. Each integer corresponds to the length
 * of a sentence spoken during the call.
 *
 * @return The ArrayList of integers representing the lengths of dialogue lines.
 */
    public ArrayList<Integer> getLengths() {
        return lengthsSaved;
    }
/**
 * Retrieves the list of sentences associated with the call's dialogue.
 *
 * <p>This method returns an ArrayList of strings representing the sentences spoken
 * during the call. Each string corresponds to a sentence in the call's dialogue.
 *
 * @return The ArrayList of strings representing the sentences in the call's dialogue.
 */
    public ArrayList<String> getSentences() {
        return sentences;
    }
/**
 * Retrieves a specific sentence from the list of sentences associated with the call's dialogue.
 *
 * <p>This method returns the sentence at the specified index in the list of sentences spoken
 * during the call. The index parameter represents the position of the desired sentence.
 *
 * @param i The index of the sentence to retrieve.
 * @return The sentence at the specified index in the call's dialogue.
 */    
    public String getSentences(int i) {
        return sentences.get(i);
    }
/**
 * Retrieves the length of a specific sentence from the list of dialogue lengths.
 *
 * <p>This method returns the length of the sentence at the specified index in the list of
 * dialogue lengths associated with the call. The index parameter represents the position
 * of the desired sentence's length.
 *
 * @param i The index of the sentence length to retrieve.
 * @return The length of the sentence at the specified index in the call's dialogue.
 */
    public int getlengths(int i) {
        return lengthsSaved.get(i);
    }
/**
 * Retrieves the person associated with a specific sentence in the call's dialogue.
 *
 * <p>This method returns the person (speaker) associated with the sentence at the
 * specified index in the list of speakers associated with the call. The index parameter
 * represents the position of the desired sentence's speaker.
 *
 * @param i The index of the speaker to retrieve.
 * @return The person associated with the sentence at the specified index in the call's dialogue.
 */
    public Person getPerson(int i) {
        return speaker.get(i);
    }
}
/**
 * The MockDialoge class simulates a dialogue between a customer (caller) and an agent (receiver) during a call.
 * It involves selecting solutions, generating responses, and managing the content of the call.The dialogue involves 
 * interactions between a customer (caller) and an agent (receiver) with a set of predefined solutions.
 *
 * <ul>
 *     <li>{@code LinkedList<Solution> solutions}: A list of solutions available for the dialogue.
 *     <li>{@code Call currentCall}: The call for which the mock dialogue is simulated.
 *     <li>{@code Customer caller}: The customer initiating the call.
 *     <li>{@code Agent receiver}: The agent assigned to handle the call.
 *     <li>{@code boolean firstSolutionSeeked}: A flag indicating whether the first solution has been sought.
 *     <li>{@code int discussionLength}: The cumulative length of the dialogue content in terms of words.
 * </ul>
 * @author Your Name
 * @version 1.0
 */
class MockDialoge {
    private LinkedList<Solution> solutions = new LinkedList<>();
    private Call currentCall;
    private Customer caller;
    private Agent receiver;
    private boolean firstSolutionSeeked = true;
    private int discussionLength;
/**
 * Constructs a MockDialoge instance.
 *
 * <p>This constructor initializes a MockDialoge instance with the specified parameters, including
 * the customer initiating the call, the agent assigned to handle the call, the call for which the
 * mock dialogue is simulated, and the list of solutions available for the dialogue.
 *
 * @param caller The customer initiating the call.
 * @param receiver The agent assigned to handle the call.
 * @param currentCall The call for which the mock dialogue is simulated.
 * @param solutions The list of solutions available for the dialogue.
 */
    public MockDialoge(Customer caller, Agent receiver, Call currentCall, LinkedList<Solution> solutions) {
        this.solutions = solutions;
        this.caller = caller;
        this.receiver = receiver;
        this.currentCall = currentCall;
    }
/**
 * Retrieves the total content length of the dialogue.
 *
 * <p>This method calculates and returns the cumulative length of the dialogue content in terms of words.
 * It achieves this by invoking the {@link #getWords()} method and summing up the lengths associated with
 * each sentence in the current call.
 *
 * @return The total content length of the dialogue in terms of words.
 */
    public int getContentlength() {        
        this.getWords();
        for (Integer length : currentCall.getLengths()) {
            discussionLength += length;
        }
        return discussionLength;
    }
/**
 * Retrieves a random solution index based on the agent's level.
 *
 * <p>This method determines a random index representing a solution based on the agent's level.
 * - If the agent is at the {@link Agent.Level#SAVEY} level, it returns the index 0.
 * - If the agent is at the {@link Agent.Level#CHALLENGED} level, it generates a random index within the
 *   second half of the available solutions.
 * - For other agent levels, it generates a random index within the first half of the available solutions.
 *
 * @return A random solution index based on the agent's level.
 */
    private int getSolution() {
        int length = solutions.size(); // print(length + " from call " + z + " agent is " + receiver.getlevel());
        switch (receiver.getlevel()) {
            case SAVEY:
                return 0;
            case CHALLENGED:
                return RandomInt.generateWithinRange(length / 2, length);
            default:
                return RandomInt.generateWithinRange(0, length / 2);
        }
    }
/**
 * Iteratively selects solutions, introduces transitions, and generates dialogue steps until a specific condition is met.
 *
 * <p>This method repeatedly selects a solution using the {@link #getSolution()} method, removes the selected
 * solution from the available solutions list, introduces transitions using the {@link #introOrTransition(Solution)}
 * method, and generates dialogue steps using the {@link #getSteps(Solution)} method. The iteration continues
 * until the selected solution index is 0.
 */
    private void getWords() {
        int i = 0;
        do {
            i = getSolution();
            Solution selectedSolution = solutions.get(i);
            solutions.remove(i);
            introOrTransition(selectedSolution);
            getSteps(selectedSolution);
        } while (i != 0);        
    }
/**
 * Generates dialogue steps based on the selected solution.
 *
 * <p>This method generates dialogue steps for both the agent and the customer based on the responses
 * provided in the selected solution. It iterates through the agent responses and customer responses arrays
 * of the selected solution, adding lines to the current call using the {@link Call#addLine(String, Person, int)}
 * method with appropriate parameters.
 *
 * @param selectedSolution The selected solution for which dialogue steps are generated.
 */
    private void getSteps(Solution selectedSolution) {
        for (int j = 0; j < selectedSolution.getAgentResponse().length; j++) {
            currentCall.addLine(selectedSolution.getAgentResponse()[j], receiver, MockDialoge.getlength(selectedSolution.getAgentResponse()[j]));
            currentCall.addLine(selectedSolution.getCustomerResponse()[j], caller, MockDialoge.getlength(selectedSolution.getCustomerResponse()[j]));
        }
    }
/**
 * Retrieves the length of a string in terms of words.
 *
 * <p>This static method calculates and returns the length of the provided string in terms of words.
 * It achieves this by splitting the string using whitespace as a delimiter and counting the number
 * of resulting segments.
 *
 * @param string The string for which to calculate the length.
 * @return The length of the string in terms of words.
 */
    private static int getlength(String string) {
        return string.split("\\s+").length;
    }
/**
 * Introduces transitions or provides alternative responses based on the dialogue state.
 *
 * <p>This method introduces transitions or provides alternative responses based on the current dialogue state.
 * If it's the first solution being sought (determined by the {@code firstSolutionSeeked} flag), it adds lines
 * to the current call using randomly selected introductory phrases from the selected solution. If it's not the
 * first solution, it provides alternative responses indicating that the previous action did not work and the agent
 * will seek an alternative solution.
 */
    private void introOrTransition(Solution selectedSolution) {
        if (firstSolutionSeeked == true) {
            currentCall.addLine(selectedSolution.getRandomIntro(receiver), receiver, MockDialoge.getlength(selectedSolution.getRandomIntro(receiver)));
            currentCall.addLine(selectedSolution.getRandomIntro(caller), caller, MockDialoge.getlength(selectedSolution.getRandomIntro(caller)));
            firstSolutionSeeked = false;
        } else {
            currentCall.addLine("I just did that but it did not work", caller, 9);
            currentCall.addLine("sorry it did not work let me seek an alternative", receiver, 10);
        }
    }
}
/**
 * The RandomInt class provides utility methods for generating random integers.
 *
 * <p>This class contains static methods for generating random integers within specified ranges.
 * The methods include {@link #generateRandom(int)} for generating a random integer up to a given maximum,
 * and {@link #generateWithinRange(int, int)} for generating a random integer within a specified range.
 *
 * @author Mahdi Bathallath
 * @version 1.0
 */
class RandomInt {
/**
 * Generates a random integer up to the specified maximum.
 *
 * @param options The maximum value (exclusive) for the generated random integer.
 * @return A random integer in the range [0, options).
 */
    public static int generateRandom(int options) {
        return generateWithinRange(0, options);
    }
/**
 * Generates a random integer within the specified range.
 *
 * <p>This method calculates and returns a random integer within the inclusive range [start, end).
 * It uses the formula {@code start + (int) (Math.random() * (end - start))} to achieve this.
 *
 * @param start The inclusive start of the range.
 * @param end The exclusive end of the range.
 * @return A random integer in the range [start, end).
 */
    public static int generateWithinRange(int start, int end) {
        int number = start + (int) (Math.random() * (end - start));
        return number;
    }
}