package com.tam.isave.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;

import com.tam.isave.databinding.ActivityHomeBinding;
import com.tam.isave.databinding.PopupAddPaymentBinding;
import com.tam.isave.model.category.Category;
import com.tam.isave.utils.CategoryUtils;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.DebugUtils;
import com.tam.isave.viewmodel.CategoryViewModel;
import com.tam.isave.viewmodel.TransactionViewModel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


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
        setupMenuButton();
        setupAddTransactionButton();

        if (savedInstanceState == null) {
            startCategoriesTransaction();
        }
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

        builder.setView(addPaymentBinding.getRoot());
        addTransactionDialog = builder.create();
        addTransactionDialog.setCancelable(true);
        addTransactionDialog.getWindow().setGravity(Gravity.BOTTOM);

        EditTextDatePicker.build(this, addPaymentBinding.etAddPaymentDate);
        CategorySpinnerPicker.build(this, addPaymentBinding.spinAddPaymentCategories, categoryViewModel.getCategories().getValue());

        addPaymentBinding.buttonAddPaymentyCancel.setOnClickListener(listener -> addTransactionDialog.dismiss());
        addPaymentBinding.buttonAddPaymentSubmit.setOnClickListener(listener -> {
            addNewPayment(addPaymentBinding);
            addTransactionDialog.dismiss();
        });

        addTransactionDialog.show();
    }

    private void addNewPayment(PopupAddPaymentBinding addPaymentBinding) {
        boolean organizablePayment = addPaymentBinding.checkAddPaymentIsOrganizable.isChecked();

        String paymentName = addPaymentBinding.editAddPaymentName.getText().toString();

        String paymentValueString = addPaymentBinding.editAddPaymentValue.getText().toString();
        double paymentValue = paymentValueString.isEmpty() ? 0.0 : Double.parseDouble(paymentValueString);

        Date paymentDate = new Date(addPaymentBinding.etAddPaymentDate.getText().toString());

        String categoryName = addPaymentBinding.spinAddPaymentCategories.getSelectedItem().toString();
        Category category = CategoryUtils.getCategoryByName(categoryViewModel.getCategories().getValue(), categoryName);
        if (category == null) { return; }

        transactionViewModel.addPayment(paymentDate, paymentName, paymentValue, category.getId(), organizablePayment);
        //X TODO get date from datePicker
        //X TODO get parent category from dropDownList
        //TODO add payment through transactionViewModel
    }

    private void startCategoriesTransaction() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(homeBinding.fragmentContainerCategories.getId(), CategoriesFragment.class, null)
                .commit();
    }

}