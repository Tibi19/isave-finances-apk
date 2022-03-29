package com.tam.isave.view.dialog;

import android.view.Gravity;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.tam.isave.R;
import com.tam.isave.databinding.PopupConfirmBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.transaction.Transaction;

public class ConfirmationBuilder {

    public static void showDeleteConfirmation(LayoutInflater inflater, Category categoryToDelete, Runnable deleteRunnable) {
        String objectDescription = ConfirmationText.OBJECT_DESCRIPTION_CATEGORY;
        String objectName = categoryToDelete.getName();
        String question = ConfirmationText.getDeleteQuestion(objectDescription, objectName);
        ConfirmationBuilder.showConfirmation(inflater, question, deleteRunnable);
    }

    public static void showDeleteConfirmation(LayoutInflater inflater, Transaction transactionToDelete, Runnable deleteRunnable) {
        String objectDescription = ConfirmationText.OBJECT_DESCRIPTION_TRANSACTION;
        String objectName = transactionToDelete.getName();
        String question = ConfirmationText.getDeleteQuestion(objectDescription, objectName);
        ConfirmationBuilder.showConfirmation(inflater, question, deleteRunnable);
    }

    public enum ResetConfirmationType {
        ORGANIZER(ConfirmationText.getResetOrganizerQuestion()),
        ALL_CATEGORIES(ConfirmationText.getResetAllCategoriesQuestion()),
        EVERYTHING(ConfirmationText.getResetEverythingQuestion()),
        EVERYTHING_BUT_ORGANIZER(ConfirmationText.getResetEverythingButOrganizerQuestion());

        private final String resetQuestion;

        ResetConfirmationType(final String resetQuestion) { this.resetQuestion = resetQuestion; }

        public String getResetQuestion() { return resetQuestion; }
    }

    public static void showResetConfirmation(LayoutInflater inflater, ResetConfirmationType resetConfirmationType, Runnable resetRunnable) {
        String question = resetConfirmationType.getResetQuestion();
        showConfirmation(inflater, question, resetRunnable);
    }

    public static void showResetConfirmation(LayoutInflater inflater, Category categoryToReset, Runnable resetRunnable) {
        String question = ConfirmationText.getResetCategoryQuestion(categoryToReset.getName());
        ConfirmationBuilder.showConfirmation(inflater, question, resetRunnable);
    }

    public enum PlanningConfirmationType {
        CATEGORIES_BUDGETS(ConfirmationText.getPlanCategoriesQuestion()),
        CATEGORIES_CASHING(ConfirmationText.getPlanCategoriesCashingQuestion()),
        CATEGORIES_AND_ORGANIZER_CASHING(ConfirmationText.getPlanCategoriesAndOrganizerCashingQuestion());

        private final String planningQuestion;

        PlanningConfirmationType(final String planningQuestion) { this.planningQuestion = planningQuestion; }

        public String getPlanningQuestion() { return planningQuestion; }
    }

    public static void showPlanningConfirmation(LayoutInflater inflater, PlanningConfirmationType planningConfirmationType, Runnable planningRunnable) {
        String question = planningConfirmationType.getPlanningQuestion();
        ConfirmationBuilder.showConfirmation(inflater, question, planningRunnable);
    }

    public static void showRestoreConfirmation(LayoutInflater inflater, Category categoryToRestore, Runnable restoreRunnable) {
        String question = ConfirmationText.getRestoreCategoryQuestion(categoryToRestore.getName(), categoryToRestore.getGoalModifier());
        ConfirmationBuilder.showConfirmation(inflater, question, restoreRunnable);
    }

    private static void showConfirmation(LayoutInflater inflater, String question, Runnable confirmRunnable) {
        AlertDialog confirmationDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
        PopupConfirmBinding confirmBinding = PopupConfirmBinding
                .inflate(inflater);

        builder.setView(confirmBinding.getRoot());
        confirmationDialog = builder.create();
        confirmationDialog.setCancelable(true);
        confirmationDialog.getWindow().setGravity(Gravity.BOTTOM);
        confirmationDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        confirmBinding.tvConfirmQuestion.setText(question);

        confirmBinding.buttonConfirmNo.setOnClickListener(listener -> confirmationDialog.dismiss());
        confirmBinding.buttonConfirmYes.setOnClickListener(listener -> {
            confirmRunnable.run();
            confirmationDialog.dismiss();
        });

        confirmationDialog.show();
    }

}
