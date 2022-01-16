package com.tam.isave.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.utils.Constants;

public class GoalOrganizerPreferences {

    private SharedPreferences sharedPreferences;

    public GoalOrganizerPreferences(Application application) {
        sharedPreferences = application.getSharedPreferences(Constants.PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    }

    public double readGlobalGoal() {
        return sharedPreferences.getFloat(Constants.PREFERENCES_KEY_GLOBAL_GOAL, Constants.PREFERENCES_DEFAULT_ORGANIZER_FLOAT);
    }

    public int readGlobalDays() {
        return sharedPreferences.getInt(Constants.PREFERENCES_KEY_GLOBAL_DAYS, Constants.PREFERENCES_DEFAULT_INT);
    }

    public int readFirstDayValue() {
        return sharedPreferences.getInt(Constants.PREFERENCES_KEY_FIRST_DAY_VALUE, Constants.PREFERENCES_DEFAULT_INT);
    }

    public int readIntervalsCount() {
        return sharedPreferences.getInt(Constants.PREFERENCES_KEY_INTERVALS_COUNT, Constants.PREFERENCES_DEFAULT_INT);
    }

    public void saveGoalOrganizer(GoalOrganizer organizer) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(Constants.PREFERENCES_KEY_GLOBAL_GOAL, (float) organizer.getGlobalGoal());
        editor.putInt(Constants.PREFERENCES_KEY_GLOBAL_DAYS, organizer.getGlobalIntervalDays());
        editor.putInt(Constants.PREFERENCES_KEY_FIRST_DAY_VALUE, organizer.getFirstDay().getValue());
        editor.putInt(Constants.PREFERENCES_KEY_INTERVALS_COUNT, organizer.getIntervalsNr());

        editor.apply();
    }

    public void deleteGoalOrganizer() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(Constants.PREFERENCES_KEY_GLOBAL_GOAL, Constants.PREFERENCES_DEFAULT_ORGANIZER_FLOAT);
        editor.putInt(Constants.PREFERENCES_KEY_GLOBAL_DAYS, Constants.PREFERENCES_DEFAULT_INT);
        editor.putInt(Constants.PREFERENCES_KEY_FIRST_DAY_VALUE, Constants.PREFERENCES_DEFAULT_INT);
        editor.putInt(Constants.PREFERENCES_KEY_INTERVALS_COUNT, Constants.PREFERENCES_DEFAULT_INT);

        editor.apply();
    }
}
