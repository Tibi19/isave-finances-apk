package com.tam.isave.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;

import com.tam.isave.databinding.ActivityHomeBinding;
import com.tam.isave.databinding.PopupAddPaymentBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.utils.DebugUtils;
import com.tam.isave.utils.LiveDataUtils;
import com.tam.isave.view.dialog.CategorySpinnerPicker;
import com.tam.isave.view.dialog.EditTextDatePicker;
import com.tam.isave.view.fragment.CategoriesFragment;
import com.tam.isave.view.fragment.GoalOrganizerFragment;
import com.tam.isave.viewmodel.CategoryViewModel;
import com.tam.isave.viewmodel.TransactionViewModel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding homeBinding;
    private TransactionViewModel transactionViewModel;
    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize the context of DebugUtils only for debug purposes.
        DebugUtils.context = this;
        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        setupCategoryTracker();
        setupMenuButton();
        setupAddTransactionButton();
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

    private void setupMenuButton () {
        homeBinding.buttonHomeMenu.setOnClickListener( startGlobalHistory -> {
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

        EditTextDatePicker.build(this, addPaymentBinding.etAddPaymentDate);
        CategorySpinnerPicker.build(this, addPaymentBinding.spinAddPaymentCategories, categories);

        addPaymentBinding.buttonAddPaymentyCancel.setOnClickListener(listener -> addTransactionDialog.dismiss());
        addPaymentBinding.buttonAddPaymentSubmit.setOnClickListener(listener -> {
            transactionViewModel.addPayment(addPaymentBinding, categories);
            addTransactionDialog.dismiss();
        });

        addTransactionDialog.show();
    }

}