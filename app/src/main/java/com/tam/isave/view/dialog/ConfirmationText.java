package com.tam.isave.view.dialog;

public class ConfirmationText {

    public static final String OBJECT_DESCRIPTION_TRANSACTION = "transaction ";
    public static final String OBJECT_DESCRIPTION_CATEGORY = "category ";

    public static String getDeleteQuestion(String objectDescription, String objectName) {
        return "Are you sure you want to delete the " + objectDescription + " " + objectName + "?";
    }

    private static final String WARNING_CATEGORY_RESET = "The spent amount will be set to 0. " +
            "Deleting its payments made until this point will create inconsistencies.";

    public static String getResetCategoryQuestion(String categoryName) {
        return "Are you sure you want to reset the category " + categoryName + "? " + WARNING_CATEGORY_RESET;
    }

    private static final String QUESTION_ALL_CATEGORIES_RESET = "Are you sure you want to reset all categories? " +
            "Their spent amounts will be set to 0. Deleting their payments made until this point will create inconsistencies.";
    private static final String QUESTION_ORGANIZER_RESET = "Are you sure you want to reset the organizer? " +
            "It will start again from today using the main budget.";
    private static final String QUESTION_EVERYTHING_RESET = "Are you sure you want to reset everything? " +
            "The main budget will be replaced by the cashing. " +
            "The organizer will start again from today with the new budget. " +
            "All categories will have a new budget and their spent amounts will be set to 0. " +
            "Deleting their payments made until this point will create inconsistencies.";
    private static final String QUESTION_EVERYTHING_BUT_ORGANIZER_RESET = "Are you sure you want to reset everything? " +
            "The main budget will be replaced by the cashing. " +
            "All categories will have a new budget and their spent amounts will be set to 0. " +
            "Deleting their payments made until this point will create inconsistencies.";

    public static String getResetAllCategoriesQuestion() { return QUESTION_ALL_CATEGORIES_RESET; }
    public static String getResetOrganizerQuestion() { return QUESTION_ORGANIZER_RESET; }
    public static String getResetEverythingQuestion() { return QUESTION_EVERYTHING_RESET; }
    public static String getResetEverythingButOrganizerQuestion() { return QUESTION_EVERYTHING_BUT_ORGANIZER_RESET; }

}
