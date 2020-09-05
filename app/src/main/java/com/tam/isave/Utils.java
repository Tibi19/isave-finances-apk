package com.tam.isave;

public class Utils {

    // To be used for double comparisons to 0.
    public static final double ZERO_DOUBLE = 0.0001;

    public static final int TRANSACTION_DAYS_LIMIT = 60; // User should not add transactions older than 2 months.

    public static double twoDecimals(double value) {
        return value - ( Math.signum(value) * (value % 0.01) );
    }

    // Whether parameters are the same with a precision of 2 decimals.
    public static boolean sameDoubles(double x, double y) {
        return twoDecimals(x) == twoDecimals(y);
    }

    // Checks if @value can be practically considered 0.
    // true if -0.0001 <= value <= 0.0001.
    public static boolean isZeroDouble(double value) {
        return (value <= ZERO_DOUBLE) && (value >= -ZERO_DOUBLE);
    }
}
