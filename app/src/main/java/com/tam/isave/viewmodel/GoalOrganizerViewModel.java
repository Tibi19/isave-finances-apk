package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.model.ModelRepository;
import com.tam.isave.model.goalorganizer.GoalOrganizer;
import com.tam.isave.model.goalorganizer.Interval;
import com.tam.isave.utils.Date;

import java.util.List;

public class GoalOrganizerViewModel extends AndroidViewModel {

    private ModelRepository modelRepository;
    private LiveData<List<Interval>> intervals;

    public GoalOrganizerViewModel(@NonNull Application application) {
        super(application);
        modelRepository = ModelRepository.getModelRepository(application);
        intervals = modelRepository.getIntervals();
    }

    public int getGoalOrganizerFirstDayValue() {
        Date goalOrganizerFirstDay = modelRepository.getGoalOrganizer().getFirstDay();
        if(goalOrganizerFirstDay == null) { return -1; }
        return goalOrganizerFirstDay.getValue();
    }

    public int getGoalOrganizerDays() {
        return modelRepository.getGoalOrganizer().getGlobalIntervalDays();
    }

    public LiveData<List<Interval>> getIntervals() {
        return intervals;
    }

}
