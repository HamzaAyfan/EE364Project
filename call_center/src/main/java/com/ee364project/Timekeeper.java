package com.ee364project;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.ee364project.helpers.Ratio;

import javafx.beans.property.SimpleIntegerProperty;


/**
 The {@code TimeManager} class manages time-related properties for simulation.
 * It includes a starting point, a step size, a time property, and a delay in milliseconds.
 *
 * <p><b>Fields:</b>
 * <ul>
 *   <li>{@code startPoint}: The starting point of the simulation time, set to January 1, 2001, 00:00:00.</li>
 *   <li>{@code step}: The step size representing the duration of each time step, initially set to 1 second.</li>
 *   <li>{@code time}: A {@code SimpleIntegerProperty} representing the current simulated time in seconds.</li>
 *   <li>{@code delayMs}: The delay in milliseconds used for controlling the simulation speed.</li>
 * </ul>
 * 
 * 
 * @author Hamza Ayfan
 *  
 */
public class Timekeeper {
    private static LocalDateTime startPoint = LocalDateTime.of(2001, 1, 1, 0, 0, 0);
    private static int step = 1; 
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
     * Gets the delay value in milliseconds.
     *
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
        int timeReceived = time.get();
        time.set(timeReceived + step); // this is the way to increment the property similar to: time += step;
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
        int time =getTime();
        return startPoint.plus(time, ChronoUnit.SECONDS);
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
        double power = (double) newPeriod / (double) originalPeriod;
        double chance = 1 - originalChance.getValue();
        return new Ratio( 1 - Math.pow(chance, power ));
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
