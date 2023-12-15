package com.ee364project;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.ee364project.helpers.Ratio;

import javafx.beans.property.SimpleIntegerProperty;

public class Timekeeper {
    private static LocalDateTime startPoint = LocalDateTime.of(2001, 1, 1, 0, 0, 0);
    private static int step = 1; // 60 * 60 * 24;
    private static SimpleIntegerProperty time = new SimpleIntegerProperty(0);
    private static long delayMs = 100;
    private static long scaller = 1;

    public static long getScaller() {
        return scaller;
    }

    public static void setScaller(long newScaller) {
        scaller = newScaller;
    }

    public static void setDelayMs(long newDelayMs) {
        if (newDelayMs < 1) {
            return;
        }
        delayMs = newDelayMs;
    }

    public static long getDelayMs() {
        return delayMs;
    }

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

    public static Ratio adjustedChance(Ratio originalChance, long originalPeriod, long newPeriod) {
        return new Ratio( 1 - Math.pow(1 - originalChance.getValue(), (double) newPeriod / (double) originalPeriod) );
    }

    public static Ratio adjustedChance(Ratio originalChance, long originalPeriod) {
        return adjustedChance(originalChance, originalPeriod, Timekeeper.step);
    }

    public static long getSecondsInDay(int n) {
        return n * 24 * 60 * 60;
    }

    public static long getSecondsInWeek(int n) {
        return n * getSecondsInDay(7);
    }

    public static long getSecondsInMonth(int n) {
        return n * getSecondsInDay(30);
    }

    public static long getSecondsInYear(int n) {
        return n * getSecondsInDay(365);
    }
}
