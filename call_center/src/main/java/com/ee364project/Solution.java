package com.ee364project;

import java.util.ArrayList;
import java.util.HashSet;

import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

import java.util.Random;

/**
 * A class that represents a Solution to a Problem in the simulation.
 * 
 * @author Team 2
 */
public class Solution implements Cloneable {
    private static Random random = new Random();
    // public static HashMap<Problem, ArrayList<Solution>> allSolutions = new
    // HashMap<>();
    public String[] customerIntro;
    public String[] customerResponses;
    public String[] agentIntro;
    public String[] agentResponses;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Returns a random intro string from the given array of intro strings
     * 
     * @param person the person whose intro is being retrieved
     * @return a random intro string from the given array of intro strings
     */
    public String getRandomIntro(Person person) {
        String[] intro = null;
        if (person instanceof Agent) {
            intro = agentIntro;
        } else {
            intro = customerIntro;
        }
        int selectedIndex = (int) (Math.random() * intro.length);
        return intro[selectedIndex];
    }

    /**
     * Checks if two solutions are equal
     * 
     * @param other the other solution to compare with
     * @return true if the two solutions are equal, false otherwise
     */
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

    /**
     * Creates a new solution with the given problem, customer intro, customer
     * responses, agent intro, and agent responses
     * 
     * @param problem           the problem to which the solution belongs
     * @param customerIntro     the customer intro strings
     * @param customerResponses the customer response strings
     * @param agentIntro        the agent intro strings
     * @param agentResponses    the agent response strings
     */
    public Solution(Problem problem, String[] customerIntro, String[] customerResponses, String[] agentIntro,
            String[] agentResponses) {

        // TODO:
        // FIXME: apparently some part of the program is calling this method with an
        // empty arguemnt like customerResponses = [],
        // for now this checks if that happens and replaces it with a random 1
        // String[]/.
        if (customerIntro.length == 0) {
            this.customerIntro = Utilities.getRandomStringArray(1);
        } else {
            this.customerIntro = customerIntro;
        }

        if (customerResponses.length == 0) {
            this.customerResponses = Utilities.getRandomStringArray(1);
        } else {
            this.customerResponses = customerResponses;
        }

        if (agentIntro.length == 0) {
            this.agentIntro = Utilities.getRandomStringArray(1);
        } else {
            this.agentIntro = agentIntro;
        }
        if (agentResponses.length == 0) {
            this.agentResponses = Utilities.getRandomStringArray(1);
        } else {
            this.agentResponses = agentResponses;
        }
        // if (allSolutions.get(problem) == null) {
        // ArrayList<Solution> arr = new ArrayList<>();
        // arr.add(this);
        // allSolutions.put(problem, arr);
        // problem.solutions = arr;
        // } else {
        // allSolutions.get(problem).add(this);
        // }
    }

    /**
     * Checks if a solution has no intro, response, or both
     * 
     * @param solution the solution to check
     * @return true if the solution has no intro, response, or both, false otherwise
     */
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

    /**
     * Checks if a solution has at least one intro, response, or both
     * 
     * @param solution the solution to check
     * @return true if the solution has at least one intro, response, or both, false
     *         otherwise
     */
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

    /**
     * Removes all empty solutions from an array of solutions
     * 
     * @param solutions the array of solutions to remove empty solutions from
     * @return the array of solutions without empty solutions
     */
    public static ArrayList<Solution> removeEmptySolutions(ArrayList<Solution> solutions) {
        HashSet<Solution> solutionsClone = new HashSet<>();
        for (Solution solution : solutions) {
            solutionsClone.add(solution);
        }
        for (Solution solution : solutionsClone) {
            if (checkIfAllEmptySolution(solution)) { // NOTE: maybe change to any instead of all.
                solutions.remove(solution);
            }
        }
        return solutions;
    }

    /**
     * Removes all empty solutions from a problem's array of solutions
     * 
     * @param problem the problem whose array of solutions to remove empty solutions
     *                from
     * @return the problem's array of solutions without empty solutions
     */
    public static ArrayList<Solution> removeEmptySolutions(Problem problem) {
        return removeEmptySolutions(problem.solutions);
    }

    /**
     * Adds a random solution to a problem
     * 
     * @param problem the problem to which to add the random solution
     * @return the random solution that was added to the problem
     */
    public static Solution addRandomSolution(Problem problem) {
        int numberOfLines = random.nextInt(10) + 1;
        String[] customerIntro = Utilities.getRandomStringArray(1);
        String[] customerResponses = Utilities.getRandomStringArray(numberOfLines);
        String[] agentIntro = Utilities.getRandomStringArray(1);
        String[] agentResponses = Utilities.getRandomStringArray(numberOfLines);
        return new Solution(problem, customerIntro, customerResponses, agentIntro, agentResponses);
    }

    // 1. maintained data structure instead of HashSet
    // becomes solutions[n]
    // 2. or sort solution on the go.
    // Solutions.getSolutionFromLevel(int n): Solution // solution is the (n+1)th
    // best solution
}
