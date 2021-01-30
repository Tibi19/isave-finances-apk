package com.tam.isave.utils;

public class Utils {

    // To be used for double comparisons to 0.
    public static final double ZERO_DOUBLE = 0.0001;

    public static final int TRANSACTION_DAYS_LIMIT = 60; // User should not add transactions older than 2 months.

    public static double twoDecimals(double value) {
        return value - ( Math.signum(value) * (value % 0.01) );
    }

    // Whether parameters are the same with a precision of 2 decimals.
    public static boolean isSameDoubles(double x, double y) {
        return twoDecimals(x) == twoDecimals(y);
    }

    // Checks if @value can be practically considered 0.
    // true if -0.0001 <= value <= 0.0001.
    public static boolean isZeroDouble(double value) {
        return (value <= ZERO_DOUBLE) && (value >= -ZERO_DOUBLE);
    }

    // Returns a String describing the order of the specified @index.
    // ex: @index 0 - returns "1st"
    // ex: @index 5 - returns "6th"
    // ex: @index 22 - returns "23rd"
    public static String getOrderString(int index) {
        if(index < 0) { return null; }

        // Increment index to describe order numerically.
        // E.g. Element 0 is order 1.
        String orderString = "" + (++index);

        // Last digit of index which will be checked to add to the ending of orderString.
        // Will be -1 in case the last digit of index is preceded by a 1.
        // In this case, orderString will end in "th".
        int indexLastDigit = ( ( (index % 100) / 10 ) != 1 ) ? index % 10 : -1;
        switch(indexLastDigit) {
            case 1:
                orderString += "st";
                break;
            case 2:
                orderString += "nd";
                break;
            case 3:
                orderString += "rd";
                break;
            default:
                orderString += "th";
                break;
        }

        return orderString;
    }
}
