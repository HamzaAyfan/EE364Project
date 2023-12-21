package com.ee364project;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.ee364project.exceptions.InvalidIdException;
import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

import net.datafaker.providers.base.Name;

import java.util.Random;

/**
 * The {@code Level} enum represents different levels of Agents.
 * It defines three levels: SAVEY, DEFULT, and CHALLENGED.
 * These levels are typically control the duration of the calls
 * and number of solutions seeked.
 */
enum Level {
    SAVEY,
    DEFULT,
    CHALLENGED
}

/**
 * This class represents an agent in the EE364 project.
 * 
 * @author Team 2
 *
 */
public class Agent extends Person {
    private static final String CLSNAME = "Agent";
    private static final String[] HEADERS = new String[] { "id", "name", "department", "joinDate" };
    private Level level;
    private String id;
    private Department department;
    private LocalDateTime joinDate;
    private ArrayList<Problem> problemsSeen = new ArrayList<>();
    private static ArrayList<Agent> allAgents = new ArrayList<>();

    /**
     * Returns an array of all agents in the system.
     * 
     * @return an array of all agents in the system
     */
    public Agent[] getAllAgents() {
        int size = allAgents.size();
        Agent[] agents = new Agent[size];
        return allAgents.toArray(agents);
    }

    /**
     * Creates a new agent with the given id, name, and department.
     * 
     * @param id         the id of the agent
     * @param name       the name of the agent
     * @param department the department of the agent
     * @throws InvalidIdException if the given id is not valid
     */
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

    /**
     * Assigns a level to the agent based on the given problem. If the agent has
     * already seen
     * the problem before, the level is set to "SAVEY." If the agent's department
     * matches the
     * problem's department, the level is randomly selected from the levels "SAVEY"
     * and "DEFULT."
     * Otherwise, the level is randomly selected from all available levels.
     *
     * @param problem The Problem object for which the level is to be assigned.
     */
    public void assignLevel(Problem problem) {
        for (Problem problemSeen : problemsSeen) {
            if (problemSeen == problem) {
                level = Level.SAVEY;
                return;
            }
        }
        if (this.getDepartment() == problem.getDepartment()) {
            level = RandomSelect.getRandomEnumValue(Level.class, 1);
        } else {
            level = RandomSelect.getRandomEnumValue(Level.class, 0);
        }
    }

    /**
     * Returns the level of the agent.
     * 
     * @return the level of the agent
     */
    public Level getlevel() {
        return level;
    }

    /**
     * Creates a new agent with a randomly generated id, name, and department.
     * 
     * @throws InvalidIdException if the randomly generated id is not valid
     */
    public Agent() throws InvalidIdException {
        this(Vars.DEFALT_ID, Vars.NONE, Department.getDepartment());
    }

    /**
     * Returns the join date of the agent.
     * 
     * @return the join date of the agent
     */
    public LocalDateTime getJoinDate() {
        return this.joinDate;
    }

    /**
     * Returns the department of the agent.
     * 
     * @return the department of the agent
     */
    public Department getDepartment() {
        return this.department;
    }

    /**
     * Returns the id of the agent.
     * 
     * @return the id of the agent
     */
    public String getId() {
        return this.id;
    }

    /**
     * Retrieves the name of the data type associated with this class.
     *
     * @return A string representing the name of the data type.
     */
    @Override
    public String getDataTypeName() {
        return CLSNAME;
    }

    /**
     * Retrieves an array of headers representing the fields or properties of the
     * data type.
     *
     * @return An array of strings containing the headers of the data type.
     */
    @Override
    public String[] getHeaders() {
        return HEADERS;
    }

    /**
     * Retrieves a two-dimensional array of data representing the values of the data
     * type.
     * Each row in the array corresponds to an instance of the data type, and each
     * column
     * contains specific properties or fields.
     *
     * @return A two-dimensional array of strings containing the data values of the
     *         data type.
     */
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

    /**
     * Parses an array of data fields and populates the properties of the Agent
     * instance with the
     * corresponding values. The order of the data fields should match the order
     * expected by the
     * {@link #getData()} method.
     *
     * @param dataFields An array of strings containing the data fields to be
     *                   parsed.
     * @return The Agent instance with properties populated based on the provided
     *         data fields.
     */
    @Override
    public Agent parseData(String[] dataFields) {
        this.id = dataFields[0];
        this.setName(dataFields[1]);
        this.department = Department.getDepartment(dataFields[2]);
        this.joinDate = LocalDateTime.parse(dataFields[3]);
        return this;
    }

    /**
     * Shuffles the properties of the Agent instance, generating new random values
     * for each property.
     * This method is useful for creating instances with randomized data for testing
     * or simulation purposes.
     *
     * @return The Agent instance with properties shuffled and populated with new
     *         random values.
     */
    @Override
    public Agent shuffle() {
        Name name = Utilities.faker.name();
        this.id = Utilities.faker.number().digits(8);
        this.setName(name.firstName());
        this.department = Department.getRandomDepartment();
        this.joinDate = Utilities.getRandLocalDateTime();
        return this;
    }

    /**
     * Returns a string containing information about the agent which is used in the
     * toggle feature.
     * 
     * @return a string containing information about the agent
     */
    public String getStringInfo() {
        Department department = getDepartment();
        return "ID: " + getId() +
                "\nName: " + getName() +
                "\nDepartment: " + department.getName() +
                "\nJoin Date: " + getJoinDate();
    }

    /**
     * Retrieves the tag associated with the Agent class, which is used for
     * identification or labeling purposes in the Dialogue window.
     *
     * @return A string containing the tag associated with the Agent class.
     */
    @Override
    protected String getTag() {
        return "Agent: ";
    }

    /**
     * Throws an {@link UnsupportedOperationException} to indicate that the 'step'
     * method is not implemented.
     * This method is typically used as a placeholder or a signal that the subclass
     * should provide its
     * own implementation for the 'step' behavior.
     *
     * @throws UnsupportedOperationException Always thrown to indicate that the
     *                                       'step' method is not implemented.
     */
    @Override
    public void step() {
        throw new UnsupportedOperationException("Unimplemented method 'step'");
    }
}

/**
 * This class provides utility methods for getting a Random Enum for Agent's
 * Level.
 * 
 * @author Team 2
 *
 */
class RandomSelect {
    public static <T extends Enum<?>> T getRandomEnumValue(Class<T> enumClass, int removeFromEnd) {
        // Use values() method to get an array of enum constants
        T[] values = enumClass.getEnumConstants();

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(values.length - removeFromEnd);

        // Return the enum constant at the random index
        return values[randomIndex];
    }
}