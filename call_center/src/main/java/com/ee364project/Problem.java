package com.ee364project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Problem implements HasData {
    public static ArrayList<Problem> allProblems = new ArrayList<>();
    public static final String CLSNAME = "Problem";
    public static final String[] HEADERS = new String[] { "identifier", "department", "customerIntro",
            "customerResponses", "agentIntro", "agentResponses" };
    public static final Problem NO_PROBLEM = new Problem();
    
    static {
        allProblems.remove(NO_PROBLEM.identifier);
    }

    public Department department;
    public String identifier;
    public ArrayList<Solution> solutions = new ArrayList<>();

    static public Problem getProblem(String identifier) {
        Problem problem = allProblems.get(allProblems.indexOf(identifier));
        if (identifier == null) {
            problem = new Problem();
            problem.identifier = identifier;
        }
        return problem;
    }

    static public Problem getProblem() {
        return NO_PROBLEM;
    }

    public Problem(String identifier, Department department, String[] customerIntro, String[] customerResponses,
            String[] agentIntro, String[] agentResponses) {
        this.identifier = identifier;
        this.department = department;
        new Solution(this, customerIntro, customerResponses, agentIntro, agentResponses);
        // this.solutions = Solution.allSolutions.get(this);
        allProblems.add(this);
    }

    public Problem() {
        this(Vars.NONE, Department.getRandomDepartment(), Vars.NONE2D, Vars.NONE2D, Vars.NONE2D, Vars.NONE2D);
    }

    @Override
    public String toString() {
        return Utilities.prettyToString(CLSNAME, this.identifier, this.department);
    }

    public Department getDepartment() {
        return this.department;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Solution[] getSolutions() {
        return this.solutions.toArray(new Solution[this.solutions.size()]);
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
        String[][] arr = new String[this.solutions.size()][];
        String[] inArr;
        int i = 0;
        for (Solution solution : this.solutions) {
            inArr = new String[] {
                    this.identifier,
                    this.department.getName(),
                    Utilities.joinStrings(solution.customerIntro, ";"),
                    Utilities.joinStrings(solution.customerResponses, ";"),
                    Utilities.joinStrings(solution.agentIntro, ";"),
                    Utilities.joinStrings(solution.agentResponses, ";"),
            };
            arr[i++] = inArr;
        }
        return arr;
    }   

    public static Problem checkRepeatedProblem(String identifier) {
        for (Problem PreExistingProblems:allProblems){
            String PreExistingIdentifier = PreExistingProblems.identifier;
            if (PreExistingIdentifier.equals(identifier)){
                return PreExistingProblems;
            }
        }
        return new Problem();
    }

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

    @Override
    public Problem shuffle() {
        allProblems.remove(this.identifier);
        this.identifier = Utilities.faker.azure().appServiceEnvironment();
        this.department = Department.getRandomDepartment();
        allProblems.add(this);
        this.solutions = Solution.removeEmptySolutions(this.solutions);
        solutions.add(Solution.addRandomSolution(this));
        return this;
    }

    public Solution getRandomSolution() {
        return this.solutions.toArray(new Solution[this.solutions.size()])[Utilities.random
                .nextInt(this.solutions.size())];
    }

    public boolean equals(Problem other) {
        return this.identifier == other.identifier;
    }

    public static HasData[] getAllProblems() {
        return allProblems.toArray(new Problem[allProblems.size()]);
    }
}
