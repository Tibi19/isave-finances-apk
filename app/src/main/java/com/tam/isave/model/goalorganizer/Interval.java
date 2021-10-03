package com.tam.isave.model.goalorganizer;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.GoalAdapter;
import com.tam.isave.utils.Constants;

@Entity(tableName = Constants.TABLE_NAME_INTERVAL)
public class Interval extends Category {

    @ColumnInfo(name = Constants.INTERVAL_COLUMN_DAYS)
    private int days; // Number of days in this interval.

    @Ignore
    public Interval(int id, int days, double goal) {
        super(Constants.NAMING_INTERVAL + (id + 1), goal);
        this.days = days;
    }

    public Interval() {
        super();
        setFlexibleGoal(true);
    }

    public void modify(double spent, double goal, GoalAdapter adapter) {
        super.modify(getName(), spent, goal, true, adapter);
    }

    public void setDays(int days) { this.days = days; }
    public int getDays() {
        return days;
    }
}
