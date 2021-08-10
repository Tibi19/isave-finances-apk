package com.tam.isave.utils;

public class IdGenerator {

    private static final int MAX_DAY_SECONDS_DIGITS = 100000;

    /**
     * A time based unique id in the format *DDDSSSSS*, where
     * DDD - the number of days passed so far in the year.
     * SSSSS - the number of seconds passed so far in the day.
     * Assumes objects receiving an id will not persist for longer than a year.
     * @return The unique id.
     */
    public static int makeId() {
        int dayNumber = Date.getDaysUntilDate(Date.today());
        int secondsInDay = Date.getSecondsInDay();

        return dayNumber * MAX_DAY_SECONDS_DIGITS + secondsInDay;
    }

}
