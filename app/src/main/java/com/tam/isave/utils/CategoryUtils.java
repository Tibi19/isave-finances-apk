package com.tam.isave.utils;

import com.tam.isave.model.category.Category;

import java.util.List;

public class CategoryUtils {

    public static Category getCategoryByName(List<Category> categories, String categoryName) {
        if (categories == null || categoryName.isEmpty()) { return null; }

        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null;
    }

    public static Category getCategoryById(List<Category> categories, int categoryId) {
        if(categories == null || categoryId == -1) { return null; }

        for(Category category : categories) {
            if(category.getId() == categoryId) {
                return category;
            }
        }

        return null;
    }

}
