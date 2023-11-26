package com.ee364project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Problem implements HasData {
    public static HashMap<String, Problem> allProblems = new HashMap<>();
    public static final String CLSNAME = "Problem";
    public static final String[] HEADERS = new String[] { "identifier", "department", "customerIntro",
            "customerResponses", "agentIntro", "agentResponses" };
    public static final Problem NO_PROBLEM = new Problem();

    public Department department;
    public String identifier;
    public HashSet<Solution> solutions;

    static public Problem getProblem(String identifier) {
        Problem problem = allProblems.get(identifier);
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
        this.solutions = Solution.allSolutions.get(this);
        allProblems.put(this.identifier, this);
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
            arr[i] = inArr;
        }
        return arr;
    }

    @Override
    public Problem parseData(String[] dataFields) {
        this.identifier = dataFields[0];
        this.department = Department.getDepartment(dataFields[1]);
        ArrayList<String> d1 = new ArrayList<>();
        ArrayList<String> d2 = new ArrayList<>();
        String[] ds = dataFields[2].split(";");
        for (int i = 0; i < ds.length; i++) {
            if (i % 2 == 0) {
                d2.add(ds[i]);
            } else {
                d1.add(ds[i]);
            }
        }
        String[] customerResponses = d1.toArray(new String[d1.size()]);
        String[] agentResponses = d2.toArray(new String[d2.size()]);
        String[] customerIntro = dataFields[3].split(";");
        String[] agentIntro = dataFields[4].split(";");
        this.solutions = Solution.removeEmptySolutions(this.solutions);
        new Solution(this, customerIntro, customerResponses, agentIntro, agentResponses);
        return this;
    }

    @Override
    public Problem shuffle() {
        this.identifier = Utilities.faker.azure().appServiceEnvironment();
        this.department = Department.getRandomDepartment();
        allProblems.put(this.identifier, this);
        this.solutions = Solution.removeEmptySolutions(this.solutions);
        Solution.addRandomSolution(this);
        return this;
    }

    public static Problem[] getAllProblems() {
        return allProblems.values().toArray(new Problem[allProblems.size()]);
    }

    public Solution getRandomSolution() {
        return this.solutions.toArray(new Solution[this.solutions.size()])[Utilities.random.nextInt(this.solutions.size())];
    }

    public boolean equals(Problem other) {
        return this.identifier == other.identifier;
    }

    // TODO: add problem related stuff like `state`. Note: Did that in
    // Customer.ProblemState.
}

class Solution {
    public static HashMap<Problem, HashSet<Solution>> allSolutions = new HashMap<>();
    public String[] customerIntro;
    public String[] customerResponses;
    public String[] agentIntro;
    public String[] agentResponses;

    public boolean equals(Solution other) {
        if (this.customerIntro != other.customerIntro) {
            return false;
        }
        if (this.customerResponses != other.customerResponses) {
            return false;
        }
        if (this.agentIntro != other.agentIntro) {
            return false;
        }
        if (this.agentResponses != other.agentResponses) {
            return false;
        }
        return true;
    }

    public Solution(Problem problem, String[] customerIntro, String[] customerResponses, String[] agentIntro,
            String[] agentResponses) {
        this.customerIntro = customerIntro;
        this.customerResponses = customerResponses;
        this.agentIntro = agentIntro;
        this.agentResponses = agentResponses;
        if (allSolutions.get(problem) == null) {
            HashSet<Solution> arr = new HashSet<>();
            arr.add(this);
            allSolutions.put(problem, arr);
        } else {
            allSolutions.get(problem).add(this);
        }
    }
    
    public static boolean checkIfAllEmptySolution(Solution solution) {
        if (solution.customerIntro != Vars.NONE2D) {
            return false;
        }
        if (solution.customerResponses != Vars.NONE2D) {
            return false;
        }
        if (solution.agentIntro != Vars.NONE2D) {
            return false;
        }
        if (solution.agentResponses != Vars.NONE2D) {
            return false;
        }
        return true;
    }

    public static boolean checkIfAnyEmptySolution(Solution solution) {
        if (solution.customerIntro == Vars.NONE2D) {
            return true;
        }
        if (solution.customerResponses == Vars.NONE2D) {
            return true;
        }
        if (solution.agentIntro == Vars.NONE2D) {
            return true;
        }
        if (solution.agentResponses == Vars.NONE2D) {
            return true;
        }
        return false;
    }

    public static HashSet<Solution> removeEmptySolutions(HashSet<Solution> solutions) {
        HashSet<Solution> solutionsClone = new HashSet<>();
        for (Solution solution : solutions) {
            solutionsClone.add(solution);
        }
        for (Solution solution : solutions) {
            if (checkIfAllEmptySolution(solution)) {  // NOTE: maybe change to any instead of all.
                solutionsClone.remove(solution);
            }
        }
        return solutionsClone;
    }

    public static Solution addRandomSolution(Problem problem) {
        String[] customerIntro = Utilities.getRandomStringArray();
        String[] customerResponses = Utilities.getRandomStringArray();
        String[] agentIntro = Utilities.getRandomStringArray();
        String[] agentResponses = Utilities.getRandomStringArray();
        return new Solution(problem, customerIntro, customerResponses, agentIntro, agentResponses);
    }

    // only thing missing is:
    //  1. maintained data structure instead of HashSet
    //      becomes solutions[n]
    //  2. or sort solution on the go.
    //   Solutions.getSolutionFromLevel(int n): Solution // solution is the (n+1)th best solution
    // 
}
