package com.tam.isave.utils;

public class TextUtils {

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
