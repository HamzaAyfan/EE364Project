package com.ee364project;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javafx.beans.property.SimpleIntegerProperty;

public class Timekeeper {
    private static LocalDateTime startPoint = LocalDateTime.of(2001, 1, 1, 0, 0, 0);
    private static int step = 1;//
    private static SimpleIntegerProperty time = new SimpleIntegerProperty(0);//

    public static int getTime() {
        return time.get(); // return time
    }

    public static SimpleIntegerProperty getTimeProperty(){
        return time; // return the observable time property
    }

    public static void step() {
        time.set(time.get() + step); // this is the way to increment the property similar to: time += step;
        // System.out.println("Time now: " + getProperTime());
    }

    public static int getStep() {
        return step;
    }

    public static void setStep(int newStep) {
        if (newStep < 1) {
            System.out.println("Step can't be less than 1. Ignored command.");
        } else {
            step = newStep;
        }
    }

    public static void setStartPoint(LocalDateTime newStartPoint) {
        startPoint = newStartPoint;
    }

    public static LocalDateTime getStartPoint() {
        return startPoint;
    }

    public static LocalDateTime getProperTime() {
        return startPoint.plus(getTime(), ChronoUnit.SECONDS);
    }
}
