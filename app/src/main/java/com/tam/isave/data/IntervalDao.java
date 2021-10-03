package com.tam.isave.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.tam.isave.model.goalorganizer.Interval;

import java.util.List;

@Dao
public interface IntervalDao {

    @Insert
    void insert(Interval interval);

    @Update
    void update(Interval... intervals);

    @Delete
    void delete(Interval interval);

    @Query("DELETE FROM interval_table")
    void deleteAll();

    @Query("SELECT * FROM interval_table")
    LiveData<List<Interval>> getIntervals();

}
