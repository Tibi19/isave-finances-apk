package com.tam.isave.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tam.isave.databinding.FragmentOrganizerBinding;
import com.tam.isave.databinding.PopupEditOrganizerBinding;
import com.tam.isave.model.MainBudget;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.LiveDataUtils;
import com.tam.isave.utils.NumberUtils;
import com.tam.isave.utils.OrganizerBindingUtils;
import com.tam.isave.view.activity.IntervalHistoryActivity;
import com.tam.isave.view.dialog.ConfirmationBuilder;
import com.tam.isave.view.dialog.EditTextDatePicker;
import com.tam.isave.viewmodel.GoalOrganizerViewModel;
import com.tam.isave.viewmodel.MainBudgetViewModel;
import com.tam.isave.viewmodel.TransactionViewModel;

import java.util.List;

public class GoalOrganizerFragment extends Fragment {

    private FragmentOrganizerBinding binding;
    private GoalOrganizerViewModel organizerViewModel;

    private TransactionViewModel transactionViewModel;
    private LiveData<List<Transaction>> transactionsLiveData;
    private Observer<List<Transaction>> bindingUpdateObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrganizerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        organizerViewModel.updateOrganizer();
        updateBinding();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        organizerViewModel = new ViewModelProvider(this).get(GoalOrganizerViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        bindingUpdateObserver = transactions -> updateBinding();

        OrganizerBindingUtils.setUpdateBinding(this::updateBinding);
        OrganizerBindingUtils.setResetBindingIfTimeChange(this::resetBindingObserverIfTimeChange);

        setupGoalOrganizerTransactions();
        setupOrganizerController();
        attachBindingUpdateObserver();
    }

    private void setupGoalOrganizerTransactions() {
        int firstDayValue = organizerViewModel.getGoalOrganizerFirstDayValue();
        int goalOrganizerDays = organizerViewModel.getGoalOrganizerDays();

        LiveData<List<Transaction>> organizerTransactions = transactionViewModel
                .getGoalOrganizerTransactions(firstDayValue, goalOrganizerDays);

        if(organizerTransactions == null) { return; }

        LiveDataUtils.observeOnce(organizerTransactions,
                transactions -> {
                    transactionViewModel.setupGoalOrganizerTransactions(transactions);
                    updateBinding();
                });
    }

    private void attachBindingUpdateObserver() {
        int firstDayValue = organizerViewModel.getGoalOrganizerFirstDayValue();
        int goalOrganizerDays = organizerViewModel.getGoalOrganizerDays();
        transactionsLiveData = transactionViewModel.getGoalOrganizerTransactions(firstDayValue, goalOrganizerDays);

        if(transactionsLiveData == null) { return; }

        transactionsLiveData.observe(getViewLifecycleOwner(), bindingUpdateObserver);
    }

    private void updateBinding() {
        GoalOrganizer organizer = organizerViewModel.getGoalOrganizer();
        binding.tvIntervalDetails.setText(organizer.getIntervalsProgressString());
        binding.tvDayDetails.setText(organizer.getDaysProgressString());
        binding.tvOrganizerProgress.setText(organizer.getBudgetProgress());
    }

    private void setupOrganizerController() {
        binding.btnOrganizerHistory.setOnClickListener(listener -> startIntervalHistoryActivity());
        binding.btnOrganizerEdit.setOnClickListener(listener -> showEditOrganizerPopup());
        binding.btnOrganizerReset.setOnClickListener(listener -> showResetOrganizerPopup());
    }

    private void showResetOrganizerPopup() {
        Runnable resetRunnable = () -> {
            resetOrganizer();
            updateBinding();
        };

        ConfirmationBuilder.showResetConfirmation(
                getLayoutInflater(),
                ConfirmationBuilder.ResetConfirmationType.ORGANIZER,
                resetRunnable
        );
    }

    private void resetOrganizer() {
        int originalFirstDayValue = organizerViewModel.getGoalOrganizerFirstDayValue();
        int originalOrganizerDays = organizerViewModel.getGoalOrganizerDays();

        int firstDayValueForReset = organizerViewModel.getFirstDayValueForReset();
        int lastDayValueForReset = organizerViewModel.getLastDayValueForReset();

        double mainBudgetValue = getMaiBudgetValue();

        LiveDataUtils.observeOnce(
                transactionViewModel.getIntervalTransactions(firstDayValueForReset, lastDayValueForReset),
                transactions -> {
                    organizerViewModel.resetGoalOrganizer(mainBudgetValue, transactions);
                    updateBinding();
                    resetBindingObserverIfTimeChange(originalFirstDayValue, originalOrganizerDays);
                }
        );
    }

    private void startIntervalHistoryActivity() {
        Intent startIntervalHistoryIntent = new Intent(getContext(), IntervalHistoryActivity.class);
        startActivity(startIntervalHistoryIntent);
    }

    private void showEditOrganizerPopup() {
        AlertDialog editOrganizerDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        PopupEditOrganizerBinding editOrganizerBinding = PopupEditOrganizerBinding.inflate(getLayoutInflater());

        builder.setView(editOrganizerBinding.getRoot());
        editOrganizerDialog = builder.create();
        editOrganizerDialog.setCancelable(true);
        editOrganizerDialog.getWindow().setGravity(Gravity.BOTTOM);

        populateEditBinding(editOrganizerBinding);

        int originalFirstDayValue = organizerViewModel.getGoalOrganizerFirstDayValue();
        int originalOrganizerDays = organizerViewModel.getGoalOrganizerDays();

        editOrganizerBinding.buttonEditOrganizerCancel.setOnClickListener(listener -> editOrganizerDialog.dismiss());
        editOrganizerBinding.buttonEditOrganizerSubmit.setOnClickListener(listener -> {
            if( !organizerViewModel.isModifyOrganizerValid(editOrganizerBinding, getContext()) ) { return; }
            editOrganizerWithBinding(editOrganizerBinding, originalFirstDayValue, originalOrganizerDays);
            editOrganizerDialog.dismiss();
        });
        editOrganizerBinding.buttonEditOrganizerSync.setOnClickListener(
                listener -> syncStateWithMainBudget(editOrganizerBinding)
        );

        editOrganizerDialog.show();
    }

    private void syncStateWithMainBudget(PopupEditOrganizerBinding editOrganizerBinding) {
        double mainBudgetValue = getMaiBudgetValue();
        editOrganizerBinding.etEditOrganizerBudget.setText(String.valueOf(mainBudgetValue));

        Toast.makeText(
                getContext(),
                Constants.FEEDBACK_SYNCED_WITH_MAIN_BUDGET,
                Toast.LENGTH_SHORT
        ).show();
    }

    private double getMaiBudgetValue() {
        MainBudgetViewModel mainBudgetViewModel = new ViewModelProvider(this).get(MainBudgetViewModel.class);
        MainBudget mainBudget = mainBudgetViewModel.getMainBudget();

        return mainBudget == null ? 0.0 : mainBudget.getBudget();
    }

    private void populateEditBinding(PopupEditOrganizerBinding editOrganizerBinding) {
        GoalOrganizer organizer = organizerViewModel.getGoalOrganizer();
        if(organizer == null || !organizer.isActive()) {
            populateEditBindingFallback(editOrganizerBinding);
            return;
        }

        int intervalsCount = organizer.getIntervalsNr();
        double budget = NumberUtils.twoDecimalsRounded(organizer.getGlobalGoal());

        EditTextDatePicker.build(getActivity(), editOrganizerBinding.etEditOrganizerStart, organizer.getFirstDay());
        EditTextDatePicker.build(getActivity(), editOrganizerBinding.etEditOrganizerEnd, organizer.getLastDay());
        editOrganizerBinding.etEditOrganizerIntervals.setText(String.valueOf(intervalsCount));
        editOrganizerBinding.etEditOrganizerBudget.setText(String.valueOf(budget));
    }

    private void populateEditBindingFallback(PopupEditOrganizerBinding editOrganizerBinding) {
        Date defaultFirstDay = Date.today();
        Date defaultLastDay = defaultFirstDay.countDays(Constants.DEFAULT_ORGANIZER_DAYS);
        int defaultIntervalsCount = Constants.DEFAULT_INTERVALS_COUNT;

        EditTextDatePicker.build(getActivity(), editOrganizerBinding.etEditOrganizerStart, defaultFirstDay);
        EditTextDatePicker.build(getActivity(), editOrganizerBinding.etEditOrganizerEnd, defaultLastDay);
        editOrganizerBinding.etEditOrganizerIntervals.setText(String.valueOf(defaultIntervalsCount));
    }

    private void editOrganizerWithBinding(PopupEditOrganizerBinding editOrganizerBinding, int originalFirstDayValue, int originalOrganizerDays) {
        String globalGoalString = editOrganizerBinding.etEditOrganizerBudget.getText().toString();
        double globalGoal = globalGoalString.isEmpty() ? -1.0 : Double.parseDouble(globalGoalString);

        String intervalsCountString = editOrganizerBinding.etEditOrganizerIntervals.getText().toString();
        int intervalsCount = intervalsCountString.isEmpty() ? -1 : Integer.parseInt(intervalsCountString);

        Date firstDay = new Date(editOrganizerBinding.etEditOrganizerStart.getText().toString());
        Date lastDay = new Date(editOrganizerBinding.etEditOrganizerEnd.getText().toString());

        updateGoalOrganizer(globalGoal, intervalsCount, firstDay, lastDay, originalFirstDayValue, originalOrganizerDays);
    }

    private void updateGoalOrganizer(
            double globalGoal,
            int intervalsCount,
            Date firstDay,
            Date lastDay,
            int originalFirstDayValue,
            int originalOrganizerDays
    ) {
        if(firstDay == null || lastDay == null) { return; }

        LiveDataUtils.observeOnce(
                transactionViewModel.getIntervalTransactions(firstDay.getValue(), lastDay.getValue()),
                transactions -> {
                    organizerViewModel.modifyOrganizer(globalGoal, intervalsCount, firstDay, lastDay, transactions);
                    updateBinding();
                    resetBindingObserverIfTimeChange(originalFirstDayValue, originalOrganizerDays);
                }
        );
    }

    // If first day or total days change in goal organizer, then we need to observe a different set of transactions.
    private void resetBindingObserverIfTimeChange(int originalFirstDayValue, int originalOrganizerDays) {
        boolean isFirstDayChanged = originalFirstDayValue != organizerViewModel.getGoalOrganizerFirstDayValue();
        boolean isOrganizerDaysChanged = originalOrganizerDays != organizerViewModel.getGoalOrganizerDays();

        if(!isFirstDayChanged || !isOrganizerDaysChanged) { return; }

        transactionsLiveData.removeObserver(bindingUpdateObserver);
        attachBindingUpdateObserver();
    }
}
