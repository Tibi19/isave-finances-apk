package com.tam.isave.model;

import com.tam.isave.model.CategoryTools.CategoryTracker;
import com.tam.isave.model.CategoryTools.Interval;
import com.tam.isave.model.TransactionTools.History;
import com.tam.isave.model.TransactionTools.Payment;
import com.tam.isave.model.TransactionTools.Transaction;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.NumberUtils;

import java.util.ArrayList;

// GoalOrganizer breaks up the user's goal in smaller goals.
// Functionality based on "how to eat an elephant: one bite at a time".
//
// Takes the user's goal and required time frame to reach the goal and breaks them up in intervals,
// Categories that save payments based on their dates and are active for a number of days.
//
// Has a category tracker that tracks and handles overflow for intervals.
// Updates active interval to be displayed based on today's date.
public class GoalOrganizer implements IProgressDisplayable {

    private final static int DEFAULT_GLOBAL_INTERVAL_DAYS = 30; // 1 month as default.

    private double globalGoal; // How much should be spent in @globalIntervalDays.
    private int globalIntervalDays; // How many days for the whole interval.
    private int daysProgress; // Day number since beginning the global Interval (starts from 1).
    private Date firstDay; // First day of the global interval.

    private History history; // Tracks all payments made in the global interval.

    private CategoryTracker tracker; // Tracks intervals.
    private int intervalsNr; // How many intervals are desired.
    private Interval activeInterval; // Index of the interval to be shown.
    private Interval[] intervals; // Intervals to be tracked.

    // Blank Constructor.
    public GoalOrganizer() {
        this(0.0, 0, 0);
        this.daysProgress = 0;
    }

    // Constructor with a start date and end date.
    public GoalOrganizer(double globalGoal, int intervalsNr, Date firstDay, Date lastDay) {
        setup(globalGoal, intervalsNr, firstDay, lastDay);
    }

    // Constructor with default period of 30 days starting from today.
    public GoalOrganizer(double globalGoal, int intervalsNr) {
        this(globalGoal, intervalsNr, DEFAULT_GLOBAL_INTERVAL_DAYS);
    }

    // Constructor with a set number of days starting from today.
    public GoalOrganizer(double globalGoal, int intervalsNr, int globalIntervalDays) {
        this(globalGoal, intervalsNr, Date.today(), Date.today().addDays(globalIntervalDays));
    }

    // Sets up the instance.
    // Works as a constructor.
    // Can be used to modify and reset the tracker.
    // Any progress of the current tracker will be lost.
    public void setup(double globalGoal, int intervalsNr, Date firstDay, Date lastDay) {
        this.globalGoal = globalGoal;
        this.intervalsNr = intervalsNr;
        this.globalIntervalDays = firstDay.differenceInDays(lastDay);

        this.firstDay = firstDay;
        this.activeInterval = null;
        this.history = new History();

        setupIntervals();
        update();
    }

    // Resets the goal organizer by starting all over again.
    // Keeps the same goal, interval structure and time frame.
    // First day becomes today.
    public void reset() {
        Date today = Date.today();
        setup(this.globalGoal, this.intervalsNr, today, today.addDays(globalIntervalDays - 1));
    }

    // Modifies goal, number of intervals or time state.
    // Resets intervals and reassigns history in case of modification
    // To account for interval or overflow changes.
    public void modify(double globalGoal, int intervalsNr, Date firstDay, Date lastDay) {
        boolean modGoal = modifyGlobalGoal(globalGoal);
        boolean modIntervalsNr = modifyIntervalsNr(intervalsNr);
        boolean modTime = modifyTime(firstDay, lastDay);

        // No modifications, stop method.
        if( !(modGoal || modIntervalsNr || modTime) ) { return; }

        // Modifications happened, reset interval state and reassign history.
        setupIntervals();
        update();
        reassignHistory();
    }

    /**
     * Modifies goal of goal organizer
     * @param globalGoal - The new goal.
     */
    public void modify(double globalGoal) {
        modify(globalGoal, this.intervalsNr, this.firstDay, this.firstDay.addDays(this.globalIntervalDays - 1));
    }

    // Reassign history by making each transaction again.
    // Will recalculate state and handle overflow in case of modification.
    private void reassignHistory() {
        if(history.isEmpty()) { return; }

        ArrayList<Transaction> historyList = history.cloneHistoryList();
        history.reset();
        // Inverse iteration through historyList as history assignation will be easier.
        // Newer transactions should be added last.
        for(int i = historyList.size() - 1; i >= 0; i--) {
            makePayment((Payment) historyList.get(i));
        }
    }

    // Modifies the global goal
    // If parameter is different than the current goal, than 0 and is positive.
    // Returns true if modification happened.
    private boolean modifyGlobalGoal(double globalGoal) {
        globalGoal = NumberUtils.twoDecimals(globalGoal);
        if(globalGoal <= NumberUtils.ZERO_DOUBLE) { return false; }

        if(NumberUtils.twoDecimals(this.globalGoal) != globalGoal) {
            this.globalGoal = globalGoal;
            return true;
        }

        return false;
    }

    // Modifies number of intervals
    // If parameter is different than the current number of intervals, than 0 and is positive.
    // Returns true if modification happened.
    private boolean modifyIntervalsNr(int intervalsNr) {
        if(intervalsNr <= 0) { return false; }

        if(this.intervalsNr != intervalsNr) {
            this.intervalsNr = intervalsNr;
            return true;
        }

        return false;
    }

    // Modifies first day and days number for the whole interval.
    //
    // If @firstDay is different than this.firstDay => modifies first day of interval.
    // If days difference between @firstDay and @lastDay is different than this.globalIntervalDays => modifies days number for whole interval
    //
    // Returns true if modification happened.
    private boolean modifyTime(Date firstDay, Date lastDay) {
        if(firstDay == null || lastDay == null) { return false; }
        boolean modified = false;

        if(this.firstDay.getValue() != firstDay.getValue()) {
            this.firstDay = firstDay;
            modified = true;
        }

        int globalIntervalDays = firstDay.differenceInDays(lastDay);
        if(this.globalIntervalDays != globalIntervalDays) {
            this.globalIntervalDays = globalIntervalDays;
            modified = true;
        }

        return modified;
    }

    // Set up days for each interval.
    // Initializes spending tracker for intervals.
    // Initializes goals for each interval.
    // Initializes category tracker of intervals.
    private void setupIntervals() {
        if(globalIntervalDays <= 0 || intervalsNr <= 0) { return; }

        intervals = new Interval[intervalsNr];

        // How many days are left after dividing the total number of days to the number of intervals.
        int leftoverDays = globalIntervalDays % intervalsNr;
        // General goal for each interval.
        // The total that wants to be spent divided by the number of intervals.
        double goal = this.globalGoal / this.intervalsNr;

        // Set up each interval.
        for(int i = 0; i < intervalsNr; i++) {
            // Global days are divided equally for each interval.
            // Any leftover days are added 1 by 1 to each interval until none are left.
            int intervalDays = globalIntervalDays / intervalsNr;
            if(leftoverDays-- > 0) { intervalDays++; }

            // Assign interval to its place in the array.
            intervals[i] = new Interval(i, intervalDays, goal);
        }

        // Set up category tracker of intervals;
        // Intervals are ordered chronologically.
        // Only intervals that follow an interval solicitor should help with positive overflow.
        // Therefore, OrderedHandling is set to true.
        tracker = new CategoryTracker(intervals, history, true);
    }

    // Updates:
    // The current day number since the first day of the interval.
    // The current interval tracked.
    public void update() {
        daysProgress = firstDay.differenceInDays(Date.today()) + 1;
        // Increment current interval until the current day is less than the total amount of days between all intervals tracked so far.
        while(daysProgress > getIntervalsDays(activeInterval)) {
            int activeIntervalIndex = getIntervalIndex(activeInterval);
            if(activeIntervalIndex < 0) { break; } // Break if index indicates an error (should be -1).
            activeInterval = intervals[activeIntervalIndex + 1];
        }
    }

    // The total amount of days between all intervals until target interval inclusively.
    // Returns int1.days + int2.days + ... + target.days.
    // EX: Let there be 4 intervals of 5 days each.
    // EX: If we're halfway through the 3rd interval, method will return 5 + 5 + 5 (15);
    private int getIntervalsDays(Interval targetInterval) {
        int intervalsDays = 0;
        for(int i = 0; i <= getIntervalIndex(targetInterval); i++) {
            intervalsDays += intervals[i].getDays();
        }
        return intervalsDays;
    }

    // Makes payment in category tracker for the right interval.
    // Interval is assigned by payment's date.
    // Adds payment to history.
    public void makePayment(Payment payment) {
        // Protect from duplicate payments.
        if(history.hasTransaction(payment)) { return; }

        Interval interval = getPaymentIntervalByDate(payment);
        if(interval == null) { return; }

        tracker.makePayment(interval, payment);
        history.addTransaction(payment);
    }

    // Returns the interval where @payment has been made by its date.
    private Interval getPaymentIntervalByDate(Payment payment) {
        int tranValue = payment.getDate().getValue();
        // If payment's date is older than first day of the global interval,
        // Or more recent than its last day,
        // payment is not valid and shouldn't be tracked, return null.
        if(tranValue < firstDay.getValue()) { return null; }
        if(tranValue > firstDay.addDays(globalIntervalDays - 1).getValue()) { return null; }

        int intervalIndex = 0; // Index to iterate through intervals
        // Iterate through pairs of dates representing the first and last day of each interval.
        Date earlyBoundDate = firstDay;
        Date lateBoundDate = firstDay.addDays(intervals[0].getDays() - 1);
        while(true) {
            Interval interval = intervals[intervalIndex];
            // If payment's date is in between an interval's date bounds,
            // Interval where payment has been made was found.
            if( (earlyBoundDate.getValue() <= tranValue) && (lateBoundDate.getValue() >= tranValue) ) {
                return interval;
            }

            if(intervalIndex >= (intervalsNr - 1)) { return null; } // Return null if max index has been reached.

            // Update iteration.
            intervalIndex++;
            earlyBoundDate = lateBoundDate.addDays(1);
            lateBoundDate = lateBoundDate.addDays(intervals[intervalIndex].getDays());
        }
    }

    // Returns interval where payment happened by checking each interval's history
    private Interval getPaymentIntervalByHistory(Payment payment) {
        if(!history.hasTransaction(payment)) { return null; }

        for(Interval interval : intervals) {
            if(interval.getHistory().hasTransaction(payment)) {
                return interval;
            }
        }

        return null;
    }

    // Removes payment from the interval where it was made.
    // Removes payment from history.
    public void removePayment(Payment payment) {
        Interval interval = getPaymentIntervalByHistory(payment);
        if(interval == null) { return; }

        tracker.removePayment(interval, payment);
    }

    // Handles modification of a payment if it's part of this object's history.
    // If date is different after modification, the payment might need to change interval.
    // Calls tracker's modify payment method to handle change in values.
    public void modifyPayment(Payment payment, double valueDiff) {
        if(!history.hasTransaction(payment)) { return; }

        // Compare the interval of the payment by its old date (interval wasn't updated yet)
        // And the interval of the payment by its new date.
        // If intervals are different, remove payment from the old interval and add to new interval.
        Interval interval = getPaymentIntervalByHistory(payment);
        Interval newInterval = getPaymentIntervalByDate(payment);
        if(interval == null || newInterval == null) { return; }
        if(!interval.equals(newInterval)) {
            tracker.movePayment(interval, newInterval, payment);
            return;
        }

        // If interval wasn't changed for the modified payment, let tracker handle value difference.
        tracker.modifyPaymentInInterval(interval, payment, valueDiff);
    }

    // Return index of @interval in intervals.
    private int getIntervalIndex(Interval interval) {
        for(int i = 0; i < intervals.length; i++) {
            if(interval.equals(intervals[i])) {
                return i;
            }
        }
        return -1;
    }

    // Processes payments in History starting from firstDay;
    public void makePaymentsFromHistory(History history) {
        ArrayList<Transaction> transactions = history.getTransactionsFromDate(firstDay);
        if(transactions.isEmpty()) { return; }

        for(Transaction transaction : transactions) {
            if( !( transaction instanceof Payment ) ) { continue; }
            makePayment((Payment) transaction);
        }
    }

    // Returns the string to be displayed
    // In format "Day X in Y - Z";
    // X: day number since first day of goal organizer (this.daysProgress).
    // Y - Z: day numbers of first and last days of the active interval.
    //
    // If today's date is not between goal organizer's first and last days,
    // Returns appropriate message.
    @Override
    public String getInfoAboutProgress() {
        // toString with "Day x in y - z: intervalSpent / intervalGoal"
        int startDaysDifference = firstDay.differenceInDays(Date.today());
        final String BEFORE_START_DATE = startDaysDifference + " days until next goal";
        final String AFTER_END_DATE = "Goal time frame has been passed";

        // If Today is older than first day, return status until next goal.
        if(Date.today().isOlderThan(firstDay)) { return BEFORE_START_DATE; }

        // If today is newer than last day, return warning that goal has been passed.
        // At this point, we know today is newer than firstDay,
        // If days since start date are >= goal organizer's time frame, today is newer than last day.
        boolean isTodayNewerThanLastDay = startDaysDifference >= globalIntervalDays;
        if(isTodayNewerThanLastDay) { return AFTER_END_DATE; }

        int daysAtIntStart = 1; // How many days have been progressed at interval start day.
        // If we are at first interval, @daysAtIntStart remains 1.
        if(getIntervalIndex(activeInterval) != 0) {
            // If we are not at first interval, calculate how many days have been progressed
            Interval prevInterval = intervals[getIntervalIndex(activeInterval) - 1]; // The previous interval.
            daysAtIntStart += getIntervalsDays(prevInterval); // @daysAtIntStart is 1 + the sum of days between preceding intervals.
        }
        int daysAtIntEnd = daysAtIntStart + (activeInterval.getDays() - 1); // How many days have been progressed at interval end day.

        return "Day " + daysProgress + " in " + daysAtIntStart + " - " + daysAtIntEnd;
    }

    // Spending progress of active interval
    // In format "spent / goal" and goal modification info "(-goalModification)".
    @Override
    public String getProgress() {
        return activeInterval.getSpent() + " / " + activeInterval.getEndGoalString();
    }

    public double getGlobalGoal() {
        return globalGoal;
    }

    public int getGlobalIntervalDays() {
        return globalIntervalDays;
    }

    public int getDaysProgress() {
        return daysProgress;
    }

    public Date getFirstDay() {
        return firstDay;
    }

    public int getIntervalsNr() {
        return intervalsNr;
    }

    public Interval getActiveInterval() {
        return activeInterval;
    }

    public Interval[] getIntervals() {
        return intervals;
    }

    public void dispose() {
        if(firstDay != null) {
            firstDay.dispose();
            firstDay = null;
        }

        if(history != null) {
            history.dispose();
            history = null;
        }

        if(tracker != null) {
            tracker.dispose();
            tracker = null;
        }

        activeInterval = null;
        intervals = null;
    }
}
