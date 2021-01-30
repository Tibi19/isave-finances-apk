package com.tam.isave.model.CategoryTools;

public class Interval extends Category {

    private static final String DEFAULT_NAME = "interval";

    private int days; // Number of days in this interval.
    private int id; // Should represent object's index in parent's array of Intervals.

    public Interval(int id, int days, double goal) {
        super(DEFAULT_NAME + id, goal);
        this.days = days;
        this.id = id;
    }

    public void modify(double spent, double goal, GoalAdapter adapter) {
        super.modify(DEFAULT_NAME + id, spent, goal, true, adapter);
    }

    public int getDays() {
        return days;
    }
}
