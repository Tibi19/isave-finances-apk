package com.tam.isave.model.CategoryTools;

public class Interval extends Category {

    private static final String DEFAULT_NAME = "Interval";

    private int days; // Number of days in this interval.

    public Interval(int id, int days, double goal) {
        super(DEFAULT_NAME + (id + 1), goal);
        this.days = days;
    }

    public void modify(double spent, double goal, GoalAdapter adapter) {
        super.modify(getName(), spent, goal, true, adapter);
    }

    public int getDays() {
        return days;
    }
}
