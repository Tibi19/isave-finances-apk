package com.tam.isave.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tam.isave.model.transaction.Transaction;
import com.tam.isave.utils.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Transaction.class}, version = Constants.VERSION_TRANSACTION_TABLE, exportSchema = false)
public abstract class TransactionRoomDatabase extends RoomDatabase {

    private static volatile TransactionRoomDatabase INSTANCE;
    public abstract TransactionDao transactionDao();

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService DATABASE_WRITE_EXECUTOR =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static TransactionRoomDatabase getDatabase(final Context context) {
        if (INSTANCE != null) return INSTANCE;

        synchronized (TransactionRoomDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        TransactionRoomDatabase.class, Constants.TRANSACTION_DATABASE)
                        .addMigrations(TransactionDatabaseMigrations.getAddOrganizableColumnMigration())
                        .build();
            }
        }

        return INSTANCE;
    }

}