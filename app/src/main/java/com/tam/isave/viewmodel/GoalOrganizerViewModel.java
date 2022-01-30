package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.model.ModelRepository;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.model.goalorganizer.Interval;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.DebugUtils;
import com.tam.isave.utils.NumberUtils;

import java.util.List;

public class GoalOrganizerViewModel extends AndroidViewModel {

    private ModelRepository modelRepository;
    private LiveData<List<Interval>> intervals;

    public GoalOrganizerViewModel(@NonNull Application application) {
        super(application);
        modelRepository = ModelRepository.getModelRepository(application);
        intervals = modelRepository.getIntervals();
    }

    public void modifyOrganizer(double globalGoal, int intervalsCount, Date firstDay, Date lastDay, List<Transaction> intervalTransactions) {
        if(globalGoal <= NumberUtils.ZERO_DOUBLE || intervalsCount <= 0) { return; }
        if(firstDay == null || lastDay == null) { return; }

        modelRepository.modifyGoalOrganizer(globalGoal, intervalsCount, firstDay, lastDay, intervalTransactions);
    }

    public void addCashing(double cashingValue, boolean shouldReset) {
        if( cashingValue < -NumberUtils.ZERO_DOUBLE ) { return; }
        GoalOrganizer organizer = getGoalOrganizer();
        double goal = organizer.getGlobalGoal();
        List<Transaction> transactions = organizer.getHistory().getHistoryList();

        if(shouldReset) {
            goal = 0.0;
            transactions = organizer.getHistory().getTransactionsOfToday();
            resetGoalOrganizer();
        }

        goal += cashingValue;
        modelRepository.modifyGoalOrganizer(goal,
                organizer.getIntervalsNr(), organizer.getFirstDay(), organizer.getLastDay(), transactions);
    }

    public void updateOrganizer() {
        modelRepository.updateOrganizer();
    }

    public int getGoalOrganizerFirstDayValue() {
        Date goalOrganizerFirstDay = modelRepository.getGoalOrganizer().getFirstDay();
        if(goalOrganizerFirstDay == null) { return -1; }
        return goalOrganizerFirstDay.getValue();
    }

    public int getGoalOrganizerDays() {
        return modelRepository.getGoalOrganizer().getGlobalIntervalDays();
    }

    public GoalOrganizer getGoalOrganizer() {
        return modelRepository.getGoalOrganizer();
    }

    public LiveData<List<Interval>> getIntervals() {
        return intervals;
    }

    public void resetGoalOrganizer() { modelRepository.resetGoalOrganizer(); }
}
