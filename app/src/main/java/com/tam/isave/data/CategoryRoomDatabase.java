package com.tam.isave.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tam.isave.model.CategoryTools.Category;
import com.tam.isave.utils.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Category.class}, version = 1, exportSchema = false)
public abstract class CategoryRoomDatabase extends RoomDatabase {

    private static volatile CategoryRoomDatabase INSTANCE;
    public abstract CategoryDao categoryDao();

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService DATABASE_WRITE_EXECUTOR =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static CategoryRoomDatabase getDatabase(final Context context) {
        if (INSTANCE != null) return INSTANCE;

        synchronized (CategoryRoomDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        CategoryRoomDatabase.class, Constants.DATABASE_NAME)
                        .build();
            }
        }

        return INSTANCE;
    }

}
