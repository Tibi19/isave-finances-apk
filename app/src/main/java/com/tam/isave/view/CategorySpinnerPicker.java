package com.tam.isave.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tam.isave.model.category.Category;

import java.util.ArrayList;
import java.util.List;

public class CategorySpinnerPicker  {

    public static void build(Context context, Spinner spinner, List<Category> categories) {
        new CategorySpinnerPicker(context, spinner, categories);
    }
    public static void build(Context context, Spinner spinner, List<Category> categories, Category defaultCategory) {
        new CategorySpinnerPicker(context, spinner, categories, defaultCategory);
    }

    public CategorySpinnerPicker(Context context, Spinner spinner, List<Category> categories) {
        List<String> categoriesNames = new ArrayList<>();
        for (Category category : categories) {
            categoriesNames.add(category.getName());
        }

        int standardSpinnerLayout = android.R.layout.simple_spinner_dropdown_item;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, standardSpinnerLayout, categoriesNames);
        adapter.setDropDownViewResource(standardSpinnerLayout);
        spinner.setAdapter(adapter);
    }

    public CategorySpinnerPicker(Context context, Spinner spinner, List<Category> categories, Category defaultCategory) {
        this(context, spinner, categories);

        if(defaultCategory == null) { return; }

        for(int i = 0; i < categories.size(); i++) {
            if(categories.get(i).getId() == defaultCategory.getId()) {
                spinner.setSelection(i); // i is the position of the default category.
                break;
            }
        }

    }

}
