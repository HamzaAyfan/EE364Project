package com.ee364project;

import java.util.ArrayList;
import java.util.Random;

/**
 * The {@code Person} class represents an entity with a name and a tag.
 * All instances of the class share a common list, {@code allPersons}, which
 * stores references to every created person.
 *
 * 
 * Each person has a randomly generated tag, and the class provides methods
 * to access and manipulate the person's information.
 *
 * 
 * <b>Fields:</b>
 * <ul>
 * <li>{@code allPersons}: A static {@code ArrayList} containing references to
 * all created persons.</li>
 * <li>{@code name}: The name of the person.</li>
 * <li>{@code random}: A {@code Random} instance for generating random
 * values.</li>
 * <li>{@code tag}: The tag associated with the person.</li>
 * </ul>
 * 
 * @author Team 2
 */
public abstract class Person implements HasData, Simulated {
    private static ArrayList<Person> allPersons = new ArrayList<>();
    private String name;

    /**
     * A class that represents a person in the simulation.
     * 
     * @param name the name of the person
     */
    Person(String name) {
        this.name = name;

        allPersons.add(this);
    }

    /**
     * Returns the name of the person
     * 
     * @return the name of the person
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the person
     * 
     * @param name the new name of the person
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the number of milliseconds of sleep time for the person
     * 
     * @return the number of milliseconds of sleep time for the person
     */
    public int sleeptime() {
        return 0;
    }

    /**
     * Returns the tag of the person for display in {@code DialogeBox}
     * 
     * @return the tag of the person
     */
    protected abstract String getTag();
}
