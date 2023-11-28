package com.ee364project;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.ee364project.exceptions.InvalidIdException;
import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

public class Agent extends Person {
    private static ArrayList<Agent> allAgents = new ArrayList<>();
    public Agent[] getAllAgents() {
        return allAgents.toArray(new Agent[allAgents.size()]);
    }
    private static final String CLSNAME = "Agent";
    private static final String[] HEADERS = new String[]{"id", "name", "department", "joinDate"};
    // final String phone;
    // private int salary;
    private String id;
    private Department department;
    private LocalDateTime joinDate;


    // public Agent(String phone, int salary, Department department) {
    public Agent(String id, String name, Department department) throws InvalidIdException {
        // this.phone = phone;
        // this.salary = salary;
        super(name);
        if (!Utilities.validateId(id)) {
            throw new InvalidIdException(id);
        }
        this.id = id;
        this.department = department;
        this.joinDate = LocalDateTime.now();
        allAgents.add(this);
    }

    public Agent() throws InvalidIdException {
        this(Vars.DEFALT_ID, Vars.NONE, Department.NO_DEPARTMENT);
    }

    @Override
    public String toString() {
        return Utilities.prettyToString(CLSNAME, this.id, this.getName(), this.department.toString(), this.joinDate);
    }

    public LocalDateTime getJoinDate() {
        return this.joinDate;
    }
    
    public Department getDepartment() {
        return this.department;
    }

    public String getId() {
        return this.id;
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
    public String[] getData() {
        return new String[]{
            this.id,
            this.getName(),
            this.department.getName(),
            this.joinDate.toString()
        };
    }
    
    public String getStringInfo(){
        return 
                "ID: " + getId() +
                "\nName: " + getName() +
                "\nDepartment: " + getDepartment().getName() +
                "\nJoin Date: " + getJoinDate();
    }

    @Override
    public Agent parseData(String[] dataFields) {
        this.id = dataFields[0];
        this.setName(dataFields[1]);
        this.department = Department.getDepartment(dataFields[2]);
        this.joinDate = LocalDateTime.parse(dataFields[3]);
        return this;
    }

    @Override
    public Agent shuffle() {
        this.id = Utilities.faker.number().digits(8);
        this.setName(Utilities.faker.name().firstName());
        this.department = Department.getRandomDepartment();
        this.joinDate = Utilities.getRandLocalDateTime();
        return this;
    }

    @Override
    public void step() {
        // TODO
    }
}
