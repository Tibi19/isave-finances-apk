package com.tam.isave.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.tam.isave.model.CategoryTools.Category;

import java.util.List;

public class DataRepository {

    private CategoryDao categoryDao;
    private LiveData<List<Category>> categories;

    public DataRepository(Application application) {
        CategoryRoomDatabase database = CategoryRoomDatabase.getDatabase(application);
        categoryDao = database.categoryDao();
        categories = categoryDao.getCategories();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void insertCategory(final Category category) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.insert(category);
            }
        });
    }

    public void updateCategory(final Category category) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.update(category);
            }
        });
    }

    public void deleteCategory(final Category category) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.delete(category);
            }
        });
    }

}
