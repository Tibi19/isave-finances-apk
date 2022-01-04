package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.tam.isave.adapter.PlannerAdapter;
import com.tam.isave.model.ModelRepository;
import com.tam.isave.model.category.Category;

import java.util.List;

public class PlannerViewModel extends AndroidViewModel {

    private ModelRepository modelRepository;
    private List<Category> categories;
    private PlannerAdapter plannerAdapter;

    public PlannerViewModel(@NonNull Application application) {
        super(application);
        modelRepository = ModelRepository.getModelRepository(application);
        categories = modelRepository.getCategories().getValue();
    }

    public void updateCategoriesNewBudgets(boolean addToExistingBudget) {
        for(Category category : categories) {
            String categoryName = category.getName();
            double newBudget = plannerAdapter.getCategoryPlannedBudget(categoryName);
            if(newBudget < 0.0) { continue; }
            if(addToExistingBudget) { newBudget += category.getGoal(); }
            modelRepository.modifyCategory(category, categoryName, category.getSpent(), newBudget, category.isFlexibleGoal());
        }
    }

    public void updateCategoriesNewBudgets() {
        updateCategoriesNewBudgets(false);
    }

    public List<Category> getCategories() { return categories; }

    public void setPlannerAdapter(PlannerAdapter plannerAdapter) { this.plannerAdapter = plannerAdapter; }

}
