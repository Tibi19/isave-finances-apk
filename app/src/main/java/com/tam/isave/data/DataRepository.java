package com.tam.isave.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.tam.isave.model.category.Category;

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

    public void updateAllCategories(final List<Category> categories) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                // A temporary category array;
                // To be used for converting categories list into an array.
                Category[] tempCategoryArray = new Category[categories.size()];
                // Convert categories list to an array and pass to dao update.
                categoryDao.update(categories.toArray(tempCategoryArray));
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

    public void deleteAllCategories() {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.deleteAll();
            }
        });
    }

}
