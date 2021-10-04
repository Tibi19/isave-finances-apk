package com.tam.isave.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tam.isave.databinding.FragmentOrganizerBinding;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Date;
import com.tam.isave.viewmodel.GoalOrganizerViewModel;
import com.tam.isave.viewmodel.TransactionViewModel;

import java.util.List;

public class GoalOrganizerFragment extends Fragment {

    private FragmentOrganizerBinding binding;
    private GoalOrganizerViewModel organizerViewModel;

    private TransactionViewModel transactionViewModel;
    private LiveData<List<Transaction>> transactionsLiveData;
    private Observer<List<Transaction>> transactionsObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrganizerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        organizerViewModel = new ViewModelProvider(this).get(GoalOrganizerViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transactionsObserver = transactions -> updateBinding();

        setupOrganizerController();
        attachObserver();
    }

    private void attachObserver() {
        int firstDayValue = organizerViewModel.getGoalOrganizerFirstDayValue();
        int goalOrganizerDays = organizerViewModel.getGoalOrganizerDays();
        transactionsLiveData = transactionViewModel.getGoalOrganizerTransactions(firstDayValue, goalOrganizerDays);

        if(transactionsLiveData == null) { return; }

        transactionsLiveData.observe(getViewLifecycleOwner(), transactionsObserver);
    }

    private void updateBinding() {
        GoalOrganizer organizer = organizerViewModel.getGoalOrganizer();
        binding.tvIntervalDetails.setText(organizer.getIntervalsProgressString());
        binding.tvDayDetails.setText(organizer.getDaysProgressString());
        binding.tvOrganizerProgress.setText(organizer.getBudgetProgress());
    }

    private void setupOrganizerController() {
        // binding.btnOrganizerHistory.setOnClickListener();
        binding.btnOrganizerEdit.setOnClickListener(listener -> showEditOrganizerPopup());
        binding.btnOrganizerReset.setOnClickListener(listener -> deleteOrganizer());
    }

    private void showEditOrganizerPopup() {
        int originalFirstDayValue = organizerViewModel.getGoalOrganizerFirstDayValue();
        int originalOrganizerDays = organizerViewModel.getGoalOrganizerDays();

        // TESTING
//        Date firstDay = Date.today(); // Test with old days next.
//        Date lastDay = firstDay.addDays(DebugUtils.getRandomIntInRange(15, 30));
//        double globalGoal = DebugUtils.getRandomDoubleInRange(500, 5000);
//        int intervalsCount = DebugUtils.getRandomIntInRange(2, 7);
//        organizerViewModel.updateGoalOrganizer(globalGoal, intervalsCount, firstDay, lastDay);
//        updateBinding();

        Date firstDay = new Date(20210912);
        Date lastDay = firstDay.addDays(30);
        double globalGoal = 3000;
        int intervalsCount = 6;
        organizerViewModel.updateGoalOrganizer(globalGoal, intervalsCount, firstDay, lastDay);

        resetObserverIfTimeChanged(originalFirstDayValue, originalOrganizerDays);
    }

    // If first day or total days change in goal organizer, then we need to observe a different set of transactions.
    private void resetObserverIfTimeChanged(int originalFirstDayValue, int originalOrganizerDays) {
        boolean isFirstDayChanged = originalFirstDayValue != organizerViewModel.getGoalOrganizerFirstDayValue();
        boolean isOrganizerDaysChanged = originalOrganizerDays != organizerViewModel.getGoalOrganizerDays();

        if(!isFirstDayChanged || !isOrganizerDaysChanged) { return; }

        transactionsLiveData.removeObserver(transactionsObserver);
        attachObserver();
    }

    private void deleteOrganizer() {
        organizerViewModel.deleteGoalOrganizer();
        updateBinding();
    }
}
