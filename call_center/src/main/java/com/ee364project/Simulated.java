package com.ee364project;

/**
 * An interface for objects that can be stepped through simulation.
 */
public interface Simulated {
    /**
     * Advances the state of the object by one simulation step.
     */
    void step();
}
