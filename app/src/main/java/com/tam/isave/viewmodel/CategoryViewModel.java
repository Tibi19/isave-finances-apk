package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.databinding.PopupAddCategoryBinding;
import com.tam.isave.databinding.PopupEditCategoryBinding;
import com.tam.isave.databinding.PopupEditPaymentBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.ModelRepository;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.CategoryUtils;
import com.tam.isave.utils.Date;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {

    private ModelRepository modelRepository;
    private LiveData<List<Category>> categories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        modelRepository = ModelRepository.getModelRepository(application);
        categories = modelRepository.getCategories();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void addCategory(PopupAddCategoryBinding addCategoryBinding) {
        boolean categoryFlexibility = addCategoryBinding.checkAddCategoryIsFlexible.isChecked();
        String categoryName = addCategoryBinding.editAddCategoryName.getText().toString();
        double categoryGoal = Double.parseDouble(addCategoryBinding.editAddCategoryBudget.getText().toString());
        modelRepository.newCategory(categoryName, categoryGoal, categoryFlexibility);
    }

    public void editCategory(Category category, PopupEditCategoryBinding editCategoryBinding) {
        String newCategoryName = editCategoryBinding.etEditCategoryName.getText().toString();

        String newCategorySpentString = editCategoryBinding.etEditCategorySpent.getText().toString();
        double newCategorySpent = newCategorySpentString.isEmpty() ? 0.0 : Double.parseDouble(newCategorySpentString);

        String newCategoryGoalString = editCategoryBinding.etEditCategoryBudget.getText().toString();
        double newCategoryGoal = newCategoryGoalString.isEmpty() ? 0.0 : Double.parseDouble(newCategoryGoalString);

        boolean newCategoryFlexibility = editCategoryBinding.checkEditCategoryIsFlexible.isChecked();

        modelRepository.modifyCategory(category, newCategoryName, newCategorySpent, newCategoryGoal, newCategoryFlexibility);
    }

    public void modifyCategory(Category category, String newName, double newSpent, double newGoal, boolean newHasFlexibleGoal) {
        modelRepository.modifyCategory(category, newName, newSpent, newGoal, newHasFlexibleGoal);
    }

    public void deleteCategory(Category category) {
        modelRepository.removeCategory(category);
    }

    public void resetCategory(Category category) {
        modelRepository.resetCategory(category);
    }

    public void resetAllCategories() {
        modelRepository.resetAllCategories();
    }

    public void deleteAll() {
        modelRepository.removeAllCategories();
    }

}
