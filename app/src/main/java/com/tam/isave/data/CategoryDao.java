package com.tam.isave.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.tam.isave.model.category.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insert(Category category);

    @Update
    void update(Category... categories);

    @Delete
    void delete(Category category);

    @Query("DELETE FROM category_table")
    void deleteAll();

    @Query("SELECT * FROM category_table")
    LiveData<List<Category>> getCategories();

}
