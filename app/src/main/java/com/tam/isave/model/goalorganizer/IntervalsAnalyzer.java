package com.tam.isave.model.goalorganizer;

import com.tam.isave.model.transaction.Payment;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Date;

/**
 * Provides methods that analyzes a group of intervals.
 * These methods provide information about the intervals.
 * To be used with a GoalOrganizer
 */
public class IntervalsAnalyzer {

    private GoalOrganizer goalOrganizer;
    private Interval[] intervals;

    public IntervalsAnalyzer(GoalOrganizer goalOrganizer) {
        this.goalOrganizer = goalOrganizer;
        this.intervals = goalOrganizer.getIntervals();
    }

    // Return index of @interval in intervals.
    public int getIntervalIndex(Interval interval) {
        if(intervals == null) { return -1; }

        for(int i = 0; i < intervals.length; i++) {
            if(interval.getId() == intervals[i].getId()) {
                return i;
            }
        }
        return -1;
    }

    // The total amount of days between all intervals until target interval inclusively.
    // Returns int1.days + int2.days + ... + target.days.
    // EX: Let there be 4 intervals of 5 days each.
    // EX: If we're halfway through the 3rd interval, method will return 5 + 5 + 5 (15);
    public int getIntervalsDays(Interval targetInterval) {
        if ( (targetInterval == null) || (intervals == null) ) { return 0; }

        int intervalsDays = 0;
        for(int i = 0; i <= getIntervalIndex(targetInterval); i++) {
            intervalsDays += intervals[i].getDays();
        }
        return intervalsDays;
    }

    // Returns the interval where @payment has been made by its date.
    public Interval getPaymentIntervalByDate(Transaction payment) {
        Date firstDay = goalOrganizer.getFirstDay();

        int tranValue = payment.getDate().getValue();
        // If payment's date is older than first day of the global interval,
        // Or more recent than its last day,
        // payment is not valid and shouldn't be tracked, return null.
        if(tranValue < firstDay.getValue()) { return null; }
        if(tranValue > firstDay.addDays(goalOrganizer.getGlobalIntervalDays() - 1).getValue()) { return null; }

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

            if(intervalIndex >= (goalOrganizer.getIntervalsNr() - 1)) { return null; } // Return null if max index has been reached.

            // Update iteration.
            intervalIndex++;
            earlyBoundDate = lateBoundDate.addDays(1);
            lateBoundDate = lateBoundDate.addDays(intervals[intervalIndex].getDays());
        }
    }

    // Returns interval where payment happened by checking each interval's history
    public Interval getPaymentIntervalByHistory(Transaction payment) {
        if(!goalOrganizer.getHistory().hasTransaction(payment)) { return null; }

        for(Interval interval : intervals) {
            if(interval.getHistory().hasTransaction(payment)) {
                return interval;
            }
        }

        return null;
    }
}
