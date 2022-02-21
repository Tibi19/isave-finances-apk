package com.tam.isave.view.dialog;

import android.content.Context;
import android.widget.Toast;

public class ErrorBuilder {

    private static final String NEGATIVE_VALUE = "Value can't be negative.";
    public static void negativeValue(Context context) { makeErrorToast(context, NEGATIVE_VALUE); }

    private static final String MISSING_VALUE = "Please insert value.";
    public static void missingValue(Context context) { makeErrorToast(context, MISSING_VALUE); }

    private static final String CATEGORY_NAME_EXISTS = "Category name already exists.";
    public static void categoryNameExists(Context context) { makeErrorToast(context, CATEGORY_NAME_EXISTS); }

    private static final String MISSING_OTHER_CATEGORIES = "Please add more categories.";
    public static void missingOtherCategories(Context context) { makeErrorToast(context, MISSING_OTHER_CATEGORIES); }

    private static final String INTERVALS_RANGE = "Organizer can have between 1 and 10 intervals.";
    public static void intervalsRange(Context context) { makeErrorToast(context, INTERVALS_RANGE); }

    private static final String ORGANIZER_DAYS_ORDER = "Last day must be after first day.";
    public static void organizerDaysOrder(Context context) { makeErrorToast(context, ORGANIZER_DAYS_ORDER); }

    private static void makeErrorToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
