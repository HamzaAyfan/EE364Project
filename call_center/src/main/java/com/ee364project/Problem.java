package com.ee364project;

import java.util.ArrayList;

import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

/**
 * The {@code Problem} class represents an issue or scenario within a
 * department,
 * encapsulating information about the problem, its associated department,
 * and a list of solutions.
 * 
 * This class implements the {@link HasData} interface, providing methods
 * to interact with data, such as retrieving headers and data for CSV
 * processing.
 * 
 * The class includes static fields for managing a collection of all problems,
 * a common class name, and headers for CSV representation. It also defines a
 * special instance {@code NO_PROBLEM} for specific use cases.
 * 
 * 
 * <b>Fields:</b>
 * <ul>
 * <li>{@code ArrayList<Problem> allProblems}: A collection of all problems
 * managed by this class.</li>
 * <li>{@code String CLSNAME}: The class name used for identification and data
 * processing.</li>
 * <li>{@code String[] HEADERS}: Headers for CSV representation of problem
 * data.</li>
 * <li>{@code Problem NO_PROBLEM}: A special instance representing the absence
 * of a problem.</li>
 * <li>{@code Department department}: The department associated with the
 * problem.</li>
 * <li>{@code String identifier}: The unique identifier of the problem.</li>
 * <li>{@code ArrayList<Solution> solutions}: A list of solutions related to the
 * problem.</li>
 * </ul>
 * 
 * @author Team 2
 */
public class Problem implements HasData {
    private static ArrayList<Problem> allProblems = new ArrayList<>();

    /**
     * The class name for CSV.
     */
    public static final String CLSNAME = "Problem";
    /**
     * The header names for CSV.
     */
    public static final String[] HEADERS = new String[] { "identifier", "department", "customerIntro",
            "customerResponses", "agentIntro", "agentResponses" };
    /**
     * The null problem.
     */
    public static final Problem NO_PROBLEM = new Problem();
    private Department department;
    private String identifier;
    private ArrayList<Solution> solutions = new ArrayList<>();

    /**
     * Retrieves the list of all problems managed by the {@code Problem} class.
     *
     * @return An {@code ArrayList} containing all problems.
     */
    public static ArrayList<Problem> getProblemsList() {
        return allProblems;
    }

    /**
     * Retrieves the list of solutions associated with this {@code Problem}.
     *
     * @return An {@code ArrayList} containing solutions related to the problem.
     */
    public ArrayList<Solution> getSolutionsList() {
        return solutions;
    }

    /**
     * This static initialization block is responsible for removing a specific
     * problem
     * (likely a placeholder or special case) from the 'allProblems' list when the
     * class is loaded.
     * It ensures that the 'NO_PROBLEM' with its associated identifier is not
     * included in the list.
     */
    static {
        allProblems.remove(NO_PROBLEM.identifier);
    }

    /**
     * Returns the Problem object with the given identifier, creating a new Problem
     * object if no Problem with the given identifier exists.
     * 
     * @param identifier the identifier of the Problem object to retrieve
     * @return the Problem object with the given identifier, or a new Problem object
     *         if no Problem with the given identifier exists
     */
    static public Problem getProblem(String identifier) {
        Problem problem = null;
        for (Problem problemInLinkedList : allProblems) {
            if (identifier == problemInLinkedList.getIdentifier()) {
                return problemInLinkedList;
            }
        }
        problem = new Problem();
        problem.identifier = identifier;
        return problem;
    }

    /**
     * Gets a default Problem instance representing "no problem."
     *
     * @return The default Problem instance representing "no problem."
     */
    static public Problem getProblem() {
        return NO_PROBLEM;
    }

    /**
     * Creates a new Problem object with the given identifier, department, customer
     * intro, customer responses, agent intro, and agent responses.
     * 
     * @param identifier        the identifier of the Problem object
     * @param department        the department of the Problem object
     * @param customerIntro     the customer intro of the Problem object
     * @param customerResponses the customer responses of the Problem object
     * @param agentIntro        the agent intro of the Problem object
     * @param agentResponses    the agent responses of the Problem object
     */
    public Problem(String identifier, Department department, String[] customerIntro, String[] customerResponses,
            String[] agentIntro, String[] agentResponses) {
        this.identifier = identifier;
        this.department = department;
        new Solution(this, customerIntro, customerResponses, agentIntro, agentResponses);
        // this.solutions = Solution.allSolutions.get(this);
        allProblems.add(this);
    }

    /**
     * Creates a new Problem object with the random identifier, department, customer
     * intro, customer responses, agent intro, and agent responses.
     */
    public Problem() {
        this(Vars.NONE, Department.getRandomDepartment(), Vars.NONE2D, Vars.NONE2D, Vars.NONE2D, Vars.NONE2D);
    }

    /**
     * Returns the department of the Problem object
     * 
     * @return the department of the Problem object
     */
    public Department getDepartment() {
        return this.department;
    }

    /**
     * Returns the identifier of the Problem object
     * 
     * @return the identifier of the Problem object
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Returns an array of all the solutions associated with this problem
     * 
     * @return an array of all the solutions associated with this problem
     */

    public Solution[] getSolutions() {
        return this.solutions.toArray(new Solution[this.solutions.size()]);
    }

    /**
     * Returns the name of the data type associated with this object.
     * The data type name is retrieved from the constant CLSNAME.
     *
     * @return The name of the data type.
     * @see #CLSNAME
     */
    @Override
    public String getDataTypeName() {
        return CLSNAME;
    }

    /**
     * Returns the array of headers associated with this data object.
     * The headers are retrieved from the constant HEADERS.
     *
     * @return The array of headers for this data object.
     * @see #HEADERS
     */
    @Override
    public String[] getHeaders() {
        return HEADERS;
    }

    /**
     * Returns a two-dimensional array of strings representing the data associated
     * with this problem.
     * Each row in the array corresponds to a different solution of the problem.
     * The columns include the problem identifier, department name, and various
     * information from each solution.
     * The order of columns is as follows:
     * - Problem identifier
     * - Department name
     * - Agent introduction phrases (concatenated with ';')
     * - Customer response phrases (concatenated with ';')
     * - Agent introduction phrases (concatenated with ';')
     * - Agent response phrases (concatenated with ';')
     *
     * @return A two-dimensional array of strings representing the data for each
     *         solution of the problem.
     */
    @Override
    public String[][] getData() {
        String[][] arr = new String[this.solutions.size()][];
        String[] inArr;
        int i = 0;
        for (Solution solution : this.solutions) {
            inArr = new String[] {
                    this.identifier,
                    this.department.getName(),
                    Utilities.joinStrings(solution.getAgentIntro(), ";"),
                    Utilities.joinStrings(solution.getCustomerResponse(), ";"),
                    Utilities.joinStrings(solution.getAgentIntro(), ";"),
                    Utilities.joinStrings(solution.getAgentResponse(), ";"),
            };
            arr[i++] = inArr;
        }
        return arr;
    }

    /**
     * Returns the Problem object with the given identifier, creating a new Problem
     * object if no Problem with the given identifier exists.
     * 
     * @param identifier the identifier of the Problem object to retrieve
     * @return the Problem object with the given identifier, or a new Problem object
     *         if no Problem with the given identifier exists
     */
    public static Problem checkRepeatedProblem(String identifier) {
        for (Problem PreExistingProblems : allProblems) {
            String PreExistingIdentifier = PreExistingProblems.identifier;
            if (PreExistingIdentifier.equals(identifier)) {
                return PreExistingProblems;
            }
        }
        return new Problem();
    }

    /**
     * Parses an array of data fields and sets the corresponding attributes of the
     * Problem instance.
     * The order of data fields is as follows:
     * - Problem identifier
     * - Department name
     * - Agent introduction phrases (concatenated with ';')
     * - Customer response phrases (concatenated with ';')
     * - Agent introduction phrases (concatenated with ';')
     * - Agent response phrases (concatenated with ';')
     *
     * The parsed data is used to create a new Solution instance, and the solution
     * is added to the list of solutions
     * associated with this problem.
     *
     * @param dataFields An array of strings representing the data fields for the
     *                   problem.
     * @return The Problem instance with attributes set based on the parsed data.
     */
    @Override
    public Problem parseData(String[] dataFields) {
        this.identifier = dataFields[0];
        this.department = Department.getDepartment(dataFields[1]);
        String[] customerResponses = dataFields[3].split(";");
        String[] agentResponses = dataFields[5].split(";");
        String[] customerIntro = dataFields[2].split(";");
        String[] agentIntro = dataFields[4].split(";");
        Solution solution = new Solution(this, customerIntro, customerResponses, agentIntro, agentResponses);
        this.solutions.add(solution);
        return this;
    }

    /**
     * Shuffles the attributes of the Problem instance, generating a new identifier,
     * department, and solutions.
     * The shuffle involves removing the current instance from the global list of
     * problems, generating a new
     * identifier using external data (Azure app service environment in this case),
     * selecting a random department,
     * adding the instance back to the global list, removing empty solutions, and
     * adding a new random solution.
     *
     * @return The Problem instance after shuffling its attributes.
     */
    @Override
    public Problem shuffle() {
        allProblems.remove(this);
        this.identifier = Utilities.faker.azure().appServiceEnvironment(); // chain: external.
        this.department = Department.getRandomDepartment();
        allProblems.add(this);
        this.solutions = Solution.removeEmptySolutions(this.solutions);
        solutions.add(Solution.addRandomSolution(this));
        return this;
    }

    /**
     * Returns a random solution from the list of solutions associated with this
     * problem
     * 
     * @return a random solution from the list of solutions associated with this
     *         problem
     */
    public Solution getRandomSolution() {
        return this.solutions.toArray(new Solution[this.solutions.size()])[Utilities.random
                .nextInt(this.solutions.size())];
    }

    /**
     * Returns true if the given object is a Problem object and its identifier is
     * equal to this Problem object's identifier.
     * 
     * @param other the object to compare to
     * @return true if the given object is a Problem object and its identifier is
     *         equal to this Problem object's identifier, false otherwise
     */
    public boolean equals(Problem other) {
        return this.identifier == other.identifier;
    }

    /**
     * Returns an array of all the problems in the simulation.
     * 
     * @return an array of all the problems in the simulation
     */
    public static HasData[] getAllProblems() {
        return allProblems.toArray(new Problem[allProblems.size()]);
    }
}
