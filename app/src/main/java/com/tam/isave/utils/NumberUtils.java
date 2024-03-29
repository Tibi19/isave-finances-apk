package com.tam.isave.utils;

public class NumberUtils {

    // To be used for double comparisons to 0.
    public static final double ZERO_DOUBLE = 0.0001;

    public static double twoDecimalsRounded(double value) {
        int tempValue = (int) Math.round(value * 100.0);
        return tempValue / 100.0;
    }

    // Whether parameters are the same with a precision of 2 decimals.
    public static boolean isSameDoubles(double x, double y) {
        return twoDecimalsRounded(x) == twoDecimalsRounded(y);
    }

    // Checks if @value can be practically considered 0.
    // true if -0.0001 <= value <= 0.0001.
    public static boolean isZeroDouble(double value) {
        return (value <= ZERO_DOUBLE) && (value >= -ZERO_DOUBLE);
    }
}
