package com.ee364project;

import com.ee364project.file_manage.Csv;
import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Write {
    public static void main(String[] args) {
        Utilities.getFakeData(5, Vars.DataClasses.Department);
        Utilities.getFakeData(20, Vars.DataClasses.Problem);

        HasData[] customers = Utilities.getFakeData(20,Vars.DataClasses.Customer);
        HasData[] agents = Utilities.getFakeData(5,Vars.DataClasses.Agent);

        Csv.write(customers, "call_center\\output");
        Csv.write(agents, "call_center\\output");
    }   
}
