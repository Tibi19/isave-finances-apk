package com.tam.isave.view.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewbinding.ViewBinding;

import com.tam.isave.databinding.PopupEditBudgetBinding;
import com.tam.isave.model.MainBudget;
import com.tam.isave.utils.EditTextUtils;
import com.tam.isave.utils.NumberUtils;
import com.tam.isave.viewmodel.MainBudgetViewModel;

import org.w3c.dom.Text;

public class EditMainBudgetBuilder {

    public static void showPopup(Activity activity, LayoutInflater inflater, ViewModelStoreOwner owner, Runnable onSubmit) {
        MainBudgetViewModel mainBudgetViewModel = new ViewModelProvider(owner).get(MainBudgetViewModel.class);
        PopupEditBudgetBinding editBudgetBinding = PopupEditBudgetBinding.inflate(inflater);
        AlertDialog editBudgetDialog = setupPopupDialog(activity, editBudgetBinding);

        initializeEditBudgetPopup(editBudgetBinding, mainBudgetViewModel);
        setupEditTextListener(editBudgetBinding);

        editBudgetBinding.buttonEditBudgetCancel.setOnClickListener(listener -> editBudgetDialog.dismiss());
        editBudgetBinding.buttonEditBudgetSubmit.setOnClickListener(listener -> {
            mainBudgetViewModel.modifyMainBudget(editBudgetBinding);
            onSubmit.run();
            editBudgetDialog.dismiss();
        });
        editBudgetBinding.buttonEditBudgetCashing.setOnClickListener(
                listener -> PlannerBuilder.showCashingPlannerPopup(activity, inflater, owner, onSubmit)
        );
        editBudgetBinding.buttonEditBudgetReset.setOnClickListener(
                listener -> editBudgetBinding.etEditBudgetSpent.setText("0.0")
        );

        editBudgetDialog.show();
    }

    private static AlertDialog setupPopupDialog(Activity activity, ViewBinding binding) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(binding.getRoot());
        AlertDialog popupDialog = builder.create();
        popupDialog.setCancelable(true);
        popupDialog.getWindow().setGravity(Gravity.BOTTOM);

        return popupDialog;
    }

    private static void initializeEditBudgetPopup(PopupEditBudgetBinding popupBinding, MainBudgetViewModel mainBudgetViewModel) {
        MainBudget mainBudget = mainBudgetViewModel.getMainBudget();
        double budget = mainBudget.getBudgetFormatted();
        double spent = mainBudget.getSpentFormatted();
        popupBinding.etEditBudget.setText(String.valueOf(budget));
        popupBinding.etEditBudgetSpent.setText(String.valueOf(spent));
        popupBinding.checkEditBudgetIsHidden.setChecked(mainBudget.isHidden());
        updateBalance(popupBinding.tvEditBudgetBalance, budget, spent);
    }

    private static void setupEditTextListener(PopupEditBudgetBinding popupBinding) {
        Runnable updatePopupBalance = () -> updateBalanceWithStrings(
                popupBinding.tvEditBudgetBalance,
                popupBinding.etEditBudget.getText().toString(),
                popupBinding.etEditBudgetSpent.getText().toString()
        );
        EditTextUtils.setOnTextChangedListener(popupBinding.etEditBudget, updatePopupBalance);
        EditTextUtils.setOnTextChangedListener(popupBinding.etEditBudgetSpent, updatePopupBalance);
    }

    private static void updateBalanceWithStrings(TextView balanceView, String budgetString, String spentString) {
        updateBalance(
                balanceView,
                budgetString.isEmpty() ? 0.0 : Double.parseDouble(budgetString),
                spentString.isEmpty() ? 0.0 : Double.parseDouble(spentString)
        );
    }

    private static void updateBalance(TextView balanceView, double budget, double spent) {
        double balance = NumberUtils.twoDecimalsRounded(budget - spent);
        balanceView.setText(String.valueOf(balance));
    }

}
