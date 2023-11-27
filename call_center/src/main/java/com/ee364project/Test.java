package com.ee364project;

import com.ee364project.file_manage.Csv;
import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Test {
    public static void main(String[] args) {
        Problem[] problems = new Problem[9];
        for (int i = 0; i < 3; i++) {
            Problem[] problems2 = getNProblemsWithMSolutions(3, i);
            for (int j = 0; j < 3; j++) {
                problems[(i * 3 + j)] = problems2[j];
            }
        }
        Csv.write(problems, "call_center\\output\\Problem.csv");
    }

    public static Problem[] getNProblemsWithMSolutions(int n, int m) {
        HasData[] hasData = Utilities.getFakeData(n, Vars.DataClasses.Problem);
        for (HasData datum : hasData) {
            Problem problem = (Problem) datum;
            // Solution.removeEmptySolutions(problem);
            for (int i = 0; i < m; i++) {
                Solution.addRandomSolution(problem);
            }
        }
        Problem[] problems = new Problem[n];
        int i = 0;
        for (HasData datum : hasData) {
            problems[i++] = (Problem) datum;
        }
        return problems;
    }
}
