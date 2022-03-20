package com.tam.isave.model.category;

import com.tam.isave.model.category.Category;
import com.tam.isave.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static List<Category> getCategoriesExcept(List<Category> categories, Category categoryToOmit) {
        if(categories == null || categoryToOmit == null) { return null; }

        ArrayList<Category> otherCategories = new ArrayList<>(categories);
        otherCategories.remove(categoryToOmit);

        return otherCategories;
    }

    public static HashMap<Integer, String> getCategoriesIdToNameMap(List<Category> categories) {
        if(categories == null) { return null; }

        HashMap<Integer, String> categoriesIdToNameMap = new HashMap<>();

        categories.forEach( category -> categoriesIdToNameMap.put(category.getId(), category.getName()) );

        return categoriesIdToNameMap;
    }
}
