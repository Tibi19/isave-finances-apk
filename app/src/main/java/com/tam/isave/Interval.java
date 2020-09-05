package com.tam.isave;

public class Interval extends Category {

    private static final String DEFAULT_NAME = "interval";

    private int days; // Number of days in this interval.
    private int id; // Should represent object's index in parent's array of Intervals.

    public Interval(int id, int days, double goal) {
        super(DEFAULT_NAME + id, goal);
        this.days = days;
        this.id = id;
    }

    // Handles the value change of a modified transaction.
    // Calls modifyTransaction of the superclass with a null transaction.
    // Transaction handling of the superclass will not be reached as it has to resolve a history is not null check.
    public boolean modifyPayment(double valueDiff) {
        return super.modifyPayment(null, valueDiff);
    }

    public boolean modify(double goal) {
        return super.modify(DEFAULT_NAME + id, goal);
    }

    public int getDays() {
        return days;
    }
}
