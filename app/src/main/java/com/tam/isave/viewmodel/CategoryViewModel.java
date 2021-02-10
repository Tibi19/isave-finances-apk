package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.model.category.Category;
import com.tam.isave.model.ModelRepository;

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

    public void addCategory(String name, double goal, boolean hasFlexibleGoal) {
        modelRepository.newCategory(name, goal, hasFlexibleGoal);
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
