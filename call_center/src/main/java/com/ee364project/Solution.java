

package com.ee364project;

import java.util.ArrayList;
import java.util.HashSet;


import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

import java.util.Random;

public class Solution implements Cloneable{
    private static Random random = new Random();
    // public static HashMap<Problem, ArrayList<Solution>> allSolutions = new HashMap<>();
    public String[] customerIntro;
    public String[] customerResponses;
    public String[] agentIntro;
    public String[] agentResponses;

    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
    public String getRandomIntro(Person person){
        String[] intro=null;
        if (person instanceof Agent){
            intro = agentIntro;
        }else{
            intro = customerIntro;
        }
        int selectedIndex = (int)(Math.random()*intro.length);
        return intro[selectedIndex];
    }

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

                // TODO: 
                // FIXME: apparently some part of the program is calling this method with an empty arguemnt like customerResponses = [],
                // for now this checks if that happens and replaces it with a random 1 String[]/.
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
        //     ArrayList<Solution> arr = new ArrayList<>();
        //     arr.add(this);
        //     allSolutions.put(problem, arr);
        //     problem.solutions = arr;
        // } else {
        //     allSolutions.get(problem).add(this);
        // }
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

    public static ArrayList<Solution> removeEmptySolutions(Problem problem) {
        return removeEmptySolutions(problem.solutions);
    }

    public static Solution addRandomSolution(Problem problem) {
        int numberOfLines = random.nextInt(10)+1;
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
