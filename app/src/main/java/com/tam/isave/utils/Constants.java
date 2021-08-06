package com.tam.isave.utils;

import androidx.annotation.NonNull;

public class Constants {

    // Naming
    public final static String NAMING_HISTORY = "History";

    // Category Database Constants
    public final static String CATEGORY_DATABASE = "category_database";

    public static final int VERSION_CATEGORY_TABLE = 2;
    public final static String TABLE_NAME_CATEGORY = "category_table";

    public final static String CATEGORY_COLUMN_NAME = "name";
    public final static String CATEGORY_COLUMN_SPENT = "spent";
    public final static String CATEGORY_COLUMN_GOAL = "goal";
    public final static String CATEGORY_COLUMN_GOAL_MODIFIER = "goal_modifier";
    public final static String CATEGORY_COLUMN_GOAL_PASSED = "goal_passed";
    public final static String CATEGORY_COLUMN_FLEXIBLE_GOAL = "flexible_goal";

    // Transaction Database Constants
    public final static String TRANSACTION_DATABASE = "transaction_database";

    public static final int VERSION_TRANSACTION_TABLE = 1;
    public final static String TABLE_NAME_TRANSACTION = "transaction_table";

    public final static String TRANSACTION_COLUMN_NAME = "name";
    public final static String TRANSACTION_COLUMN_VALUE = "value";
    public final static String TRANSACTION_COLUMN_DATE_VALUE = "date_value";
    public final static String TRANSACTION_COLUMN_PARENT_ID = "parent_id";

    // Bundle Keys
    public static final String KEY_HISTORY_TYPE = "history_type_key";
    public static final String KEY_CATEGORY_ID = "category_id_key";
    public static final String KEY_CATEGORY_NAME = "category_name_key";
    public static final String KEY_START_DATE_VALUE = "start_date_value_key";
    public static final String KEY_END_DATE_VALUE = "end_date_value_key";
}
