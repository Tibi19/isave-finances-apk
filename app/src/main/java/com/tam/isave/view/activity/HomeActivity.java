package com.tam.isave.view.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import com.tam.isave.R;
import com.tam.isave.databinding.ActivityHomeBinding;
import com.tam.isave.databinding.PopupAddPaymentBinding;
import com.tam.isave.model.MainBudget;
import com.tam.isave.model.category.Category;
import com.tam.isave.utils.ButtonAreaExtensionUtils;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.DebugUtils;
import com.tam.isave.utils.LiveDataUtils;
import com.tam.isave.view.dialog.CategorySpinnerPicker;
import com.tam.isave.view.dialog.EditMainBudgetBuilder;
import com.tam.isave.view.dialog.EditTextDatePicker;
import com.tam.isave.viewmodel.CategoryViewModel;
import com.tam.isave.viewmodel.MainBudgetViewModel;
import com.tam.isave.viewmodel.TransactionViewModel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding homeBinding;
    private TransactionViewModel transactionViewModel;
    private CategoryViewModel categoryViewModel;
    private MainBudgetViewModel mainBudgetViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize the context of DebugUtils only for debug purposes.
        DebugUtils.context = this;
        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        mainBudgetViewModel = new ViewModelProvider(this).get(MainBudgetViewModel.class);

        setupCategoryTracker();
        setupHistoryButton();
        setupAddTransactionButton();
        setupInfoButton();
        setupBalance();

        ButtonAreaExtensionUtils.extendHitAreaOfButtons(
                this,
                homeBinding.textBalance,
                homeBinding.buttonHomeInfo,
                homeBinding.buttonHomeHistory,
                homeBinding.buttonAddTransaction
        );
    }

    private void setupBalance() {
        updateBalanceView();
        homeBinding.textBalance.setOnClickListener(
                editBudget -> EditMainBudgetBuilder.showPopup(
                        this,
                        getLayoutInflater(),
                        this,
                        this::updateBalanceView
                )
        );
    }

    private void updateBalanceView() {
        String balanceString = Constants.NAMING_BUDGET_HIDDEN;
        MainBudget mainBudget = mainBudgetViewModel.getMainBudget();

        if(!mainBudget.isHidden()) {
            balanceString = String.valueOf(mainBudget.getBalance());
        }

        homeBinding.textBalance.setText(balanceString);
    }

    /**
     * Sets up the tracker model that will take care of category calculations.
     */
    private void setupCategoryTracker() {
        LiveDataUtils.observeOnce(
                categoryViewModel.getCategories(),
                categories -> categoryViewModel.setupTrackerCategories(categories));
        LiveDataUtils.observeOnce(
                transactionViewModel.getTransactions(),
                transactions -> transactionViewModel.setupTrackerTransactions(transactions)
        );
    }

    private void setupAddTransactionButton() {
        homeBinding.buttonAddTransaction.setOnClickListener( addTransaction -> showAddTransactionPopup() );
    }

    private void setupInfoButton() {
        homeBinding.buttonHomeInfo.setOnClickListener( startInfoActivity -> {
            Intent startInfoActivityIntent = new Intent(this, InfoActivity.class);
            startActivity(startInfoActivityIntent);
        });
    }

    private void setupHistoryButton() {
        homeBinding.buttonHomeHistory.setOnClickListener( startGlobalHistory -> {
            Intent startGlobalHistoryIntent = new Intent(this, GlobalHistoryActivity.class);
            startActivity(startGlobalHistoryIntent);
        });
    }

    private void showAddTransactionPopup() {
        AlertDialog addTransactionDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        PopupAddPaymentBinding addPaymentBinding = PopupAddPaymentBinding.inflate(getLayoutInflater());
        List<Category> categories = categoryViewModel.getCategories().getValue();

        builder.setView(addPaymentBinding.getRoot());
        addTransactionDialog = builder.create();
        addTransactionDialog.setCancelable(true);
        addTransactionDialog.getWindow().setGravity(Gravity.BOTTOM);
        addTransactionDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        EditTextDatePicker.build(this, addPaymentBinding.etAddPaymentDate);
        CategorySpinnerPicker.build(this, addPaymentBinding.spinAddPaymentCategories, categories);

        addPaymentBinding.buttonAddPaymentyCancel.setOnClickListener(listener -> addTransactionDialog.dismiss());
        addPaymentBinding.buttonAddPaymentSubmit.setOnClickListener(listener -> {
            if(!transactionViewModel.isAddPaymentValid(addPaymentBinding, this)) { return; }
            transactionViewModel.addPayment(addPaymentBinding, categories);
            updateBalanceView();
            addTransactionDialog.dismiss();
        });

        addTransactionDialog.show();
    }

}