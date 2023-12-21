package com.ee364project.exceptions;

/**
 * Exception class for handling invalid IDs.
 */
public class InvalidIdException extends Exception {
    /**
     * Constructs a new InvalidIdException with the specified ID.
     * 
     * @param id The invalid ID.
     */
    public InvalidIdException(String id) {
        super("Invalid id: " + id);
    }
}
