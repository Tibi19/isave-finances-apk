package com.tam.isave.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tam.isave.adapter.CategoryAdapter;
import com.tam.isave.databinding.FragmentCategoriesBinding;
import com.tam.isave.databinding.PopupAddCategoryBinding;
import com.tam.isave.viewmodel.CategoryViewModel;
import com.tam.isave.R;

public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;
    private CategoryAdapter categoryAdapter;
    private CategoryViewModel categoryViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        setupCategoriesController();
        setupCategoryRecycler();
        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories ->
                categoryAdapter.setCategories(categories));
    }

    private void setupCategoryRecycler() {
        // Instantiate recyclerview and its adapter.
        RecyclerView categoryRecycler = binding.recyclerCategory;
        categoryAdapter = new CategoryAdapter(getContext());
        // Give adapter the method for deleting Category data.
        categoryAdapter.setDeleteItemData( (category) -> categoryViewModel.deleteCategory(category) );
        // Set recycler's adapter.
        categoryRecycler.setAdapter(categoryAdapter);
        // Set recycler's layout manager.
        categoryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupCategoriesController(){
        // Functionality of add category button
        binding.buttonAddCategory.setOnClickListener(listener -> showAddCategoryPopup());
    }

    private void showAddCategoryPopup() {
        AlertDialog addCategoryDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        PopupAddCategoryBinding addCategoryBinding = PopupAddCategoryBinding.inflate(getLayoutInflater());

        builder.setView(addCategoryBinding.getRoot());
        addCategoryDialog = builder.create();
        addCategoryDialog.setCancelable(true);
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
