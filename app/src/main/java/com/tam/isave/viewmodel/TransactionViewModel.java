package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.databinding.PopupAddPaymentBinding;
import com.tam.isave.model.ModelRepository;
import com.tam.isave.model.category.Category;
import com.tam.isave.model.transaction.Payment;
import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.CategoryUtils;
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
        return modelRepository.getIntervalTransactions(startDateValue, endDateValue);
    }

    public void addPayment(PopupAddPaymentBinding addPaymentBinding, CategoryViewModel categoryViewModel) {
        boolean organizablePayment = addPaymentBinding.checkAddPaymentIsOrganizable.isChecked();

        String paymentName = addPaymentBinding.editAddPaymentName.getText().toString();

        String paymentValueString = addPaymentBinding.editAddPaymentValue.getText().toString();
        double paymentValue = paymentValueString.isEmpty() ? 0.0 : Double.parseDouble(paymentValueString);

        Date paymentDate = new Date(addPaymentBinding.etAddPaymentDate.getText().toString());

        String categoryName = addPaymentBinding.spinAddPaymentCategories.getSelectedItem().toString();
        Category category = CategoryUtils.getCategoryByName(categoryViewModel.getCategories().getValue(), categoryName);
        if (category == null) { return; }

        modelRepository.newPayment(paymentDate, paymentName, paymentValue, category.getId(), organizablePayment);
    }

    public void addCashing(Date date, String name, double value, boolean modifiesOrganizer) {
        modelRepository.newCashing(date, name, value, modifiesOrganizer);
    }

    public void modifyPayment(Payment payment, int newParentId, String newName, Date newDate, double newValue) {
        modelRepository.modifyPayment(payment, newParentId, newName, newDate, newValue);
    }

    public void deleteTransaction(Transaction transaction) {
        modelRepository.deleteTransaction(transaction);
    }

}
