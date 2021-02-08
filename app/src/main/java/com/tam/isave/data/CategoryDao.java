package com.tam.isave.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.tam.isave.model.CategoryTools.Category;
import com.tam.isave.utils.Constants;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM category_table")
    LiveData<List<Category>> getCategories();

    @Query("DELETE FROM category_table")
    void deleteAll();

}
