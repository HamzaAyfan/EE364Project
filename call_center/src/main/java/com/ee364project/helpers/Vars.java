package com.ee364project.helpers;

/**
 * This class contains all the static variables used throughout the project.
 */
public class Vars {
    public static boolean projectPhase = false;
    public static final String DEFAULT_OUTPUT_FOLDER = "./Output/";
    public static final String projectPrefix = "com.ee364project.";
    public static final String NONE = "NONE";
    public static final String[] NONE2D = new String[] { "NONE" };
    public static final String DEFALT_ID = "00000000";
    public static final long TIMEINC = 1;
    /**
     * The {@code DataClasses} class provides constants for various data types used in the application.
     * They represent the names of classes in the project where objects are created from a CSV file
     */
    public static final class DataClasses {
        public static final String Customer = "Customer";
        public static final String Problem = "Problem";
        public static final String Agent = "Agent";
        public static final String Department = "Department";
    }    
}
