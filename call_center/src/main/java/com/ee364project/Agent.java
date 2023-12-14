package com.ee364project;

import java.time.LocalDateTime;
import java.util.ArrayList;


import com.ee364project.exceptions.InvalidIdException;
import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

import java.util.Random;



enum Level{
    SAVEY,
    DEFULT,
    CHALLENGED
}
public class Agent extends Person {

    private static ArrayList<Agent> allAgents = new ArrayList<>();

    public Agent[] getAllAgents() {
        return allAgents.toArray(new Agent[allAgents.size()]);
    }

    private static final String CLSNAME = "Agent";
    private static final String[] HEADERS = new String[] { "id", "name", "department", "joinDate" };
    private Level level;

    private String id;
    private Department department;
    private LocalDateTime joinDate;
    private ArrayList<Problem> problemsSeen = new ArrayList<>();

    public Agent(String id, String name, Department department) throws InvalidIdException {
        super(name);
        if (!Utilities.validateId(id)) {
            throw new InvalidIdException(id);
        }
        this.id = id;
        this.department = department;
        this.joinDate = LocalDateTime.now();
        allAgents.add(this);
        
    }

    public void assignLevel(Problem problem){  
        for (Problem problemSeen: problemsSeen){
            if (problemSeen==problem){
                level = Level.SAVEY;
                return;
            }
        }      
        if (this.getDepartment() == problem.getDepartment()){
            level = RandomSelect.getRandomEnumValue(Level.class,1);
        }else{
            level = RandomSelect.getRandomEnumValue(Level.class,0);
        }
    }

    public Level getlevel() {
		return level;
	}

    public Agent() throws InvalidIdException {
        this(Vars.DEFALT_ID, Vars.NONE, Department.NO_DEPARTMENT);
    }

    @Override
    public String toString() {
        return Utilities.prettyToString(CLSNAME, this.id, this.getName(), this.department.toString(), this.joinDate);
    }

    public LocalDateTime getJoinDate() {
        return this.joinDate;
    }

    public Department getDepartment() {
        return this.department;
    }

    public String getId() {
        return this.id;
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
        String[][] arr = new String[1][4];
        arr[0] = new String[] {
                this.id,
                this.getName(),
                this.department.getName(),
                this.joinDate.toString()
        };
        return arr;
    }

    @Override
    public Agent parseData(String[] dataFields) {
        this.id = dataFields[0];
        this.setName(dataFields[1]);
        this.department = Department.getDepartment(dataFields[2]);
        this.joinDate = LocalDateTime.parse(dataFields[3]);
        return this;
    }

    @Override
    public Agent shuffle() {
        this.id = Utilities.faker.number().digits(8);
        this.setName(Utilities.faker.name().firstName());
        this.department = Department.getRandomDepartment();
        this.joinDate = Utilities.getRandLocalDateTime();
        return this;
    }

    private void idle(String msg) {
        Utilities.log(this, "idles", "", msg);
    }

    @Override
    public void step() {
        if (callInfo.isInCall()) {
            idle("in-call with " + callInfo.getLastCall().getReceiver());
        } else {
            idle("no call assigned");
        }
        
        // NOTE: for now, agents don't require active simulation.

        /*
         * To whoever writing this method:
         * - this method will be called for each agent on each cycle.
         * - in this method you should define how the agent will interact with environment.
         * - the most crucial piece of information is callInfo.
         * - when the agent gets assigned a call, it will be stored in callInfo.
         * 
         * - NOTE that callInfo is not of type Call.
         * 
         * - callInfo.getLastCall() will give you the last call the agent had.
         * 
         * 
         * = callInfo.history will give you all the calls assigned to this agent.
         * 
         * - callInfo.isInACall() will tell you whether the agent is currently in a call or not.
         * 
         * - if you want more informatino about the call, you can do
         *      callInfo.getLastCall().{any method the object of class Call has.}
         * 
         * - for example, if you want the customer which is in the call, you can do that by:
         *      callInfo.getLastCall().getCaller()
         * 
         * - or to get the problem:
         *      callInfo.getLastCall().getCaller().problemInfo.getProblem()
         * 
         * - so, in conclusion, everything you need to manipulate the outcome of the call is in the callInfo object.
         */
    }

    public CallInfo callInfo = new CallInfo();

    public void assignCall(Call call) {
        this.callInfo.called(call);
    }

    public String getStringInfo() {
        return  
        "ID: " + getId() +
        "\nName: " + getName() +
        "\nDepartment: " + getDepartment().getName() +
        "\nJoin Date: " + getJoinDate();
    }

    @Override
    protected String getTag() {
        return "Agent: ";
    }
}

class RandomSelect {
	// public static <T extends Enum<?>> T getRandomEnumValue(Class<T> enumClass) {
    //     // Use values() method to get an array of enum constants
    //     T[] values = enumClass.getEnumConstants();

    //     // Generate a random index
    //     Random random = new Random();
    //     int randomIndex = random.nextInt(values.length);

    //     // Return the enum constant at the random index
    // //     return values[randomIndex];
    // }
    public static <T extends Enum<?>> T getRandomEnumValue(Class<T> enumClass,int removeFromEnd) {
        // Use values() method to get an array of enum constants
        T[] values = enumClass.getEnumConstants();

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(values.length-removeFromEnd);

        // Return the enum constant at the random index
        return values[randomIndex];
    }
}