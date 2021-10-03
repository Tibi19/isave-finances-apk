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

    /**
     * Append to an id a digit suffix.
     * In cases where the id has to be created through iterations, the id might be the same. Use this method to make the id unique.
     * The id appending should be limited to 10 objects.
     * @param id The original id.
     * @param idSuffix The id suffix. Has to be < 10.
     * @return The new id
     */
    public static int appendId(int id, int idSuffix) {
        // The new id has to have 9 digits, we can't just append one digit.
        // By appending one digit, the new id might conflict with existing ids that are 7 or 8 digits.
        while (id <= 99999999) {
            id = id * 10;
        }
        return id + idSuffix;
    }

}
