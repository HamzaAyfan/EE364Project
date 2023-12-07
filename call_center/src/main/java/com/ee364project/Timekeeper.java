package com.ee364project;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Timekeeper {
    private static LocalDateTime startPoint = LocalDateTime.of(2001, 1, 1, 0, 0, 0);
    private static long step = 1;
    private static long time = 0;

    public static long getTime() {
        return time;
    }

    public static void step() {
        time += step;
        // System.out.println("Time now: " + getProperTime());
    }

    public static long getStep() {
        return step;
    }

    public static void setStep(long newStep) {
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
