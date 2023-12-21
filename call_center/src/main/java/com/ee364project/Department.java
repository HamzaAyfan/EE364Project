package com.ee364project;

import java.util.HashMap;
import java.util.Set;

import com.ee364project.helpers.Utilities;
import com.ee364project.helpers.Vars;

import net.datafaker.Faker;
import net.datafaker.providers.base.Company;

/**
 * Represents a department implementing the HasData interface.
 * 
 * This class provides methods for creating, retrieving, and manipulating Department objects.
 * 
  *
 * @author Team 2
 * @version 1.0
 * @since 2023-12-21
 */
public class Department implements HasData {
    private static HashMap<String, Department> allDepartments = new HashMap<>();
    //contains all deparments
    private static final String[] HEADERS = new String[] { "name", };
    //Header of departments in CSV File
    private static final String CLSNAME = "Department";
    //class name for Department
    private static final Department NO_DEPARTMENT = new Department(Vars.NONE);
    //Constant representing no deparment
    private String name;
    // name of department

    /**
    * Creates a new department with the specified name.
    * 
    * This method provides a convenient way to instantiate a new {@code Department}
    * object with the given name. It is designed for simplicity and is equivalent
    * to calling the {@link Department#Department(String)} constructor directly.
    * 
    *
    * @param name The name of the new department.
    * @return A new {@code Department} instance with the specified name.
    * @see Department#Department(String)
    */

    public static Department easyNewDepartment(String name) {
        return new Department(name);
    }

    /**
    * Returns the provided department without creating a new instance.
    * 
    * This method simply returns the same {@code Department} instance that is
    * passed as a parameter. It is designed for cases where you want to provide
    * a method that does not create a new department but returns an existing one.
    * 
    *
    * @param department The department to be returned.
    * @return The same {@code Department} instance that is passed as a parameter.
    */

    public static Department easyNewDepartment(Department department) {
        return department;
    }

    /**
    * Retrieves a department by its name, creating a new instance if not found.
    * 
    * This method attempts to retrieve a {@code Department} instance from a collection
    * of all departments based on the provided name. If a department with the given
    * name is found, it is returned. Otherwise, a new {@code Department} instance is
    * created with the provided name and added to the collection before being returned.
    * 
    *
    * @param name The name of the department to retrieve or create.
    * @return The {@code Department} instance associated with the provided name. If
    *         the department does not exist, a new instance with the provided name is
    *         created and returned.
    */

     public static Department getDepartment(String name) {
        Department dep = allDepartments.get(name);
        if (dep == null) {
            dep = new Department(name);
        }
        return dep;
    }

    /**
    * Retrieves a constant instance representing the absence of a department.
    * 
    * This method returns a predefined constant instance of the {@code Department}
    * class, indicating the absence of a specific department. It is useful when you
    * want to represent scenarios where an object does not belong to any particular
    * department.
    * 
    * 
    * The constant instance returned by this method is shared among all callers
    * and remains unchanged throughout the program's execution.
    * 
    *
    * @return The predefined instance representing the absence of a department.
    */

    public static Department getDepartment() {
        return NO_DEPARTMENT;
    }

    /**
    * Constructs a new department with the specified name.
    * 
    * This constructor creates a new {@code Department} instance with the provided name.
    * The department is added to the collection of all departments for easy retrieval.
    * 
    *
    * @param name The name of the new department.
    */
    public Department(String name) {
        this.name = name;
        allDepartments.put(name, this);
    }

    /**
    * Constructs a new department with a default name.
    * 
    * This constructor creates a new {@code Department} instance with a default name,
    * equivalent to calling the parameterized constructor with the default name provided
    * by the {@link Vars#NONE} constant.
    * 
    */
    public Department() {
        this(Vars.NONE);
    }

    /**
    * Retrieves the name of the department.
    *
    * @return The name of the department.
    */
    public String getName() {
        return this.name;
    }
    /**
    * Returns a formatted string representation of the department.
    * 
    * This method generates a string containing the class name and the name of the department
    * in a human-readable format. It is intended for debugging and logging purposes.
    * 
    *
    * @return A formatted string representation of the department.
    */
    @Override
    public String toString() {
        return Utilities.prettyToString(CLSNAME, this.name);
    }
    /**
    * Retrieves a collection of all departments.
    * 
    * This method returns a reference to the internal collection containing all department instances.
    * It allows external access to the complete set of departments for informational or manipulative purposes.
    * 
    *
    * @return A {@code HashMap} containing all departments, where keys are department names, and values are department instances.
    */
    public static HashMap<String, Department> getAllDepartments() {
        return allDepartments;
    }
    /**
    * Retrieves the data type name associated with the department.
    * 
    * This method returns the class name representing the data type of the department.
    * It is used to identify the type of data when working with classes that implement
    * the {@code HasData} interface.
    * 
    *
    * @return The data type name associated with the department.
    */
    @Override
    public String getDataTypeName() {
        return CLSNAME;
    }
    /**
    * Retrieves the headers representing data fields in the department.
    * 
    * This method returns an array of strings representing the headers or names of data fields
    * associated with the department. It is used in conjunction with the {@code getData} method
    * when working with classes that implement the {@code HasData} interface.
    * 
    *
    * @return An array of strings representing the headers of data fields in the department.
    */
    @Override
    public String[] getHeaders() {
        return HEADERS;
    }
    /**
    * Retrieves the data representation of the department.
    * 
    * This method returns a two-dimensional array of strings representing the data
    * associated with the department. The array typically includes a row of headers
    * obtained from the {@code getHeaders} method and a row of data fields.
    * 
    *
    * @return A two-dimensional array of strings representing the data of the department.
    */
    @Override
    public String[][] getData() {
        String[][] arr = new String[1][1];
        arr[0] = new String[] { 
            this.name };
        return arr;
    }
    /**
    * Parses an array of data fields and updates the department's properties.
    * 
    * This method takes an array of strings representing data fields and updates the
    * properties of the department accordingly. The order and format of data fields
    * should match the expected structure defined by the {@code getHeaders} and {@code getData}
    * methods when working with classes that implement the {@code HasData} interface.
    * 
    *
     * @param dataFields An array of strings representing data fields for the department.
    * @return The updated department instance.
    */
    @Override
    public Department parseData(String[] dataFields) {
        this.name = dataFields[0];
        return this;
    }
    /**
    * Shuffles the department, updating its name with a random department name.
    * 
    * This method removes the department from the collection of all departments, generates
    * a new random industry name using a utility method, updates the department's name, and
    * adds it back to the collection. It is designed for scenarios where a department's name
    * needs to be refreshed randomly.
    * 
    *
    * @return The updated department instance with a new random name.
    */
    @Override
    public Department shuffle() {
        String name = this.name;
        allDepartments.remove(name);
        Faker faker = Utilities.faker;
        Company company = faker.company();
        this.name = company.industry();
        allDepartments.put(this.name, this);
        return this;
    }
    /**
    * Retrieves a randomly selected department from the collection of all departments.
    * 
    * This method returns a randomly selected {@code Department} instance from the
    * collection of all departments. It is useful when you need to obtain a department
    * at random for various scenarios, such as simulations or testing.
    * 
    *
    * @return A randomly selected {@code Department} instance.
    */
    public static Department getRandomDepartment() {
        HashMap<String, Department> deps = Department.getAllDepartments();
        int size = deps.size();
        int randomIndex = Utilities.random.nextInt(size);
        Set<String> set = deps.keySet(); 
        return deps.get(set.toArray()[randomIndex]);
    }
}
