package com.tam.isave.model.goalorganizer;

import com.tam.isave.utils.Date;

public class IntervalUtils {

    public static int getIntervalFirstDayValue(Interval interval, GoalOrganizer organizer) {
        if(interval == null || organizer == null) { return -1; }

        Date intervalFirstDay = organizer.getFirstDay();
        for(Interval intervalItem : organizer.getIntervals()) {
            if(interval.getId() == intervalItem.getId()) { return intervalFirstDay.getValue(); }
            intervalFirstDay = intervalFirstDay.addDays(intervalItem.getDays());
        }

        return -1;
    }

    public static int getIntervalLastDayValue(Interval interval, GoalOrganizer organizer) {
        if(interval == null || organizer == null) { return -1; }

        Interval[] intervals = organizer.getIntervals();
        int firstIntervalDays = intervals[0].getDays();
        Date intervalLastDay = organizer.getFirstDay().countDays(firstIntervalDays);

        for(int i = 0; i < intervals.length; i++) {
            Interval intervalItem = intervals[i];
            if(interval.getId() == intervalItem.getId()) { return intervalLastDay.getValue(); }
            if(i == intervals.length - 1) { return -1; }
            intervalLastDay = intervalLastDay.addDays(intervals[i + 1].getDays());
        }

        return -1;
    }

    public static Interval getPreviousInterval(Interval interval, GoalOrganizer organizer) {
        if(interval == null || organizer == null) { return null; }

        Interval[] intervals = organizer.getIntervals();
        for(int i = 1; i < intervals.length; i++) {
            if(interval.getId() == intervals[i].getId()) {
                return intervals[i - 1];
            }
        }

        return null;
    }

    public static Interval getNextInterval(Interval interval, GoalOrganizer organizer) {
        if(interval == null || organizer == null) { return null; }

        Interval[] intervals = organizer.getIntervals();
        for(int i = 0; i < intervals.length - 1; i++) {
            if(interval.getId() == intervals[i].getId()) {
                return intervals[i + 1];
            }
        }

        return null;
    }

}
