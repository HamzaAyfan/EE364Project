package com.ee364project;

import com.ee364project.file_manage.Csv;
import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Test {
    public static void main(String[] args) {
        Utilities.getFakeData(5, Vars.DataClasses.Department);
        Csv.write(Utilities.getFakeData(10, Vars.DataClasses.Problem), "call_center\\input\\Problem.csv");

        HasData[] data = Csv.read("call_center\\input\\Problem.csv");
        for (HasData datum : data) {
            System.out.println(datum);
        }
    }
    
}
