package com.tam.isave.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.databinding.PopupAddCategoryBinding;
import com.tam.isave.databinding.PopupEditCategoryBinding;
import com.tam.isave.databinding.PopupMoveBudgetBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.ModelRepository;
import com.tam.isave.model.category.CategoryUtils;
import com.tam.isave.view.dialog.ErrorBuilder;

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

    public void setupTrackerCategories(List<Category> categories) {
        modelRepository.setupTrackerCategories(categories);
    }

    public void addCategory(PopupAddCategoryBinding addCategoryBinding) {
        boolean categoryFlexibility = addCategoryBinding.checkAddCategoryIsFlexible.isChecked();
        String categoryName = addCategoryBinding.editAddCategoryName.getText().toString();
        double categoryGoal = Double.parseDouble(addCategoryBinding.editAddCategoryBudget.getText().toString());
        modelRepository.newCategory(categoryName, categoryGoal, categoryFlexibility);
    }

    public boolean isAddCategoryValid(PopupAddCategoryBinding addCategoryBinding, Context context) {
        String name = addCategoryBinding.editAddCategoryName.getText().toString();
        String budgetString = addCategoryBinding.editAddCategoryBudget.getText().toString();

        if(name.isEmpty() || budgetString.isEmpty()) {
            ErrorBuilder.missingValue(context);
            return false;
        }

        if(CategoryUtils.getCategoryByName(categories.getValue(), name) != null) {
            ErrorBuilder.categoryNameExists(context);
            return false;
        }

        return true;
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

    public boolean isEditCategoryValid(PopupEditCategoryBinding editCategoryBinding, Context context, Category categoryToEdit) {
        String name = editCategoryBinding.etEditCategoryName.getText().toString();
        String budgetString = editCategoryBinding.etEditCategoryBudget.getText().toString();
        String spentString = editCategoryBinding.etEditCategorySpent.getText().toString();

        if(name.isEmpty() || budgetString.isEmpty() || spentString.isEmpty()) {
            ErrorBuilder.missingValue(context);
            return false;
        }

        Category searchedCategoryByName = CategoryUtils.getCategoryByName(categories.getValue(), name);
        // If user didn't modify the name, then the searched category by name will be the category to be edited.
        // This is valid, so throw an error only if it's a different category that has the inserted name.
        if( (searchedCategoryByName != null) && (searchedCategoryByName.getId() != categoryToEdit.getId()) ) {
            ErrorBuilder.categoryNameExists(context);
            return false;
        }

        return true;
    }

    public void moveBudget(Category fromCategory, PopupMoveBudgetBinding moveBudgetBinding) {
        String toCategoryName = moveBudgetBinding.spinToCategory.getSelectedItem().toString();
        Category toCategory = CategoryUtils.getCategoryByName(categories.getValue(), toCategoryName);

        if(toCategory == null || fromCategory.getId() == toCategory.getId()) { return; }

        String budgetToMoveString = moveBudgetBinding.etMoveBudgetValue.getText().toString();
        double budgetToMove = Double.parseDouble(budgetToMoveString);

        double fromCategoryNewGoal = fromCategory.getGoal() - budgetToMove;
        double toCategoryNewGoal = toCategory.getGoal() + budgetToMove;

        modelRepository.modifyCategory(fromCategory, fromCategory.getName(), fromCategory.getSpent(), fromCategoryNewGoal, fromCategory.isFlexibleGoal());
        modelRepository.modifyCategory(toCategory, toCategory.getName(), toCategory.getSpent(), toCategoryNewGoal, toCategory.isFlexibleGoal());
    }

    public boolean isMoveBudgetValid(PopupMoveBudgetBinding moveBudgetBinding, Context context) {
        String moveValueString = moveBudgetBinding.etMoveBudgetValue.getText().toString();

        if(moveValueString.isEmpty()) {
            ErrorBuilder.missingValue(context);
            return false;
        }

        return true;
    }

    public boolean canMoveBudget(Context context) {
        // Can only move budget if there are at least 2 categories (i.e. move budget from a category to another).
        if(categories.getValue().size() < 2) {
            ErrorBuilder.missingOtherCategories(context);
            return false;
        }

        return true;
    }

    public void deleteCategory(Category category) {
        modelRepository.removeCategory(category);
    }

    public void resetCategory(Category category) {
        modelRepository.resetCategory(category);
    }

    public void restoreCategory(Category category) { modelRepository.restoreCategory(category); }

    public void resetAllCategories() {
        modelRepository.resetAllCategories();
    }

    public void deleteAll() {
        modelRepository.removeAllCategories();
    }

}
