package com.ee364project.exceptions;

/**
 * Exception class for handling invalid ratio values.
 */
public class InvalidRatioValueException extends Exception {

    /**
     * Constructs a new InvalidRatioValueException with the given value.
     * 
     * @param value The invalid ratio value.
     */
    public InvalidRatioValueException(double value) {
        super("Ratio must be from 0 to 1 not: " + value);
    }
}
