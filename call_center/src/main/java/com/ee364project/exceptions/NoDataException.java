package com.ee364project.exceptions;

/**
 * Custom exception indicating that no data is available.
 * This exception can be thrown when an operation or method
 * expects data to be present, but none is found.
 */
public class NoDataException extends Exception {
    
    /**
     * Constructs a new NoDataException with the given message.
     *
     * @param msg The descriptive message for the exception.
     */
    public NoDataException(String msg) {
        super(msg);
    }
}
