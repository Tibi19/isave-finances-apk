package com.tam.isave.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.tam.isave.model.transaction.Transaction;

import java.util.List;


@Dao
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction... transactions);

    @Delete
    void delete(Transaction transaction);

    @Query("DELETE FROM transaction_table")
    void deleteAll();

    @Query("SELECT * FROM transaction_table")
    LiveData<List<Transaction>> getTransactions();

    @Query("SELECT * FROM transaction_table WHERE parent_id = :parentId")
    LiveData<List<Transaction>> getCategoryTransactions(int parentId);

    @Query("SELECT * FROM transaction_table WHERE date_value BETWEEN :startDateValue AND :endDateValue")
    LiveData<List<Transaction>> getIntervalTransactions(int startDateValue, int endDateValue);
}
