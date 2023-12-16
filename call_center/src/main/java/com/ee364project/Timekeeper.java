package com.ee364project;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.ee364project.helpers.Ratio;

import javafx.beans.property.SimpleIntegerProperty;


/**
 * This class is used to keep track of time. It provides methods for advancing time, getting the current time, and
 * calculating adjusted chances based on the current time.
 * 
 * @author Hamza Ayfan
 */
public class Timekeeper {
    private static LocalDateTime startPoint = LocalDateTime.of(2001, 1, 1, 0, 0, 0);
    private static int step = 1; // 60 * 60 * 24;
    private static SimpleIntegerProperty time = new SimpleIntegerProperty(0);
    private static long delayMs = 100;
   /**
     * Returns the current delay value in milliseconds.
     */
    public static void setDelayMs(long newDelayMs) {
        if (newDelayMs < 1) {
            return;
        }
        delayMs = newDelayMs;
    }

       /**
     * Sets the delay value in milliseconds.
     *
     * @param newDelayMs the new delay value in milliseconds
     */
    public static long getDelayMs() {
        return delayMs;
    }

       /**
     * Returns the current time in seconds.
     */
    public static int getTime() {
        return time.get(); // return time
    }

       /**
     * Returns the current time as an observable property.
     */
    public static SimpleIntegerProperty getTimeProperty(){
        return time; // return the observable time property
    }

        /**
     * Advances the time by one step.
     */
    public static void step() {
        time.set(time.get() + step); // this is the way to increment the property similar to: time += step;
        // System.out.println("Time now: " + getProperTime());
    }

        /**
     * Returns the current step value.
     */
    public static int getStep() {
        return step;
    }

    
    /**
     * Sets the step value.
     *
     * @param newStep the new step value
     */

    public static void setStep(int newStep) {
        if (newStep < 1) {
            System.out.println("Step can't be less than 1. Ignored command.");
        } else {
            step = newStep;
        }
    }

    /**
     * Sets the start point to a new value.
     *
     * @param newStartPoint the new start point
     */
    public static void setStartPoint(LocalDateTime newStartPoint) {
        startPoint = newStartPoint;
    }

    
    /**
     * Returns the current start point.
     */
    public static LocalDateTime getStartPoint() {
        return startPoint;
    }

        /**
     * Returns the current time, adjusted for the start point.
     */
    public static LocalDateTime getProperTime() {
        return startPoint.plus(getTime(), ChronoUnit.SECONDS);
    }

        /**
     * Calculates an adjusted chance based on the original chance and the original period.
     *
     * @param originalChance the original chance
     * @param originalPeriod the original period
     * @param newPeriod      the new period
     * @return the adjusted chance
     */
    public static Ratio adjustedChance(Ratio originalChance, long originalPeriod, long newPeriod) {
        return new Ratio( 1 - Math.pow(1 - originalChance.getValue(), (double) newPeriod / (double) originalPeriod) );
    }

        /**
     * Calculates an adjusted chance based on the original chance and the original period.
     *
     * @param originalChance the original chance
     * @param originalPeriod the original period
     * @return the adjusted chance
     */
    public static Ratio adjustedChance(Ratio originalChance, long originalPeriod) {
            return originalChance;
        // return adjustedChance(originalChance, originalPeriod, Timekeeper.step);
    }

        /**
     * Returns the number of seconds in a given number of days.
     *
     * @param n the number of days
     * @return the number of seconds in the given number of days
     */
    public static long getSecondsInDay(int n) {
        return n * 24 * 60 * 60;
    }

      /**
     * Returns the number of seconds in a given number of weeks.
     *
     * @param n the number of weeks
     * @return the number of seconds in the given number of weeks
     */
    public static long getSecondsInWeek(int n) {
        return n * getSecondsInDay(7);
    }

        /**
     * Returns the number of seconds in a given number of months.
     *
     * @param n the number of months
     * @return the number of seconds in the given number of months
     */
    public static long getSecondsInMonth(int n) {
        return n * getSecondsInDay(30);
    }

        /**
     * Returns the number of seconds in a given number of years.
     *
     * @param n the number of months
     * @return the number of seconds in the given number of years
     */
    public static long getSecondsInYear(int n) {
        return n * getSecondsInDay(365);
    }
}
