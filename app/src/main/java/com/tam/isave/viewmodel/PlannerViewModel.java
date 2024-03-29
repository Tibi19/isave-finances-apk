package com.tam.isave.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.adapter.PlannerAdapter;
import com.tam.isave.databinding.PopupCashingBinding;
import com.tam.isave.model.ModelRepository;
import com.tam.isave.model.category.Category;
import com.tam.isave.utils.LiveDataUtils;
import com.tam.isave.view.dialog.ErrorBuilder;

import java.util.List;

public class PlannerViewModel extends AndroidViewModel {

    private ModelRepository modelRepository;
    private LiveData<List<Category>> categoriesLiveData;
    private PlannerAdapter plannerAdapter;

    public PlannerViewModel(@NonNull Application application) {
        super(application);
        modelRepository = ModelRepository.getModelRepository(application);
        categoriesLiveData = modelRepository.getCategories();
    }

    public void updateCategoriesNewBudgets(boolean addToExistingBudget, boolean shouldResetCategories) {
        LiveDataUtils.observeOnce(
                categoriesLiveData,
                categories -> {
                    if(shouldResetCategories) { modelRepository.resetAllCategories(); }

                    for(Category category : categories) {
                        String categoryName = category.getName();
                        double newBudget = plannerAdapter.getCategoryPlannedBudget(categoryName);
                        if(newBudget < 0.0) { continue; }
                        if(addToExistingBudget && !shouldResetCategories) { newBudget += category.getGoal(); }
                        modelRepository.modifyCategory(category, categoryName, category.getSpent(), newBudget, category.isFlexibleGoal());
                    }
                }
        );
    }

    public boolean isCashingValid(PopupCashingBinding cashingPopupBinding, Context context) {
        String cashingString = cashingPopupBinding.etCashing.getText().toString();

        if(cashingString.isEmpty()) {
            ErrorBuilder.missingValue(context);
            return false;
        }

        return true;
    }

    public void updateCategoriesNewBudgets() {
        updateCategoriesNewBudgets(false, false);
    }

    public List<Category> getCategories() { return categoriesLiveData.getValue(); }

    public void setPlannerAdapter(PlannerAdapter plannerAdapter) { this.plannerAdapter = plannerAdapter; }

}
