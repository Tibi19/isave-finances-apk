package com.tam.isave.view.fragment;

import android.app.Dialog;
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
import com.tam.isave.databinding.PopupEditCategoryBinding;
import com.tam.isave.databinding.PopupMoveBudgetBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.CategoryUtils;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.NumberUtils;
import com.tam.isave.view.dialog.PlannerBuilder;
import com.tam.isave.view.dialog.CategorySpinnerPicker;
import com.tam.isave.view.dialog.ConfirmationBuilder;
import com.tam.isave.viewmodel.CategoryViewModel;

import java.util.List;

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
        // Give adapter the method for deleting, editing, resetting Category data.
        categoryAdapter.setDeleteItemData(this::showDeleteCategoryPopup); // instead of (category) -> showDeleteCategoryPopup(category)
        categoryAdapter.setEditItemData(this::showEditCategoryPopup);
        categoryAdapter.setResetItemData(this::showResetCategoryPopup);
        // Set recycler's adapter.
        categoryRecycler.setAdapter(categoryAdapter);
        // Set recycler's layout manager.
        categoryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showDeleteCategoryPopup(Category category) {
        Runnable deleteRunnable = () -> categoryViewModel.deleteCategory(category);
        ConfirmationBuilder.showDeleteConfirmation(getLayoutInflater(), category, deleteRunnable);
    }

    private void showResetCategoryPopup(Category category) {
        Runnable resetRunnable = () -> categoryViewModel.resetCategory(category);
        ConfirmationBuilder.showResetConfirmation(getLayoutInflater(), category, resetRunnable);
    }

    private void showEditCategoryPopup(Category category) {
        AlertDialog editCategoryDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        PopupEditCategoryBinding editCategoryBinding = PopupEditCategoryBinding.inflate(getLayoutInflater());
        double absCategorySpent = Math.abs( NumberUtils.twoDecimalsRounded(category.getSpent()) );
        double absCategoryGoal = Math.abs( NumberUtils.twoDecimalsRounded(category.getGoal()) );

        builder.setView(editCategoryBinding.getRoot());
        editCategoryDialog = builder.create();
        editCategoryDialog.setCancelable(true);
        editCategoryDialog.getWindow().setGravity(Gravity.BOTTOM);

        editCategoryBinding.etEditCategoryName.setText(category.getName());
        editCategoryBinding.etEditCategorySpent.setText(String.valueOf(absCategorySpent));
        editCategoryBinding.etEditCategoryBudget.setText(String.valueOf(absCategoryGoal));
        editCategoryBinding.checkEditCategoryIsFlexible.setChecked(category.isFlexibleGoal());
        editCategoryBinding.buttonEditCategoryCancel.setOnClickListener(listener -> editCategoryDialog.dismiss());
        editCategoryBinding.buttonEditCategorySubmit.setOnClickListener(listener -> {
            if(!categoryViewModel.isEditCategoryValid(editCategoryBinding, getContext(), category)) { return; }
            categoryViewModel.editCategory(category, editCategoryBinding);
            editCategoryDialog.dismiss();
        });
        editCategoryBinding.buttonMoveBudget.setOnClickListener(listener -> {
            if( !categoryViewModel.canMoveBudget(getContext()) ) { return; }
            showMoveBudgetPopup(category, editCategoryDialog);
        });

        editCategoryDialog.show();
    }

    private void showMoveBudgetPopup(Category category, Dialog editDialog) {
        AlertDialog moveBudgetDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        PopupMoveBudgetBinding moveBudgetBinding = PopupMoveBudgetBinding.inflate(getLayoutInflater());

        String fromCategoryText = Constants.NAMING_FROM_CATEGORY + category.getName();
        double remainingBudget = category.getLeftAmountTwoDecimals();
        String remainingBudgetText = String.valueOf(remainingBudget);
        List<Category> categories = categoryViewModel.getCategories().getValue();

        builder.setView(moveBudgetBinding.getRoot());
        moveBudgetDialog = builder.create();
        moveBudgetDialog.setCancelable(true);
        moveBudgetDialog.getWindow().setGravity(Gravity.BOTTOM);

        List<Category> otherCategories = CategoryUtils.getCategoriesExcept(categories, category);
        CategorySpinnerPicker.build(getActivity(), moveBudgetBinding.spinToCategory, otherCategories, false);

        moveBudgetBinding.tvFromCategory.setText(fromCategoryText);
        moveBudgetBinding.etMoveBudgetValue.setText(remainingBudgetText);
        moveBudgetBinding.buttonMoveCancel.setOnClickListener(listener -> moveBudgetDialog.dismiss());
        moveBudgetBinding.buttonMoveSubmit.setOnClickListener(Listener -> {
            if( !categoryViewModel.isMoveBudgetValid(moveBudgetBinding, getContext()) ) { return; }
            categoryViewModel.moveBudget(category, moveBudgetBinding);
            editDialog.dismiss();
            moveBudgetDialog.dismiss();
        });

        moveBudgetDialog.show();
    }

    private void setupCategoriesController(){
        binding.buttonAddCategory.setOnClickListener(listener -> showAddCategoryPopup());
        binding.buttonResetProgress.setOnClickListener(listener -> showResetCategoriesPopup());
        binding.buttonPlan.setOnClickListener(listener -> PlannerBuilder.showPlannerPopup(
                getActivity(), getLayoutInflater(), this, 0.0
        ));
    }

    private void showResetCategoriesPopup() {
        Runnable resetCategoriesRunnable = () -> categoryViewModel.resetAllCategories();
        ConfirmationBuilder.showResetConfirmation(
                getLayoutInflater(),
                ConfirmationBuilder.ResetConfirmationType.ALL_CATEGORIES,
                resetCategoriesRunnable
        );
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
            if( !categoryViewModel.isAddCategoryValid(addCategoryBinding, getContext()) ) { return; }
            categoryViewModel.addCategory(addCategoryBinding);
            addCategoryDialog.dismiss();
        });

        addCategoryDialog.show();
    }
}
