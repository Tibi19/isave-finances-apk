package com.tam.isave.model.goalorganizer;

import android.os.Debug;
import android.util.Log;

import com.tam.isave.utils.Date;
import com.tam.isave.utils.DebugUtils;
import com.tam.isave.utils.NumberUtils;

/**
 * Provides progress information in a GoalOrganizer's intervals group.
 */
public class IntervalsProgress {

    private GoalOrganizer goalOrganizer;
    private Interval activeInterval;

    public IntervalsProgress(GoalOrganizer goalOrganizer) {
        this.goalOrganizer = goalOrganizer;
        activeInterval = goalOrganizer.getActiveInterval();
    }

    /**
     * Creates a displayable string.
     * In format "Day X in Y - Z" where,
     * X: day number since first day of goal organizer (this.daysProgress).
     * Y - Z: day numbers of first and last days of the active interval.
     * If today's date is not between goal organizer's first and last days,
     * Returns appropriate message.
     * @return info about the progress.
     */
    public String getTimeProgress() {
        Date firstDay = goalOrganizer.getFirstDay();
        IntervalsAnalyzer intervalsAnalyzer = goalOrganizer.getIntervalsAnalyzer();

        // toString with "Day x in y - z: intervalSpent / intervalGoal"
        int startDaysDifference = firstDay.differenceInDays(Date.today());
        final String BEFORE_START_DATE = startDaysDifference + " days until next goal";
        final String AFTER_END_DATE = "Goal time frame has been passed";

        // If Today is older than first day, return status until next goal.
        if(Date.today().isOlderThan(firstDay)) { return BEFORE_START_DATE; }

        // If today is newer than last day, return warning that goal has been passed.
        // At this point, we know today is newer than firstDay,
        // If days since start date are >= goal organizer's time frame, today is newer than last day.
        boolean isTodayNewerThanLastDay = startDaysDifference >= goalOrganizer.getGlobalIntervalDays();
        if(isTodayNewerThanLastDay) { return AFTER_END_DATE; }

        int daysAtIntStart = 1; // How many days have been progressed at interval start day.
        // If we are at first interval, @daysAtIntStart remains 1.
        if(intervalsAnalyzer.getIntervalIndex(activeInterval) != 0) {
            // If we are not at first interval, calculate how many days have been progressed
            Interval[] intervals = goalOrganizer.getIntervals();
            Interval prevInterval = intervals[intervalsAnalyzer.getIntervalIndex(activeInterval) - 1]; // The previous interval.
            daysAtIntStart += intervalsAnalyzer.getIntervalsDays(prevInterval); // @daysAtIntStart is 1 + the sum of days between preceding intervals.
        }
        int daysAtIntEnd = daysAtIntStart + (activeInterval.getDays() - 1); // How many days have been progressed at interval end day.

        return "Day " + goalOrganizer.getDaysProgress() + " in " + daysAtIntStart + " - " + daysAtIntEnd;
    }

    public String getDaysProgress() {
        int daysProgress = getDaysProgressInActiveInterval();

        if(daysProgress < 0) { return "N/A"; }

        return daysProgress + " of " + activeInterval.getDays();
    }

    private int getDaysProgressInActiveInterval() {
        int daysProgress = goalOrganizer.getDaysProgress();
        Interval[] intervals = goalOrganizer.getIntervals();

        if(intervals == null || intervals.length <= 0 || daysProgress <= 0) { return -1; }

        for(Interval interval : intervals) {
            if(interval.getId() == activeInterval.getId()) { return daysProgress; }
            daysProgress -= activeInterval.getDays();
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
        Interval[] intervals = goalOrganizer.getIntervals();

        if(intervals == null || intervals.length <= 0) { return -1; }

        for(Interval interval : goalOrganizer.getIntervals()) {
            if(interval.getId() == activeInterval.getId()) { return activeIntervalCount; }
            activeIntervalCount++;
        }

        return -1;
    }

    // Spending progress of current interval.
    public String getBudgetProgress() {
        if(activeInterval == null) { return "N/A"; }
        return activeInterval.getProgress();
    }
}
