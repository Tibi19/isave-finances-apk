package com.tam.isave.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.tam.isave.databinding.ActivityIntervalHistoryBinding;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.model.goalorganizer.Interval;
import com.tam.isave.model.goalorganizer.IntervalUtils;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.HistoryIdentifier;
import com.tam.isave.view.fragment.HistoryFragment;
import com.tam.isave.viewmodel.GoalOrganizerViewModel;

public class IntervalHistoryActivity extends AppCompatActivity {

    private ActivityIntervalHistoryBinding binding;
    private GoalOrganizerViewModel organizerViewModel;
    private GoalOrganizer organizer;
    private Interval focusInterval;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntervalHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        organizerViewModel = new ViewModelProvider(this).get(GoalOrganizerViewModel.class);
        organizer = organizerViewModel.getGoalOrganizer();

        initializeFocusInterval();
        updateHistoryFragment();
        updateBinding();

        setupPreviousButton();
        setupNextButton();
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

    private void updateHistoryFragment() {
        if(focusInterval == null) { return; }

        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HISTORY_TYPE, HistoryIdentifier.HISTORY_TYPE_INTERVAL);
        bundle.putInt(Constants.KEY_START_DATE_VALUE, IntervalUtils.getIntervalFirstDayValue(focusInterval, organizer));
        bundle.putInt(Constants.KEY_END_DATE_VALUE, IntervalUtils.getIntervalLastDayValue(focusInterval, organizer));
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fragmentContainerHistory.getId(), HistoryFragment.class, bundle)
                .commit();
    }

    private void updateBinding() {
        if(focusInterval == null) { return; }
    }

    private void setupPreviousButton() {
    }

    private void setupNextButton() {
    }
}
