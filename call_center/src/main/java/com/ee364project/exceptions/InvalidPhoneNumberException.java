package com.ee364project.exceptions;

/**
 * Exception class for handling invalid phone numbers.
 */
public class InvalidPhoneNumberException extends Exception {
    /**
     * Constructs a new InvalidPhoneNumberException with the given phone number.
     *
     * @param phoneNumber the phone number that is invalid
     */
    public InvalidPhoneNumberException(String phoneNumber) {
        super("Invalid phone number" + phoneNumber);
    }
}
