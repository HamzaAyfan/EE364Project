package com.ee364project.helpers;


public class Ratio {
    private double value;

    private static final double DEFAULT_TOL = 0.0001;
    public Ratio(double value) {
        this.value = validate(value);
    }

    public Ratio(Ratio ratio) {
        this.value = ratio.value;
    } 

    public Ratio() {
        this(Utilities.random.nextDouble());
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Ratio add(Ratio other) {
        double val = this.value + other.value;
        return new Ratio(validate(val));
    }

    public Ratio add(double other) {
        return add(new Ratio(other));
    }

    public Ratio sub(Ratio other) {
        double val = this.value - other.value;
        return new Ratio(validate(val));
    }

    public Ratio sub(double other) {
        return sub(new Ratio(other));
    }


    public Ratio rsub(Ratio other) {
        double val = other.value - this.value;
        return new Ratio(validate(val));
    }

    public Ratio rsub(double other) {
        return sub(new Ratio(other));
    }

    public Ratio mul(Ratio other) {
        double val = this.value * other.value;
        return new Ratio(validate(val));
    }

    public Ratio mul(double other) {
        return mul(new Ratio(other));
    }
    
    public int compare(Ratio other, double tol) {
        double trueVal = this.value - other.value;
        double val = Math.abs(trueVal);
        if (val < tol) {
            return 0;
        } else if (trueVal > 0) {
            return 1;
        } else return -1;
    }

    public int compare(Ratio other) {
        return compare(other, DEFAULT_TOL);
    }

    public int compare(double other, double tol) {
        return compare(new Ratio(other), tol);
    }

    public int compare(double other) {
        return compare(new Ratio(other), DEFAULT_TOL);
    }
    private static double validate(double val) {
        if (val > 1) {
            return 1;
        } else if (val < 0) {
            return 0;
        } else {
            return val;
        }
    }

    // same as no arg constructor.
    public static Ratio getRandRatio() {
        return new Ratio(Utilities.random.nextDouble());
    }

    @Override
    public String toString() {
        return "" + this.value;
    }

    public boolean check() {
        return (this.compare(Utilities.random.nextDouble()) > 0);
    }

    public Ratio complement() {
        return this.rsub(1);
    }
}
