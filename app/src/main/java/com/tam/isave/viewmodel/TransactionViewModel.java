package com.tam.isave.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tam.isave.model.ModelRepository;
import com.tam.isave.model.transaction.Payment;
import com.tam.isave.model.transaction.Transaction;
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

    public void addPayment(Date date, String name, double value, int parentId, boolean organizable) {
        modelRepository.newPayment(date, name, value, parentId, organizable);
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
