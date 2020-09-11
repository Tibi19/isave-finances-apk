package com.tam.isave;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Date {

    private int day;
    private int month;
    private int year;
    private String errorMsg = "date error: ";

    public enum MONTH_ID {
        JAN(1), FEB(2), MAR(3), APR(4), MAY(5), JUN(6), JUL(7), AUG(8), SEP(9), OCT(10), NOV(11), DEC(12);

        private int id;
        MONTH_ID (final int id) { this.id = id; }
        public int getId() { return id; }
    }

    // Returns Date of today;
    public static Date today() {
        // Indexes describing location of needed data when splitting a java.util.Date object's String.
        final int MONTH_INDEX = 1;
        final int DAY_INDEX = 2;
        final int YEAR_INDEX = 5;

        // Get today's date and split the java.util.Date object's string by spaces.
        java.util.Date today = Calendar.getInstance().getTime();
        String[] todayDetails = today.toString().split(" ");

        // java.util.Date string describes month as a 3 letter word, search for the needed int value in MONTH_ID.
        int month = 0;
        for(MONTH_ID monthId : MONTH_ID.values()) {
            if (monthId.toString().equalsIgnoreCase(todayDetails[MONTH_INDEX])) {
                month = monthId.getId();
            }
        }

        int day = Integer.parseInt(todayDetails[DAY_INDEX]);
        int year = Integer.parseInt(todayDetails[YEAR_INDEX]);
        return new Date(day, month, year);
    }

    public Date(int day, int month, int year) {
        setYear(year);
        setMonth(month);
        setDay(day);
    }

    // Whether the date is valid or not and can be used.
    public boolean isValidDate() {
        return !( (day < 0) || (month < 0) || (year < 0) );
    }

    // Checks if @day is int between [1, @daysInMonth] and assigns it to this.day if true;
    // Otherwise, assigns error value to day and adds indicator to this.errorMsg.
    public void setDay(int day) {
        int daysInMonth = getDaysInMonth(month, year);
        if (day < 1 || day > daysInMonth) {
            this.day = -1;
            errorMsg += "day";
            return;
        }

        this.day = day;
    }

    // Checks if @month is int between [1, 12] and assigns it to this.month if true;
    // Otherwise, assigns error value to month and adds indicator to this.errorMsg.
    public void setMonth(int month) {
        if( (month < 1) || (month > 12) ) {
            this.month = -1;
            errorMsg += "month ";
            return;
        }

        this.month = month;
    }

    // Checks if @year is of 4 digits and assigns it to this.year if true.
    // Otherwise, assigns error value to year and adds indicator to this.errorMsg.
    public void setYear(int year) {
        int divisionBy1000 = year / 1000;
        boolean hasFourDigits = (divisionBy1000 < 10) && (divisionBy1000 > 0);
        if(!hasFourDigits) {
            this.year = -1;
            errorMsg += "year ";
            return;
        }

        this.year = year;
    }

    private int getDaysInMonth (int month, int year) {
        if (month < 1 || month > 12) {
            return -1;
        }

        if (year < 1 || year > 9999) {
            return -1;
        }

        final int MONTH_DAYS_LONG = 31;
        final int MONTH_DAYS_SHORT = 30;
        final int FEB_DAYS_DEFAULT = 28;
        final int FEB_DAYS_LEAP = 29;

        // If month is February, return appropriate days if it is a leap year or not.
        if (month == 2) {
            return isLeapYear(year) ? FEB_DAYS_LEAP : FEB_DAYS_DEFAULT;
        }

        // If month is before August (8th month) then:
        // Even months will have 30 days and odd months will have 31 days.
        // Otherwise, Even months will have 31 days and odd ones 30 days.
        if (month < 8) {
            return month % 2 == 0 ? MONTH_DAYS_SHORT : MONTH_DAYS_LONG;
        } else {
            return month % 2 == 0 ? MONTH_DAYS_LONG : MONTH_DAYS_SHORT;
        }
    }

    private boolean isLeapYear (int year) {
        if (year < 1 || year > 9999) {
            return false;
        }

        if (year % 4 != 0) {
            return false;
        } else if (year % 100 != 0) {
            return true;
        } else if (year % 400 != 0) {
            return false;
        } else {
            return true;
        }
    }

    // Returns the Date after a number (@days) of days.
    public Date addDays(int days) {
        int day = this.day;
        int month = this.month;
        int year = this.year;

        while(days > 0) {
            int daysInMonth = getDaysInMonth(month, year);
            // How many days leftover after end of the month is reached.
            // End of the month can't be reached if negative.
            int leftoverDays = day + days - daysInMonth;

            // Add to day, month and year accordingly.
            if(leftoverDays > 0 && month < 12) {
                month++;
                day = 1;
            } else if (leftoverDays > 0) {
                year++;
                month = 1;
                day = 1;
            } else {
                day += days;
            }

            // In case month has been changed, day has been set to 1 so we have to subtract 1 from leftoverDays.
            // In case leftoverDays is negative, loop will stop and we have reached the desired date in the last "else".
            days = leftoverDays - 1;
        }

        return new Date(day, month, year);
    }

    // Returns how many days are between @date and @this.
    public int differenceInDays(Date date) {
        // Set first date and last date by comparing them.
        Date firstDate;
        Date lastDate;
        if(this.getValue() < date.getValue()) {
            firstDate = this;
            lastDate = date;
        } else {
            firstDate = date;
            lastDate = this;
        }

        // Find total days from the start of the year until the given date.
        int totalDaysFirstDate = getDaysUntilDate(firstDate);
        int totalDaysLastDate = getDaysUntilDate(lastDate);

        // If difference between the years, we'll have to account for that as well.
        // At this point, we're counting how many days have passed since @firstDate's year's beginning.
        if(firstDate.year < lastDate.year) {
            // Add days between the first date and how many days are until the end of its year.
            totalDaysLastDate += getDaysUntilEndOfYear(firstDate);
            // Add the days of any years in between firstDate's year and lastDate's year.
            int iterateYear = firstDate.year;
            int endYear = lastDate.year - 1;
            while(iterateYear++ < endYear) {
                totalDaysFirstDate += isLeapYear(iterateYear) ? 366 : 365;
            }
        }

        // Return the difference in days.
        return totalDaysLastDate - totalDaysFirstDate;
    }

    // How many days are from the start of the year until the given @date.
    private int getDaysUntilDate(Date date) {
        int days = 0;
        for(int i = 1; i < date.month; i++) {
            days += getDaysInMonth(i, date.year);
        }
        days += date.getDay();
        return days;
    }

    // How many days are left until the end of the year starting from @date.
    private int getDaysUntilEndOfYear(Date date) {
        int days = 0;
        days += getDaysInMonth(date.month, date.year) - date.day;
        for(int i = date.month + 1; i <= 12; i++) {
            days += getDaysInMonth(i, date.year);
        }
        return days;
    }

    @NonNull
    @Override
    public String toString() {
        String dayToString = ( (day < 10) ? "0" : "" ) + day;
        String monthToString = ( (month < 10) ? "0" : "" ) + month;
        String yearToString = "" + (year & 100);

        // Returns date in format "dd.mm.yy".
        return dayToString + "." + monthToString + "." + yearToString;
    }

    // Returns a value in the format yyyymmdd.
    // To be used for comparing dates.
    // Ex: 20190923 < 20200109
    // If x is lower than y, x is an older Date than y.
    // If lower, older.
    // The higher, the newer.
    public int getValue() {
        int value = year * 100;
        value += month;
        value *= 100;
        value += day;

        return value;
    }

    // Returns true if caller is newer than parameter,
    // Or it is the same day.
    // An inverted call returns true if date is older but not equal to parameter.
    public boolean isNewerThan(Date date) {
        if(date == null) { return false; }
        return this.getValue() >= date.getValue();
    }

    // Returns true if caller is older than parameter,
    // Or it is the same day.
    // An inverted call returns true if date is newer but not equal to parameter.
    public boolean isOlderThan(Date date) {
        if(date == null) { return false; }
        return this.getValue() <= date.getValue();
    }

    // Returns true if dates are ordered:
    // newDate > midDate > oldDate.
    static boolean areOrdered(Date newDate, Date midDate, Date oldDate) {
        boolean firstTwoOrdered = newDate.isNewerThan(midDate);
        boolean lastTwoOrdered = oldDate.isOlderThan(midDate);
        return firstTwoOrdered && lastTwoOrdered;
    }

    public Date getClone() {
        return new Date(day, month, year);
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void dispose() {
        errorMsg = null;
    }
}
