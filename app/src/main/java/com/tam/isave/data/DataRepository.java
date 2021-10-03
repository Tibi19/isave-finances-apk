package com.tam.isave.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.tam.isave.model.category.Category;
import com.tam.isave.model.goalorganizer.Interval;
import com.tam.isave.model.transaction.Transaction;

import java.util.List;

public class DataRepository {

    private CategoryDao categoryDao;
    private LiveData<List<Category>> categories;

    private IntervalDao intervalDao;
    private LiveData<List<Interval>> intervals;

    private TransactionDao transactionDao;

    public DataRepository(Application application) {
        initializeCategoryDao(application);
        initializeIntervalDao(application);
        initializeTransactionDao(application);
    }

    private void initializeTransactionDao(Application application) {
        TransactionRoomDatabase transactionDatabase = TransactionRoomDatabase.getDatabase(application);
        transactionDao = transactionDatabase.transactionDao();
    }

    private void initializeIntervalDao(Application application) {
        IntervalRoomDatabase intervalDatabase = IntervalRoomDatabase.getDatabase(application);
        intervalDao = intervalDatabase.intervalDao();
        intervals = intervalDao.getIntervals();
    }

    private void initializeCategoryDao(Application application) {
        CategoryRoomDatabase categoryDatabase = CategoryRoomDatabase.getDatabase(application);
        categoryDao = categoryDatabase.categoryDao();
        categories = categoryDao.getCategories();
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

    public LiveData<List<Interval>> getIntervals() {
        return intervals;
    }

    public void insertInterval(final Interval interval) {
        IntervalRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> intervalDao.insert(interval));
    }

    public void updateInterval(final Interval interval) {
        IntervalRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> intervalDao.update(interval));
    }

    public void updateAllIntervals(final List<Interval> intervals) {
        IntervalRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> {
            // A temporary interval array;
            // To be used for converting intervals list into an array.
            Interval[] tempIntervalArray = new Interval[intervals.size()];
            // Convert intervals list to an array and pass to dao update.
            intervalDao.update(intervals.toArray(tempIntervalArray));
        });
    }

    public void updateAllIntervals(final Interval[] intervals) {
        intervalDao.update(intervals);
    }

    public void deleteInterval(final Interval interval) {
        IntervalRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> intervalDao.delete(interval));
    }

    public void deleteAllIntervals() {
        IntervalRoomDatabase.DATABASE_WRITE_EXECUTOR.execute(() -> intervalDao.deleteAll());
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
