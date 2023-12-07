package com.ee364project;

import java.util.ArrayList;
import java.util.Random;

import com.ee364project.helpers.Ratio;

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

    Person(String name) {
        this.name = name;

        allPersons.add(this);
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void increaseSoundLevel(){
        soundLevel = soundLevel*(1 + random.nextDouble()/10);
    }

    public void decreaseSoundLevel(){
        soundLevel = soundLevel*(1 - random.nextDouble()/50);
    }

    public void speedUp(){
        rateOfSpeech = rateOfSpeech*(1 + random.nextDouble()/50);
    }

    public void slowDown(){
        rateOfSpeech = rateOfSpeech*(1 - random.nextDouble()/10);
    }

    public int sleeptime(){
        return 0;
    }

    protected abstract String getTag();
}
