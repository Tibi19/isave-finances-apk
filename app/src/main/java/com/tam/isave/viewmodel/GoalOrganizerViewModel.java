package com.tam.isave.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.databinding.PopupEditOrganizerBinding;
import com.tam.isave.model.ModelRepository;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.model.goalorganizer.Interval;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.NumberUtils;
import com.tam.isave.view.dialog.ErrorBuilder;

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

    public boolean isModifyOrganizerValid(PopupEditOrganizerBinding editOrganizerBinding, Context context) {
        String intervalsString = editOrganizerBinding.etEditOrganizerIntervals.getText().toString();
        String budgetString = editOrganizerBinding.etEditOrganizerBudget.getText().toString();
        Date firstDay = new Date(editOrganizerBinding.etEditOrganizerStart.getText().toString());
        Date lastDay = new Date(editOrganizerBinding.etEditOrganizerEnd.getText().toString());

        if(intervalsString.isEmpty() || budgetString.isEmpty()) {
            ErrorBuilder.missingValue(context);
            return false;
        }

        int intervalsCount = Integer.parseInt(intervalsString);
        if(intervalsCount < 1 || intervalsCount > 10) {
            ErrorBuilder.intervalsRange(context);
            return false;
        }

        if(firstDay.isNewerThan(lastDay)) {
            ErrorBuilder.organizerDaysOrder(context);
            return false;
        }

        return true;
    }

    public void addCashing(double cashingValue, boolean shouldReset, double mainBudgetValue, List<Transaction> newTransactions) {
        if( cashingValue < -NumberUtils.ZERO_DOUBLE ) { return; }
        GoalOrganizer organizer = getGoalOrganizer();
        double goal = organizer.getGlobalGoal();
        List<Transaction> transactions = organizer.getHistory().getHistoryList();

        if(shouldReset) {
            goal = 0.0;
            transactions = organizer.getHistory().getTransactionsOfToday();
            resetGoalOrganizer(mainBudgetValue, newTransactions);
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

    public void resetGoalOrganizer(double mainBudgetValue, List<Transaction> newTransactions) {
        GoalOrganizer organizer = getGoalOrganizer();

        double newGoal = mainBudgetValue > NumberUtils.ZERO_DOUBLE ? mainBudgetValue : organizer.getGlobalGoal();

        int intervalsCount = organizer.getIntervalsNr();
        int organizerDays = organizer.getGlobalIntervalDays();

        Date firstDay = Date.today();
        Date lastDay = firstDay.countDays(organizerDays);

        modifyOrganizer(newGoal, intervalsCount, firstDay, lastDay, newTransactions);
    }

    public int getFirstDayValueForReset() { return Date.today().getValue(); }

    public int getLastDayValueForReset() {
        Date firstDayForReset = Date.today();
        Date lastDayForReset = firstDayForReset.countDays(getGoalOrganizerDays());
        return lastDayForReset.getValue();
    }
}
