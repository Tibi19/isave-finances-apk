package com.tam.isave.view.fragment;

import android.os.Bundle;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.tam.isave.databinding.FragmentOrganizerBinding;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.DebugUtils;
import com.tam.isave.viewmodel.GoalOrganizerViewModel;

public class GoalOrganizerFragment extends Fragment {

    private FragmentOrganizerBinding binding;
    private GoalOrganizerViewModel organizerViewModel;

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
        updateBinding();
        setupOrganizerController();
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
        // TESTING
        Date firstDay = Date.today(); // Test with old days next.
        Date lastDay = firstDay.addDays(DebugUtils.getRandomIntInRange(15, 30));
        double globalGoal = DebugUtils.getRandomDoubleInRange(500, 5000);
        int intervalsCount = DebugUtils.getRandomIntInRange(2, 7);
        organizerViewModel.updateGoalOrganizer(globalGoal, intervalsCount, firstDay, lastDay);
        updateBinding();
    }

    private void deleteOrganizer() {
        organizerViewModel.deleteGoalOrganizer();
        updateBinding();
    }
}
