package com.tam.isave.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.tam.isave.adapter.PlannerAdapter;
import com.tam.isave.databinding.PopupCashingBinding;
import com.tam.isave.databinding.PopupPlannerBinding;
import com.tam.isave.utils.EditTextUtils;
import com.tam.isave.utils.NumberUtils;
import com.tam.isave.utils.OrganizerBindingUtils;
import com.tam.isave.viewmodel.GoalOrganizerViewModel;
import com.tam.isave.viewmodel.MainBudgetViewModel;
import com.tam.isave.viewmodel.PlannerViewModel;

public class PlannerBuilder {

    public static void showCashingPlannerPopup(Activity activity, LayoutInflater inflater, ViewModelStoreOwner owner, Runnable onSubmit) {
        PopupCashingBinding cashingPopupBinding = PopupCashingBinding.inflate(inflater);
        AlertDialog cashingPopupDialog = setupPopupDialog(activity, cashingPopupBinding);
        PlannerViewModel plannerViewModel = new ViewModelProvider(owner).get(PlannerViewModel.class);
        PlannerAdapter plannerAdapter = setupPlannerAdapter(cashingPopupBinding.recyclerCashingPlanCategories, plannerViewModel, inflater.getContext());

        plannerViewModel.setPlannerAdapter(plannerAdapter);
        setupRemainingAmountCounter(
                cashingPopupBinding.tvRemainingAmount,
                cashingPopupBinding.etCashing,
                plannerAdapter
        );

        cashingPopupBinding.btnCashingCancel.setOnClickListener(listener -> cashingPopupDialog.dismiss());
        cashingPopupBinding.btnCashingSubmit.setOnClickListener(listener -> onCashingSubmit(
                cashingPopupBinding, cashingPopupDialog, plannerViewModel, inflater, owner, onSubmit
        ));

        cashingPopupDialog.show();
    }

    public static void onCashingSubmit(
            PopupCashingBinding cashingBinding,
            AlertDialog cashingDialog,
            PlannerViewModel plannerViewModel,
            LayoutInflater inflater,
            ViewModelStoreOwner owner,
            Runnable onSubmit
    ) {
        boolean shouldAddToOrganizer = cashingBinding.checkModifyOrganizer.isChecked();
        boolean shouldResetEverything = cashingBinding.checkResetEverything.isChecked();
        Runnable resolveCashingRunnable = () ->
                resolveCashing(cashingBinding, plannerViewModel, cashingDialog,
                        owner, onSubmit, shouldAddToOrganizer, shouldResetEverything);

        if(shouldResetEverything) {
            ConfirmationBuilder.ResetConfirmationType confirmationType = shouldAddToOrganizer ?
                    ConfirmationBuilder.ResetConfirmationType.EVERYTHING :
                    ConfirmationBuilder.ResetConfirmationType.EVERYTHING_BUT_ORGANIZER;
            ConfirmationBuilder.showResetConfirmation(inflater, confirmationType, resolveCashingRunnable);
        } else {
            ConfirmationBuilder.PlanningConfirmationType confirmationType = shouldAddToOrganizer ?
                    ConfirmationBuilder.PlanningConfirmationType.CATEGORIES_AND_ORGANIZER_CASHING :
                    ConfirmationBuilder.PlanningConfirmationType.CATEGORIES_CASHING;
            ConfirmationBuilder.showPlanningConfirmation(inflater, confirmationType, resolveCashingRunnable);
        }
    }

    public static void resolveCashing(
            PopupCashingBinding cashingBinding,
            PlannerViewModel plannerViewModel,
            AlertDialog cashingDialog,
            ViewModelStoreOwner owner,
            Runnable onSubmit,
            boolean shouldAddToOrganizer,
            boolean shouldResetEverything
    ) {
        MainBudgetViewModel mainBudgetViewModel = new ViewModelProvider(owner).get(MainBudgetViewModel.class);
        GoalOrganizerViewModel organizerViewModel = new ViewModelProvider(owner).get(GoalOrganizerViewModel.class);
        String cashingValueString = cashingBinding.etCashing.getText().toString();
        double cashingValue = cashingValueString.isEmpty() ? -1.0 : Double.parseDouble(cashingValueString);

        plannerViewModel.updateCategoriesNewBudgets(!shouldResetEverything);
        mainBudgetViewModel.addCashing(cashingValue, shouldResetEverything);
        if(shouldAddToOrganizer) {
            organizerViewModel.addCashing(cashingValue, shouldResetEverything);
            OrganizerBindingUtils.updateBinding();
        }

        onSubmit.run();
        cashingDialog.dismiss();
    }

    public static void showPlannerPopup(Activity activity, LayoutInflater inflater, ViewModelStoreOwner owner, double startingBudget) {
        PopupPlannerBinding plannerPopupBinding = PopupPlannerBinding.inflate(inflater);
        AlertDialog plannerPopupDialog = setupPopupDialog(activity, plannerPopupBinding);
        PlannerViewModel plannerViewModel = new ViewModelProvider(owner).get(PlannerViewModel.class);
        PlannerAdapter plannerAdapter = setupPlannerAdapter(plannerPopupBinding.recyclerPlanCategories, plannerViewModel, inflater.getContext());

        plannerPopupBinding.etPlanBudget.setText(String.valueOf(startingBudget));
        plannerViewModel.setPlannerAdapter(plannerAdapter);
        setupRemainingAmountCounter(
                plannerPopupBinding.tvRemainingAmount,
                plannerPopupBinding.etPlanBudget,
                plannerAdapter
        );

        plannerPopupBinding.btnPlanCancel.setOnClickListener(listener -> plannerPopupDialog.dismiss());
        plannerPopupBinding.btnPlanSubmit.setOnClickListener(listener -> {
            ConfirmationBuilder.showPlanningConfirmation(
                    inflater,
                    ConfirmationBuilder.PlanningConfirmationType.CATEGORIES_BUDGETS,
                    plannerViewModel::updateCategoriesNewBudgets
            );
            plannerPopupDialog.dismiss();
        });

        plannerPopupDialog.show();
    }

    private static void setupRemainingAmountCounter(TextView tvRemainingAmount, EditText etBudget, PlannerAdapter plannerAdapter) {
        updateRemainingAmountCounter(tvRemainingAmount, etBudget, plannerAdapter.getTotalOfPlannedBudgets());

        plannerAdapter.setRemainingAmountUpdater(
                totalOfPlannedBudgets -> updateRemainingAmountCounter(tvRemainingAmount, etBudget, totalOfPlannedBudgets)
        );

        EditTextUtils.setOnTextChangedListener(
                etBudget,
                () -> updateRemainingAmountCounter(tvRemainingAmount, etBudget, plannerAdapter.getTotalOfPlannedBudgets())
        );
    }

    private static void updateRemainingAmountCounter(TextView tvRemainingAmount, EditText etBudget, double plannedAmount) {
        String availableBudgetString = etBudget.getText().toString();
        double availableBudget = availableBudgetString.isEmpty() ? 0.0 : Double.parseDouble(availableBudgetString);
        double remainingAmount = NumberUtils.twoDecimalsRounded(availableBudget - plannedAmount);
        String remainingAmountString = String.valueOf(remainingAmount);
        tvRemainingAmount.setText(remainingAmountString);
    }

    private static AlertDialog setupPopupDialog(Activity activity, ViewBinding binding) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(binding.getRoot());
        AlertDialog plannerPopupDialog = builder.create();
        plannerPopupDialog.setCancelable(true);
        plannerPopupDialog.getWindow().setGravity(Gravity.BOTTOM);

        return plannerPopupDialog;
    }

    private static PlannerAdapter setupPlannerAdapter(RecyclerView plannerRecycler, PlannerViewModel plannerViewModel, Context context) {
        PlannerAdapter plannerAdapter = new PlannerAdapter(plannerViewModel.getCategories());
        plannerRecycler.setAdapter(plannerAdapter);
        plannerRecycler.setLayoutManager(new LinearLayoutManager(context));

        return plannerAdapter;
    }

}
