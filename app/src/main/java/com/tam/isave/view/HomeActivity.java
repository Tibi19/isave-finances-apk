package com.tam.isave.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;

import com.tam.isave.adapter.CategoryAdapter;
import com.tam.isave.databinding.ActivityHomeBinding;
import com.tam.isave.databinding.PopupAddCategoryBinding;
import com.tam.isave.viewmodel.CategoryViewModel;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class HomeActivity extends AppCompatActivity {

    private CategoryAdapter categoryAdapter;
    private CategoryViewModel categoryViewModel;
    private ActivityHomeBinding homeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        initCategoryController();
        setupCategoryRecycler();
        setupCategoryViewModel();
    }

    private void setupCategoryRecycler() {
        // Instantiate recyclerview and its adapter.
        RecyclerView categoryRecycler = homeBinding.recyclerCategory;
        categoryAdapter = new CategoryAdapter(this);
        // Set recycler's adapter.
        categoryRecycler.setAdapter(categoryAdapter);
        categoryRecycler.setAdapter(categoryAdapter);
        // Set recycler's layout manager.
        categoryRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupCategoryViewModel() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        // Observer
        categoryViewModel.getCategories().observe(this, categories ->
                categoryAdapter.setCategories(categories));
    }

    private void initCategoryController(){
        // Set functionality of menu button, current functionality is only for testing purposes.
        homeBinding.buttonHomeMenu.setOnClickListener(listener -> deleteAllCategories());
        // Functionality of add category button
        homeBinding.buttonAddCategory.setOnClickListener(listener -> showAddCategoryPopup());
    }

    private void deleteAllCategories() {
        categoryViewModel.deleteAll();
    }

//    private void addCategory() {
//        DebugUtils.makeToast(this, "added category");
//        Category category = getRandomCategory();
//        categoryViewModel.addCategory(category.getName(), category.getGoal(), category.isFlexibleGoal());
//    }

//    private Category getRandomCategory() {
//        int randomGoal = DebugUtils.getRandomIntInRange(150, 800);
//        Category category = new Category(DebugUtils.getRandomCategoryName(), randomGoal);
//        category.setSpent(DebugUtils.getRandomDouble(randomGoal));
//
//        return category;
//    }

    private void showAddCategoryPopup() {
        AlertDialog addCategoryDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        PopupAddCategoryBinding addCategoryBinding = PopupAddCategoryBinding.inflate(getLayoutInflater());

        builder.setView(addCategoryBinding.getRoot());
        addCategoryDialog = builder.create();
        addCategoryDialog.setCancelable(true);
        Window addCategoryWindow = addCategoryDialog.getWindow();
        addCategoryDialog.getWindow().setGravity(Gravity.BOTTOM);

        addCategoryBinding.buttonAddCategoryCancel.setOnClickListener(listener -> addCategoryDialog.dismiss());
        addCategoryBinding.buttonAddCategorySubmit.setOnClickListener(listener -> {
            addNewCategory(addCategoryBinding);
            addCategoryDialog.dismiss();
        });

        addCategoryDialog.show();
    }

    private void addNewCategory(PopupAddCategoryBinding addCategoryBinding) {
        boolean categoryFlexibility = addCategoryBinding.checkAddCategoryIsFlexible.isChecked();
        String categoryName = addCategoryBinding.editAddCategoryName.getText().toString();
        double categoryGoal = Double.parseDouble(addCategoryBinding.editAddCategoryBudget.getText().toString());
        categoryViewModel.addCategory(categoryName, categoryGoal, categoryFlexibility);
    }
}