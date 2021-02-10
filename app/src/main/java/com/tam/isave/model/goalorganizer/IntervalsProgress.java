package com.tam.isave.model.goalorganizer;

import com.tam.isave.utils.Date;

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

    // Returns the string to be displayed
    // In format "Day X in Y - Z";
    // X: day number since first day of goal organizer (this.daysProgress).
    // Y - Z: day numbers of first and last days of the active interval.
    //
    // If today's date is not between goal organizer's first and last days,
    // Returns appropriate message.

    /**
     * Creates a displayable string.
     * In format "Day X in Y - Z" where,
     * X: day number since first day of goal organizer (this.daysProgress).
     * Y - Z: day numbers of first and last days of the active interval.
     * If today's date is not between goal organizer's first and last days,
     * Returns appropriate message.
     * @return info about the progress.
     */
    public String getIntervalsProgress() {
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

    // Spending progress of current interval.
    public String getBudgetProgress() {
        return activeInterval.getProgress();
    }
}
