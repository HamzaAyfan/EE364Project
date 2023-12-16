package com.ee364project.helpers;

/**
 * The Ratio class represents a ratio value between 0 and 1.
 * It provides basic arithmetic operations and comparison methods for ratios.
 *
 * @author Team 2
 * @version 1.1.0
 */
public class Ratio {
    private double value;

    /**
     * Default tolerance value for comparisons.
     */
    private static final double DEFAULT_TOL = 0.0001;

    /**
     * Constructs a Ratio object with the specified value.
     *
     * @param value The value of the ratio.
     */
    public Ratio(double value) {
        this.value = validate(value);
    }

    /**
     * Constructs a Ratio object with the same value as the given ratio.
     *
     * @param ratio The ratio to copy.
     */
    public Ratio(Ratio ratio) {
        this.value = ratio.value;
    }

    /**
     * Constructs a Ratio object with a random value between 0 and 1.
     */
    public Ratio() {
        this(Utilities.random.nextDouble());
    }

    /**
     * Gets the value of the ratio.
     *
     * @return The value of the ratio.
     */
    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Sets the value of the ratio.
     *
     * @param value The new value of the ratio.
     */
    public Ratio add(Ratio other) {
        double val = this.value + other.value;
        return new Ratio(validate(val));
    }

    /**
     * Adds another ratio to this ratio.
     *
     * @param other The ratio to add.
     * @return A new Ratio object representing the sum.
     */
    public Ratio add(double other) {
        return add(new Ratio(other));
    }

    /**
     * Subtracts another ratio from this ratio.
     *
     * @param other The ratio to subtract.
     * @return A new Ratio object representing the result of the subtraction.
     */
    public Ratio sub(Ratio other) {
        double val = this.value - other.value;
        return new Ratio(validate(val));
    }

    /**
     * Subtracts a double value from this ratio.
     *
     * @param other The double value to subtract.
     * @return A new Ratio object representing the result of the subtraction.
     */
    public Ratio sub(double other) {
        return sub(new Ratio(other));
    }

    /**
     * Subtracts this ratio from another ratio (reverse subtraction).
     *
     * @param other The ratio to subtract from.
     * @return A new Ratio object representing the result of the reverse
     *         subtraction.
     */
    public Ratio rsub(Ratio other) {
        double val = other.value - this.value;
        return new Ratio(validate(val));
    }

    /**
     * Subtracts a double value from this ratio in reverse (reverse subtraction).
     *
     * @param other The double value to subtract from.
     * @return A new Ratio object representing the result of the reverse
     *         subtraction.
     */
    public Ratio rsub(double other) {
        return sub(new Ratio(other));
    }

    /**
     * Multiplies this ratio by another ratio.
     *
     * @param other The ratio to multiply by.
     * @return A new Ratio object representing the result of the multiplication.
     */
    public Ratio mul(Ratio other) {
        double val = this.value * other.value;
        return new Ratio(validate(val));
    }

    /**
     * Multiplies this ratio by a double value.
     *
     * @param other The double value to multiply by.
     * @return A new Ratio object representing the result of the multiplication.
     */
    public Ratio mul(double other) {
        return mul(new Ratio(other));
    }

    /**
     * Compares this ratio to another ratio with a specified tolerance.
     *
     * @param other The ratio to compare to.
     * @param tol   The tolerance value for the comparison.
     * @return 0 if the ratios are equal within the tolerance, 1 if this ratio is
     *         greater,
     *         -1 if the other ratio is greater.
     */
    public int compare(Ratio other, double tol) {
        double trueVal = this.value - other.value;
        double val = Math.abs(trueVal);
        if (val < tol) {
            return 0;
        } else if (trueVal > 0) {
            return 1;
        } else
            return -1;
    }

    /**
     * Compares this ratio to another ratio with the default tolerance.
     *
     * @param other The ratio to compare to.
     * @return 0 if the ratios are equal within the default tolerance, 1 if this
     *         ratio is greater,
     *         -1 if the other ratio is greater.
     */
    public int compare(Ratio other) {
        return compare(other, DEFAULT_TOL);
    }

    /**
     * Compares this ratio to a double value with a specified tolerance.
     *
     * @param other The double value to compare to.
     * @param tol   The tolerance value for the comparison.
     * @return 0 if the ratios are equal within the tolerance, 1 if this ratio is
     *         greater,
     *         -1 if the other ratio is greater.
     */
    public int compare(double other, double tol) {
        return compare(new Ratio(other), tol);
    }

    /**
     * Compares this ratio to a double value with the default tolerance.
     *
     * @param other The double value to compare to.
     * @return 0 if the ratios are equal within the default tolerance, 1 if this
     *         ratio is greater,
     *         -1 if the other ratio is greater.
     */
    public int compare(double other) {
        return compare(new Ratio(other), DEFAULT_TOL);
    }

    /**
     * Validates a given double value to ensure it falls within the range [0, 1].
     * If the value is greater than 1, it is set to 1. If the value is less than 0,
     * it is set to 0.
     *
     * @param val The double value to be validated.
     * @return The validated double value within the range [0, 1].
     */
    private static double validate(double val) {
        if (val > 1) {
            return 1;
        } else if (val < 0) {
            return 0;
        } else {
            return val;
        }
    }

    /**
     * Generates a random Ratio object with a value between 0 and 1.
     *
     * @return A new Ratio object with a random value.
     */
    public static Ratio getRandRatio() {
        return new Ratio(Utilities.random.nextDouble());
    }

    /**
     * Returns a string representation of the Ratio object.
     *
     * @return A string representation of the ratio value.
     */
    @Override
    public String toString() {
        return "" + this.value;
    }

    /**
     * Checks if a randomly generated value is greater than the current ratio.
     *
     * @return true if the random value is greater, false otherwise.
     */
    public boolean check() {
        return (this.compare(Utilities.random.nextDouble()) > 0);
    }

    /**
     * Computes the complement of the ratio (1 - current ratio).
     *
     * @return The complement ratio.
     */
    public Ratio complement() {
        return this.rsub(1);
    }
}
