package com.tam.isave.model.goalorganizer;

/**
 * Provides progress information in a GoalOrganizer's intervals group.
 */
public class IntervalsProgress {

    private GoalOrganizer goalOrganizer;

    public IntervalsProgress(GoalOrganizer goalOrganizer) {
        this.goalOrganizer = goalOrganizer;
    }

    public String getDaysProgress() {
        Interval activeInterval = goalOrganizer.getActiveInterval();
        int daysProgress = getDaysProgressInActiveInterval(activeInterval);

        if(daysProgress < 0) { return "N/A"; }

        return daysProgress + " of " + activeInterval.getDays();
    }

    private int getDaysProgressInActiveInterval(Interval activeInterval) {
        int daysProgress = goalOrganizer.getDaysProgress();
        Interval[] intervals = goalOrganizer.getIntervals();

        if(activeInterval == null || intervals == null || intervals.length <= 0 || daysProgress <= 0) { return -1; }

        for(Interval interval : intervals) {
            if(interval.getId() == activeInterval.getId()) { return daysProgress; }
            daysProgress -= interval.getDays();
        }

        return -1;
    }

    public String getIntervalsProgress() {
        int activeIntervalCount = getActiveIntervalCount();

        if(activeIntervalCount < 0) { return "N/A"; }

        return activeIntervalCount + " of " + goalOrganizer.getIntervalsNr();
    }

    private int getActiveIntervalCount() {
        int activeIntervalCount = 1;
        Interval activeInterval = goalOrganizer.getActiveInterval();
        Interval[] intervals = goalOrganizer.getIntervals();

        if(activeInterval == null || intervals == null || intervals.length <= 0) { return -1; }

        for(Interval interval : intervals) {
            if(interval.getId() == activeInterval.getId()) { return activeIntervalCount; }
            activeIntervalCount++;
        }

        return -1;
    }

    // Spending progress of current interval.
    public String getBudgetProgress() {
        Interval activeInterval = goalOrganizer.getActiveInterval();
        if(activeInterval == null) { return "N/A"; }
        return activeInterval.getProgress();
    }
}
