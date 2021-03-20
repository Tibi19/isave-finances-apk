package com.tam.isave.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;

import com.tam.isave.databinding.ActivityHomeBinding;
import com.tam.isave.databinding.PopupAddCategoryBinding;
import com.tam.isave.databinding.PopupAddPaymentBinding;
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

        addPaymentBinding.buttonAddPaymentyCancel.setOnClickListener(listener -> addTransactionDialog.dismiss());
        addPaymentBinding.buttonAddPaymentSubmit.setOnClickListener(listener -> {
            addNewPayment(addPaymentBinding);
            addTransactionDialog.dismiss();
        });
        //TODO setup datePicker: https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
        //TODO setup categories dropdown: https://developer.android.com/guide/topics/ui/controls/spinner

        addTransactionDialog.show();
    }

    private void addNewPayment(PopupAddPaymentBinding addPaymentBinding) {
        boolean paymentIsOrganizable = addPaymentBinding.checkAddPaymentIsOrganizable.isChecked();
        String paymentName = addPaymentBinding.editAddPaymentName.getText().toString();
        double paymentValue = Double.parseDouble(addPaymentBinding.editAddPaymentValue.getText().toString());
        //TODO get date from datePicker
        //TODO get parent category from dropDownList
        //TODO add payment through transactionViewModel
    }

    private void startCategoriesTransaction() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(homeBinding.fragmentContainerCategories.getId(), CategoriesFragment.class, null)
                .commit();
    }

}