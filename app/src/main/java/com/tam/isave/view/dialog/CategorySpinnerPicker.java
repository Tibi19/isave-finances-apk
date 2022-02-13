package com.tam.isave.view.dialog;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tam.isave.model.category.Category;
import com.tam.isave.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CategorySpinnerPicker  {

    public static void build(Context context, Spinner spinner, List<Category> categories) {
        new CategorySpinnerPicker(context, spinner, categories, true);
    }
    public static void build(Context context, Spinner spinner, List<Category> categories, Category defaultCategory) {
        new CategorySpinnerPicker(context, spinner, categories, defaultCategory);
    }
    public static void build(Context context, Spinner spinner, List<Category> categories, boolean includeNoCategoryOption) {
        new CategorySpinnerPicker(context, spinner, categories, includeNoCategoryOption);
    }

    public CategorySpinnerPicker(Context context, Spinner spinner, List<Category> categories, boolean includeNoCategoryOption) {
        List<String> categoriesNames = new ArrayList<>();
        for (Category category : categories) {
            categoriesNames.add(category.getName());
        }

        if(includeNoCategoryOption) {
            categoriesNames.add(Constants.NAMING_NO_CATEGORY);
        }

        int standardSpinnerLayout = android.R.layout.simple_spinner_dropdown_item;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, standardSpinnerLayout, categoriesNames);
        adapter.setDropDownViewResource(standardSpinnerLayout);
        spinner.setAdapter(adapter);
    }

    public CategorySpinnerPicker(Context context, Spinner spinner, List<Category> categories, Category defaultCategory) {
        this(context, spinner, categories, true);

        if(defaultCategory == null) {
            // The element at the 'size' position is the 'no category' element.
            spinner.setSelection(categories.size());
            return;
        }

        for(int i = 0; i < categories.size(); i++) {
            if(categories.get(i).getId() == defaultCategory.getId()) {
                spinner.setSelection(i); // i is the position of the default category.
                break;
            }
        }

    }

}
