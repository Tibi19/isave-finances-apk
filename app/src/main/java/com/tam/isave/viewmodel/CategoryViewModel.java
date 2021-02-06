package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tam.isave.data.DataRepository;
import com.tam.isave.model.CategoryTools.Category;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {

    private DataRepository dataRepository;
    private LiveData<List<Category>> categories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        dataRepository = new DataRepository(application);
        categories = dataRepository.getCategories();
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

}
