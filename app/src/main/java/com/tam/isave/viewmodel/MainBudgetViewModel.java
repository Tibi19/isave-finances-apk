package com.tam.isave.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.tam.isave.databinding.PopupCashingBinding;
import com.tam.isave.databinding.PopupEditBudgetBinding;
import com.tam.isave.model.MainBudget;
import com.tam.isave.model.ModelRepository;
import com.tam.isave.utils.NumberUtils;
import com.tam.isave.view.dialog.ErrorBuilder;

public class MainBudgetViewModel extends AndroidViewModel {

    private ModelRepository modelRepository;

    public MainBudgetViewModel(@NonNull Application application) {
        super(application);
        modelRepository = ModelRepository.getModelRepository(application);
    }

    public boolean isModifyMainBudgetValid(PopupEditBudgetBinding editBudgetBinding, Context context) {
        String budgetString = editBudgetBinding.etEditBudget.getText().toString();
        String spentString = editBudgetBinding.etEditBudgetSpent.getText().toString();

        if(budgetString.isEmpty() || spentString.isEmpty()) {
            ErrorBuilder.missingValue(context);
            return false;
        }

        return true;
    }

    public void modifyMainBudget(PopupEditBudgetBinding editBudgetBinding) {
        String newBudgetString = editBudgetBinding.etEditBudget.getText().toString();
        String newSpentString = editBudgetBinding.etEditBudgetSpent.getText().toString();
        boolean newIsHidden = editBudgetBinding.checkEditBudgetIsHidden.isChecked();

        double newBudget = Double.parseDouble(newBudgetString);
        double newSpent = Double.parseDouble(newSpentString);

        modelRepository.modifyMainBudget(newBudget, newSpent, newIsHidden);
    }

    public void addCashing(double cashingValue, boolean shouldReset) {
        if( cashingValue < -NumberUtils.ZERO_DOUBLE ) { return; }
        modelRepository.addCashing(cashingValue, shouldReset);
    }

    public MainBudget getMainBudget() {
        return modelRepository.getMainBudget();
    }

}
