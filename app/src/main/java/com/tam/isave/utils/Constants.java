package com.tam.isave.utils;

public class Constants {

    // Settings
    public static final int TRANSACTION_DAYS_LIMIT = 120; // User should not add transactions older than 4 months.

    // Default Values
    public static final int DEFAULT_INTERVALS_COUNT = 5;
    public static final int DEFAULT_ORGANIZER_DAYS = 30;
    public static final String DEFAULT_NOT_APPLICABLE = "N/A";

    // Naming
    public final static String NAMING_HISTORY = "History";
    public final static String NAMING_INTERVAL = "Interval ";
    public final static String NAMING_FROM_CATEGORY = "From category ";
    public final static String NAMING_BUDGET_HIDDEN = "***.**";
    public final static String NAMING_NO_CATEGORY = "None";

    // Feedback
    public final static String FEEDBACK_SYNCED_WITH_MAIN_BUDGET = "Synced with main budget.";

    // Category Database Constants
    public final static String CATEGORY_DATABASE = "category_database";

    public static final int VERSION_CATEGORY_TABLE = 3;
    public final static String TABLE_NAME_CATEGORY = "category_table";

    public final static String CATEGORY_COLUMN_NAME = "name";
    public final static String CATEGORY_COLUMN_SPENT = "spent";
    public final static String CATEGORY_COLUMN_GOAL = "goal";
    public final static String CATEGORY_COLUMN_GOAL_MODIFIER = "goal_modifier";
    public final static String CATEGORY_COLUMN_GOAL_PASSED = "goal_passed";
    public final static String CATEGORY_COLUMN_FLEXIBLE_GOAL = "flexible_goal";

    // Interval Database Constants
    public final static String INTERVAL_DATABASE = "interval_database";

    public static final int VERSION_INTERVAL_TABLE = 2;
    public final static String TABLE_NAME_INTERVAL = "interval_table";

    public final static String INTERVAL_COLUMN_DAYS = "days";

    // Transaction Database Constants
    public final static String TRANSACTION_DATABASE = "transaction_database";

    public static final int VERSION_TRANSACTION_TABLE = 3;
    public final static String TABLE_NAME_TRANSACTION = "transaction_table";

    public final static String TRANSACTION_COLUMN_NAME = "name";
    public final static String TRANSACTION_COLUMN_VALUE = "value";
    public final static String TRANSACTION_COLUMN_DATE_VALUE = "date_value";
    public final static String TRANSACTION_COLUMN_PARENT_ID = "parent_id";
    public final static String TRANSACTION_COLUMN_ORGANIZABLE = "organizable";

    // Shared Preferences Constants
    public final static String PREFERENCES_FILE_KEY = "com.tam.isave.PREFERENCES_FILE_KEY";
    public final static String PREFERENCES_KEY_GLOBAL_GOAL = "global_goal_key";
    public final static String PREFERENCES_KEY_GLOBAL_DAYS = "global_days_key";
    public final static String PREFERENCES_KEY_FIRST_DAY_VALUE = "first_day_value_key";
    public final static String PREFERENCES_KEY_INTERVALS_COUNT = "intervals_count_key";
    public final static String PREFERENCES_KEY_MAIN_BUDGET_VALUE = "main_budget_value_key";
    public final static String PREFERENCES_KEY_MAIN_BUDGET_SPENT = "main_budget_spent_key";
    public final static String PREFERENCES_KEY_MAIN_BUDGET_HIDDEN = "main_budget_hidden_key";
    public final static boolean PREFERENCES_DEFAULT_MAIN_BUDGET_HIDDEN = true;
    public final static float PREFERENCES_DEFAULT_MAIN_BUDGET_FLOAT = 0.0f;
    public final static float PREFERENCES_DEFAULT_ORGANIZER_FLOAT = -1.0f;
    public final static int PREFERENCES_DEFAULT_INT = -1;

    // Bundle Keys
    public static final String KEY_HISTORY_TYPE = "history_type_key";
    public static final String KEY_CATEGORY_ID = "category_id_key";
    public static final String KEY_CATEGORY_NAME = "category_name_key";
    public static final String KEY_START_DATE_VALUE = "start_date_value_key";
    public static final String KEY_END_DATE_VALUE = "end_date_value_key";

    // Export transactions values
    public static final String EXPORT_INTENT_TITLE = "Handle transactions file with:";
    public static final String EXPORT_FILE_NAME_START = "isave_transactions_";
}
