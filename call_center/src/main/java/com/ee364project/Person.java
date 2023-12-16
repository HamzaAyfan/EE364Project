package com.ee364project;

import java.util.ArrayList;
import java.util.Random;

import com.ee364project.helpers.Ratio;

/**
 * A class that represents a person in the simulation.
 * 
 * @author Team 2
 */
public abstract class Person implements HasData, Simulated {
    private static ArrayList<Person> allPersons = new ArrayList<>();
    private String name;
    Random random = new Random();
    protected String tag;

    private double defultRateOfSpeech = random.nextDouble();
    private double defultSoundLevel = random.nextDouble();
    private double hearingLevel = random.nextDouble();

    private double rateOfSpeech = defultRateOfSpeech;
    private double soundLevel = defultSoundLevel;

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
     * Increases the sound level of the person
     */
    public void increaseSoundLevel() {
        soundLevel = soundLevel * (1 + random.nextDouble() / 10);
    }

    /**
     * Decreases the sound level of the person
     */
    public void decreaseSoundLevel() {
        soundLevel = soundLevel * (1 - random.nextDouble() / 50);
    }

    /**
     * Speeds up the rate of speech of the person
     */
    public void speedUp() {
        rateOfSpeech = rateOfSpeech * (1 + random.nextDouble() / 50);
    }

    /**
     * Slows down the rate of speech of the person
     */
    public void slowDown() {
        rateOfSpeech = rateOfSpeech * (1 - random.nextDouble() / 10);
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
     * Returns the tag of the person
     * 
     * @return the tag of the person
     */
    protected abstract String getTag();
}
