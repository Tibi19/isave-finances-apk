package com.tam.isave.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.tam.isave.model.MainBudget;
import com.tam.isave.utils.Constants;

public class MainBudgetPreferences {

    private SharedPreferences sharedPreferences;

    public MainBudgetPreferences(Application application) {
        sharedPreferences = application.getSharedPreferences(Constants.PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    }

    public double readBudget() {
        return sharedPreferences.getFloat(Constants.PREFERENCES_KEY_MAIN_BUDGET_VALUE, Constants.PREFERENCES_DEFAULT_MAIN_BUDGET_FLOAT);
    }

    public double readSpent() {
        return sharedPreferences.getFloat(Constants.PREFERENCES_KEY_MAIN_BUDGET_SPENT, Constants.PREFERENCES_DEFAULT_MAIN_BUDGET_FLOAT);
    }

    public boolean readIsHidden() {
        return sharedPreferences.getBoolean(Constants.PREFERENCES_KEY_MAIN_BUDGET_HIDDEN, Constants.PREFERENCES_DEFAULT_MAIN_BUDGET_HIDDEN);
    }

    public void saveMainBudget(MainBudget mainBudget) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(Constants.PREFERENCES_KEY_MAIN_BUDGET_VALUE, (float) mainBudget.getBudget());
        editor.putFloat(Constants.PREFERENCES_KEY_MAIN_BUDGET_SPENT, (float) mainBudget.getSpent());
        editor.putBoolean(Constants.PREFERENCES_KEY_MAIN_BUDGET_HIDDEN, mainBudget.isHidden());

        editor.apply();
    }

}
