package com.tam.isave.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.tam.isave.R;
import com.tam.isave.databinding.ActivityIntervalHistoryBinding;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.model.goalorganizer.Interval;
import com.tam.isave.model.goalorganizer.IntervalUtils;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.DebugUtils;
import com.tam.isave.utils.HistoryIdentifier;
import com.tam.isave.utils.LiveDataUtils;
import com.tam.isave.utils.NumberUtils;
import com.tam.isave.view.fragment.HistoryFragment;
import com.tam.isave.viewmodel.GoalOrganizerViewModel;
import com.tam.isave.viewmodel.TransactionViewModel;

public class IntervalHistoryActivity extends AppCompatActivity {

    private ActivityIntervalHistoryBinding binding;
    private TransactionViewModel transactionViewModel;
    private GoalOrganizerViewModel organizerViewModel;
    private GoalOrganizer organizer;
    private Interval focusInterval;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntervalHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        organizerViewModel = new ViewModelProvider(this).get(GoalOrganizerViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        organizer = organizerViewModel.getGoalOrganizer();

        initializeFocusInterval();
        updateHistoryFragment();
        updateBinding();
        setupUpdateBindingObserver();
        setupPreviousButton();
        setupNextButton();
    }

    private void setupUpdateBindingObserver() {
        transactionViewModel.getTransactions().observe(
                this,
                transactions -> updateBinding()
        );
    }

    private void initializeFocusInterval() {
        if(organizer == null) { return; }

        if(organizer.isActive()) {
            focusInterval = organizer.getActiveInterval();
            return;
        }

        Interval[] intervals = organizer.getIntervals();
        if(intervals == null || intervals.length == 0) { return; }
        if(organizer.getFirstDay().isAfterToday()) { focusInterval = intervals[0]; }
        else focusInterval = intervals[intervals.length - 1]; // Last day being before today is the only scenario left.
    }

    private Bundle getHistoryBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HISTORY_TYPE, HistoryIdentifier.HISTORY_TYPE_INTERVAL);
        bundle.putInt(Constants.KEY_START_DATE_VALUE, IntervalUtils.getIntervalFirstDayValue(focusInterval, organizer));
        bundle.putInt(Constants.KEY_END_DATE_VALUE, IntervalUtils.getIntervalLastDayValue(focusInterval, organizer));

        return bundle;
    }

    private void updateHistoryFragment() {
        if(focusInterval == null) { return; }

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fragmentContainerHistory.getId(), HistoryFragment.class, getHistoryBundle())
                .commit();
    }

    private void replaceHistoryFragment() {
        if(focusInterval == null) { return; }

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(binding.fragmentContainerHistory.getId(), HistoryFragment.class, getHistoryBundle())
                .commit();
    }

    private void updateBinding() {
        if(focusInterval == null) { return; }

        Date intervalFirstDay = new Date(IntervalUtils.getIntervalFirstDayValue(focusInterval, organizer));
        Date intervalLastDay = new Date(IntervalUtils.getIntervalLastDayValue(focusInterval, organizer));
        int intervalDays = focusInterval.getDays();

        binding.titleIntervalName.setText(focusInterval.getName());
        binding.tvBudgetDetails.setText(focusInterval.getDetailedProgress());
        binding.tvFirstDayDetails.setText(intervalFirstDay.getFormatDDMMMYY());
        binding.tvLastDayDetails.setText(intervalLastDay.getFormatDDMMMYY());
        binding.tvDaysDetails.setText(String.valueOf(intervalDays));

        toggleDelimiterBasedOnTransactions(intervalFirstDay, intervalLastDay);
    }

    private void toggleDelimiterBasedOnTransactions(Date intervalFirstDay, Date intervalLastDay) {
        LiveDataUtils.observeOnce(
                transactionViewModel.getIntervalTransactions(intervalFirstDay.getValue(), intervalLastDay.getValue()),
                transactions -> {
                    boolean isDelimiterHidden = NumberUtils.isZeroDouble(binding.transactionDelimiter.getAlpha());

                    if(transactions.isEmpty() && !isDelimiterHidden) {
                        removeDelimiter();
                        return;
                    }

                    if(!transactions.isEmpty() && isDelimiterHidden) {
                        restoreDelimiter();
                    }
                }
        );
    }

    private void restoreDelimiter() {
        Context context = getApplicationContext();
        int marginsPxHorizontal = context.getResources().getDimensionPixelSize(R.dimen.line_horizontal_margin);
        int marginsPxVertical = context.getResources().getDimensionPixelSize(R.dimen.line_vertical_margin_small);

        View delimiter = binding.transactionDelimiter;

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) delimiter.getLayoutParams();
        layoutParams.setMargins(marginsPxHorizontal, marginsPxVertical, marginsPxHorizontal, marginsPxVertical);

        delimiter.setAlpha(1.0f);
        delimiter.setLayoutParams(layoutParams);
    }

    private void removeDelimiter() {
        View delimiter = binding.transactionDelimiter;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) delimiter.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        delimiter.setAlpha(0.0f);
        delimiter.setLayoutParams(layoutParams);
    }

    private void setupPreviousButton() {
        binding.buttonPreviousInterval.setOnClickListener(changeFocusToPreviousInterval -> {
            if(focusInterval == null) { return; }

            Interval previousInterval = IntervalUtils.getPreviousInterval(focusInterval, organizer);
            if(previousInterval == null) { return; }

            focusInterval = previousInterval;
            replaceHistoryFragment();
            updateBinding();
        });
    }

    private void setupNextButton() {
        binding.buttonNextInterval.setOnClickListener(changeFocusToNextInterval -> {
            if(focusInterval == null) { return; }

            Interval nextInterval = IntervalUtils.getNextInterval(focusInterval, organizer);
            if(nextInterval == null) { return; }

            focusInterval = nextInterval;
            replaceHistoryFragment();
            updateBinding();
        });
    }
}
