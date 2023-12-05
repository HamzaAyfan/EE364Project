package com.ee364project;

import java.util.HashMap;

import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Department implements HasData {
    private static HashMap<String, Department> allDepartments = new HashMap<>();
    private static final String[] HEADERS = new String[] { "name", };
    private static final String CLSNAME = "Department";
    public static final Department NO_DEPARTMENT = new Department(Vars.NONE);

    static {
        allDepartments.remove(Vars.NONE);
    }

    // this adds NONE as a possible department from the start
    // static {
    // allDepartments.put(Vars.NONE, NO_DEPARTMENT);
    // }

    public static Department easyNewDepartment(String name) {
        return new Department(name);
    }

    public static Department easyNewDepartment(Department department) {
        return department;
    }

    private String name;

    static public Department getDepartment(String name) {
        Department dep = allDepartments.get(name);
        if (dep == null) {
            dep = new Department(name);
        }
        return dep;
    }

    static public Department getDepartment() {
        return NO_DEPARTMENT;
    }

    public Department(String name) {
        this.name = name;
        allDepartments.put(name, this);
    }

    public Department() {
        this(Vars.NONE);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return Utilities.prettyToString(CLSNAME, this.name);
    }

    public static HashMap<String, Department> getAllDepartments() {
        return allDepartments;
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
        String[][] arr = new String[1][1];
        arr[0] = new String[] { this.name };
        return arr;
    }

    @Override
    public Department parseData(String[] dataFields) {
        this.name = dataFields[0];
        return this;
    }

    @Override
    public Department shuffle() {
        allDepartments.remove(this.name);
        this.name = Utilities.faker.company().industry();
        allDepartments.put(this.name, this);
        return this;
    }

    public static Department getRandomDepartment() {
        HashMap<String, Department> deps = Department.getAllDepartments();
        return deps.get(deps.keySet().toArray()[Utilities.random.nextInt(deps.size())]);
    }
}
