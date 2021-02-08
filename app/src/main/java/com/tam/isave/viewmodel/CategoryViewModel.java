package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.data.DataRepository;
import com.tam.isave.model.CategoryTools.Category;
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

    public void insert(Category category) {
        dataRepository.insertCategory(category);
    }

    public void update(Category category) {
        dataRepository.updateCategory(category);
    }

    public void delete(Category category) {
        dataRepository.deleteCategory(category);
    }

    public void deleteAll() {
        dataRepository.deleteAllCategories();
    }

}
