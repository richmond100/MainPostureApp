package com.example.mainpostureapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DataDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Data data);

    @Update
    void update(Data data);

    @Delete
    void delete(Data data);

    @Query("DELETE FROM data_table")
    void deleteAllData();

    @Query("SELECT * FROM data_table ORDER BY date DESC ")
    LiveData<List<Data>> getAllData();

    @Query("SELECT * FROM data_table ORDER BY date DESC LIMIT 1")
    Data getFirst();

}
