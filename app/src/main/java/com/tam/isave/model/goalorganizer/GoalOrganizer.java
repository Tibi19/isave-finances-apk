package com.tam.isave.model.goalorganizer;

import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.CategoryTracker;
import com.tam.isave.model.transaction.History;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * GoalOrganizer breaks up the user's goal in smaller goals.
 * Functionality based on "how to eat an elephant: one bite at a time".
 *
 * Takes the user's goal and required time frame to reach the goal and breaks them up in intervals,
 * Categories that save payments based on their dates and are active for a number of days.
 *
 * Has a category tracker that tracks and handles overflow for intervals.
 * Updates active interval to be displayed based on today's date.
 */
public class GoalOrganizer{

    private double globalGoal;
    private int globalIntervalDays;
    private Date firstDay;
    private int intervalsNr;

    private int daysProgress;
    private History history;
    private IntervalsAnalyzer intervalsAnalyzer;
    private IntervalsProgress intervalsProgress;
    private CategoryTracker tracker;
    private Interval activeInterval;
    private Interval[] intervals;

    public GoalOrganizer() {
        this(0.0, 0, null, null);
    }

    public GoalOrganizer(double globalGoal, int intervalsNr, int firstDayValue, int globalIntervalDays) {
        Date firstDay = new Date(firstDayValue);
        Date lastDay = firstDay.addDays(globalIntervalDays - 1);
        setup(globalGoal, intervalsNr, firstDay, lastDay);
    }

    public GoalOrganizer(double globalGoal, int intervalsNr, Date firstDay, Date lastDay) {
        setup(globalGoal, intervalsNr, firstDay, lastDay);
    }

    public void setup(double globalGoal, int intervalsNr, Date firstDay, Date lastDay) {
        this.globalGoal = globalGoal;
        this.intervalsNr = intervalsNr;
        if( (firstDay != null) && (lastDay != null) ) {
            this.globalIntervalDays = firstDay.countDaysUntil(lastDay);
        }

        this.firstDay = firstDay;
        this.history = new History();

        setupIntervals();
        setupIntervalsTracker();
        setupIntervalsHelpers();
        update();
    }

    private void setupIntervalsHelpers() {
        this.intervalsAnalyzer = new IntervalsAnalyzer(this);
        this.intervalsProgress = new IntervalsProgress(this);
    }

    public void setupHistory(History history) {
        setupHistory(history, false);
    }

    private void setupHistory(History history, boolean overrideHistory) {
        if(this.history != null && !this.history.isEmpty() && !overrideHistory) { return; }
        this.history = history;
        tracker.setupHistory(history, false);
        assignHistory();
    }

    // Resets progress and first day becomes today.
    public void reset() {
        Date today = Date.today();
        setup(this.globalGoal, this.intervalsNr, today, today.countDays(globalIntervalDays));
    }

    public void modify(double globalGoal, int intervalsNr, Date firstDay, Date lastDay, History history) {
        boolean modifiedGoal = modifyGlobalGoal(globalGoal);
        boolean modifiedIntervalsNr = modifyIntervalsNr(intervalsNr);
        boolean modifiedTime = modifyTime(firstDay, lastDay);

        if( !(modifiedGoal || modifiedIntervalsNr || modifiedTime) ) { return; }

        setupIntervals();
        setupIntervalsTracker();
        setupIntervalsHelpers();
        update();
        setupHistory(history, true);
    }

    private void setupIntervals() {
        if(globalIntervalDays <= 0 || intervalsNr <= 0) { return; }

        intervals = new Interval[intervalsNr];
        int leftoverDays = globalIntervalDays % intervalsNr;
        double goal = this.globalGoal / this.intervalsNr;
        for(int i = 0; i < intervalsNr; i++) {
            int intervalDays = globalIntervalDays / intervalsNr;
            if(leftoverDays-- > 0) { intervalDays++; }

            intervals[i] = new Interval(i, intervalDays, goal);
        }

        activeInterval = intervals[0];
    }

    private void setupIntervalsTracker() {
        if(intervalsNr <= 0 || intervals == null) { return; }

        Category[] categoryArray = intervals;
        ArrayList<Category> categoryList = new ArrayList<Category>(Arrays.asList(categoryArray));
        tracker = new CategoryTracker(categoryList, null, true);
    }

    public void update() {
        if ( (firstDay == null) || (activeInterval == null) || (intervals == null) ) { return; }

        daysProgress = firstDay.countDaysUntil(Date.today());
        
        if(firstDay.isExclusivelyNewerThan(Date.today()) || daysProgress > globalIntervalDays) {
            activeInterval = null;
            return;
        }

        while(daysProgress > intervalsAnalyzer.getIntervalsDays(activeInterval)) {
            int activeIntervalIndex = intervalsAnalyzer.getIntervalIndex(activeInterval);
            if(activeIntervalIndex < 0) { break; }
            activeInterval = intervals[activeIntervalIndex + 1];
        }
    }

    public void modify(double globalGoal) {
        modify(globalGoal, this.intervalsNr, this.firstDay, this.firstDay.addDays(this.globalIntervalDays - 1), this.history);
    }

    private void assignHistory() {
        if(history == null || history.isEmpty()) {  return; }

        for(Transaction transaction : history.getHistoryList()) {
            makePayment(transaction);
        }
    }

    private boolean modifyGlobalGoal(double globalGoal) {
        if(globalGoal <= NumberUtils.ZERO_DOUBLE) { return false; }

        if(!NumberUtils.isSameDoubles(this.globalGoal, globalGoal)) {
            this.globalGoal = globalGoal;
            return true;
        }

        return false;
    }

    private boolean modifyIntervalsNr(int intervalsNr) {
        if(intervalsNr <= 0) { return false; }

        if(this.intervalsNr != intervalsNr) {
            this.intervalsNr = intervalsNr;
            return true;
        }

        return false;
    }

    private boolean modifyTime(Date firstDay, Date lastDay) {
        if(firstDay == null || lastDay == null) { return false; }
        boolean modified = false;

        if(this.firstDay == null || this.firstDay.getValue() != firstDay.getValue()) {
            this.firstDay = firstDay;
            modified = true;
        }

        int globalIntervalDays = firstDay.countDaysUntil(lastDay);
        if(this.globalIntervalDays != globalIntervalDays) {
            this.globalIntervalDays = globalIntervalDays;
            modified = true;
        }

        return modified;
    }

    public void makePayment(Transaction payment) {
        if(payment == null || tracker == null) { return; }

        Interval interval = intervalsAnalyzer.getPaymentIntervalByDate(payment);
        if(interval == null) { return; }
        tracker.makePayment(interval, payment);
    }

    public void removePayment(Transaction payment) {
        Interval interval = intervalsAnalyzer.getPaymentIntervalByHistory(payment);
        if(interval == null) { return; }

        tracker.removePayment(interval, payment);
    }

    public void modifyPayment(Transaction payment, double valueDiff) {
        Interval originalInterval = intervalsAnalyzer.getPaymentIntervalByHistory(payment);
        Interval newInterval = intervalsAnalyzer.getPaymentIntervalByDate(payment);

        if(originalInterval == null && newInterval == null) { return; }

        if(originalInterval == null) {
            makePayment(payment);
            return;
        }

        if(newInterval == null) {
            removePayment(payment);
            return;
        }

        if(originalInterval.getId() != newInterval.getId()) {
            tracker.movePayment(originalInterval, newInterval, payment);
            return;
        }

        tracker.modifyPaymentInInterval(originalInterval, payment, valueDiff);
    }

    public String getDaysProgressString() {
        return intervalsProgress.getDaysProgress();
    }

    public String getIntervalsProgressString() {
        return intervalsProgress.getIntervalsProgress();
    }

    public String getBudgetProgress() {
        return intervalsProgress.getBudgetProgress();
    }

    public double getGlobalGoal() {
        return globalGoal;
    }

    public History getHistory() {
        return history;
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

    public Date getLastDay() { return firstDay.countDays(globalIntervalDays); }

    public boolean isActive() { return activeInterval != null; }

    public int getIntervalsNr() {
        return intervalsNr;
    }

    public Interval getActiveInterval() {
        return activeInterval;
    }

    public Interval[] getIntervals() {
        return intervals;
    }
}
