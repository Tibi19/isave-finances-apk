package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.databinding.PopupAddPaymentBinding;
import com.tam.isave.databinding.PopupEditPaymentBinding;
import com.tam.isave.model.ModelRepository;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.model.category.CategoryUtils;
import com.tam.isave.utils.Date;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {

    private ModelRepository modelRepository;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        modelRepository = ModelRepository.getModelRepository(application);
    }

    public LiveData<List<Transaction>> getTransactions() {
        return modelRepository.getTransactions();
    }

    public LiveData<List<Transaction>> getCategoryTransactions(int categoryId) {
        return modelRepository.getCategoryTransactions(categoryId);
    }

    public LiveData<List<Transaction>> getIntervalTransactions(int startDateValue, int endDateValue) {
        if(startDateValue < 0 || endDateValue < 0) { return null; }
        return modelRepository.getIntervalTransactions(startDateValue, endDateValue);
    }

    public LiveData<List<Transaction>> getGoalOrganizerTransactions(int startDateValue, int numberOfDays) {
        if(startDateValue < 0 || numberOfDays < 0) { return null; }
        return modelRepository.getGlobalOrganizerTransactions(startDateValue, numberOfDays);
    }

    public void setupTrackerTransactions(List<Transaction> transactions) {
        if(transactions == null) { return; }
        modelRepository.setupTrackerTransactions(transactions);
    }

    public void setupGoalOrganizerTransactions(List<Transaction> transactions) {
        if(transactions == null) { return; }
        modelRepository.setupGoalOrganizerTransactions(transactions);
    }

    public void addPayment(PopupAddPaymentBinding addPaymentBinding, List<Category> categories) {
        boolean organizablePayment = addPaymentBinding.checkAddPaymentIsOrganizable.isChecked();

        String paymentName = addPaymentBinding.editAddPaymentName.getText().toString();

        String paymentValueString = addPaymentBinding.editAddPaymentValue.getText().toString();
        double paymentValue = paymentValueString.isEmpty() ? 0.0 : Double.parseDouble(paymentValueString);

        Date paymentDate = new Date(addPaymentBinding.etAddPaymentDate.getText().toString());

        String categoryName = addPaymentBinding.spinAddPaymentCategories.getSelectedItem().toString();
        Category category = CategoryUtils.getCategoryByName(categories, categoryName);
        if (category == null) { return; }

        modelRepository.newPayment(paymentDate, paymentName, paymentValue, category.getId(), organizablePayment);
    }

    public void editPayment(Transaction transaction, PopupEditPaymentBinding editPaymentBinding, List<Category> categories) {
        String newPaymentName = editPaymentBinding.etEditPaymentName.getText().toString();

        String newPaymentValueString = editPaymentBinding.etEditPaymentValue.getText().toString();
        double newPaymentValue = newPaymentValueString.isEmpty() ? 0.0 : Double.parseDouble(newPaymentValueString);

        Date newPaymentDate = new Date(editPaymentBinding.etEditPaymentDate.getText().toString());

        String newCategoryName = editPaymentBinding.spinEditPaymentCategories.getSelectedItem().toString();
        Category newCategory = CategoryUtils.getCategoryByName(categories, newCategoryName);
        if (newCategory == null) { return; }

        modelRepository.modifyPayment(transaction, newCategory.getId(), newPaymentName, newPaymentDate, newPaymentValue);
    }

    public void deletePayment(Transaction payment) {
        modelRepository.deletePayment(payment);
    }

}
