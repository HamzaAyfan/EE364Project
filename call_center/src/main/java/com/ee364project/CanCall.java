package com.ee364project;

/**
 * The {@code CanCall} interface represents entities that have the ability to make calls.
 * Classes implementing this interface should provide an implementation for the {@link #makeCall()}
 * method, defining the specific behavior associated with initiating a call. 
 * 
 * In this simulation only the customer can intiate the call; however, this can be used to make agents also make calls
 * for sales purposes or even follow-ups
 */
public interface CanCall {
    /**
     * Initiates a call. Implementing classes should define the specific behavior
     * associated with making a call.
     */
    void makeCall();
}
