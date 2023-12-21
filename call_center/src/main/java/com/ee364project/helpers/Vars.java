package com.ee364project.helpers;

/**
 * This class contains all the static variables used throughout the project.
 */
public class Vars {
    /**
     * A boolean variable that is set to true when the project phase is active.
     */
    public static boolean projectPhase = false;
    /**
     * The default output folder where all the generated files are stored.
     */
    public static final String DEFAULT_OUTPUT_FOLDER = "./Output/";
    /**
     * The prefix used for all the classes in the project.
     */
    public static final String projectPrefix = "com.ee364project.";
    /**
     * A constant used to represent a null value.
     */
    public static final String NONE = "NONE";
    /**
     * A constant used to represent a null 2D array.
     */
    public static final String[] NONE2D = new String[] { "NONE" };
    /**
     * A constant used to represent a default ID value.
     */
    public static final String DEFALT_ID = "00000000";
    /**
     * A constant used to represent the time increment used in the simulation.
     */
    public static final long TIMEINC = 1;

    /**
     * The {@code DataClasses} class provides constants for various data types used
     * in the application.
     * They represent the names of classes in the project where objects are created
     * from a CSV file
     */
    public static final class DataClasses {
        /**
         * The name of the Customer class.
         */
        public static final String Customer = "Customer";
        /**
         * The name of the Problem class.
         */
        public static final String Problem = "Problem";
        /**
         * The name of the Agent class.
         */
        public static final String Agent = "Agent";
        /**
         * The name of the Department class.
         */
        public static final String Department = "Department";
    }
}
