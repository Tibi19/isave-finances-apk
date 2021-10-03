package com.tam.isave.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tam.isave.model.goalorganizer.Interval;
import com.tam.isave.utils.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Interval.class}, version = Constants.VERSION_INTERVAL_TABLE, exportSchema = false)
public abstract class IntervalRoomDatabase extends RoomDatabase {

    private static volatile IntervalRoomDatabase INSTANCE;
    public abstract IntervalDao intervalDao();

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService DATABASE_WRITE_EXECUTOR =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static IntervalRoomDatabase getDatabase(final Context context) {
        if (INSTANCE != null) return INSTANCE;

        synchronized (IntervalRoomDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        IntervalRoomDatabase.class, Constants.INTERVAL_DATABASE)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }

        return INSTANCE;
    }

}
