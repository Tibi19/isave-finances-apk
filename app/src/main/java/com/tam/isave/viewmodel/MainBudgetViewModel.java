package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.tam.isave.databinding.PopupEditBudgetBinding;
import com.tam.isave.model.MainBudget;
import com.tam.isave.model.ModelRepository;

public class MainBudgetViewModel extends AndroidViewModel {

    private ModelRepository modelRepository;

    public MainBudgetViewModel(@NonNull Application application) {
        super(application);
        modelRepository = ModelRepository.getModelRepository(application);
    }

    public void modifyMainBudget(PopupEditBudgetBinding editBudgetBinding) {
        String newBudgetString = editBudgetBinding.etEditBudget.getText().toString();
        String newSpentString = editBudgetBinding.etEditBudgetSpent.getText().toString();
        boolean newIsHidden = editBudgetBinding.checkEditBudgetIsHidden.isChecked();

        double newBudget = Double.parseDouble(newBudgetString);
        double newSpent = Double.parseDouble(newSpentString);

        modelRepository.modifyMainBudget(newBudget, newSpent, newIsHidden);
    }

    public void addCashing(double cashingValue) {
        modelRepository.addCashing(cashingValue);
    }

    public MainBudget getMainBudget() {
        return modelRepository.getMainBudget();
    }

}
