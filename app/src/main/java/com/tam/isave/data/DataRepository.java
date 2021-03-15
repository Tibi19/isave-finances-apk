package com.tam.isave.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.tam.isave.model.category.Category;
import com.tam.isave.model.transaction.Transaction;

import java.util.List;

public class DataRepository {

    private CategoryDao categoryDao;
    private LiveData<List<Category>> categories;

    private TransactionDao transactionDao;

    public DataRepository(Application application) {
        CategoryRoomDatabase categoryDatabase = CategoryRoomDatabase.getDatabase(application);
        categoryDao = categoryDatabase.categoryDao();
        categories = categoryDao.getCategories();

        TransactionRoomDatabase transactionDatabase = TransactionRoomDatabase.getDatabase(application);
        transactionDao = transactionDatabase.transactionDao();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void insertCategory(final Category category) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> categoryDao.insert(category));
    }

    public void updateCategory(final Category category) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> categoryDao.update(category));
    }

    public void updateAllCategories(final List<Category> categories) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> {
            // A temporary category array;
            // To be used for converting categories list into an array.
            Category[] tempCategoryArray = new Category[categories.size()];
            // Convert categories list to an array and pass to dao update.
            categoryDao.update(categories.toArray(tempCategoryArray));
        });
    }

    public void deleteCategory(final Category category) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> categoryDao.delete(category));
    }

    public void deleteAllCategories() {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> categoryDao.deleteAll());
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactionDao.getTransactions();
    }

    public LiveData<List<Transaction>> getCategoryTransactions(int categoryId) {
        return transactionDao.getCategoryTransactions(categoryId);
    }

    public LiveData<List<Transaction>> getIntervalTransactions(int startDateValue, int endDateValue) {
        return transactionDao.getIntervalTransactions(startDateValue, endDateValue);
    }

    public void insertTransaction(final Transaction transaction) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> transactionDao.insert(transaction));
    }

    public void updateTransaction(final Transaction transaction) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> transactionDao.update(transaction));
    }

    public void updateAllTransactions(final List<Transaction> transactions) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> {
            // A temporary transactions array;
            // To be used for converting transactions list into an array.
            Transaction[] tempTransactionArray = new Transaction[transactions.size()];
            // Convert transactions list to an array and pass to dao update.
            transactionDao.update(transactions.toArray(tempTransactionArray));
        });
    }

    public void deleteTransaction(final Transaction transaction) {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> transactionDao.delete(transaction));
    }

    public void deleteAllTransactions() {
        CategoryRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> transactionDao.deleteAll());
    }
}
