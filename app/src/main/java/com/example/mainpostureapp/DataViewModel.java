package com.example.mainpostureapp;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.PrimaryKey;

import java.util.List;

public class DataViewModel extends AndroidViewModel
{
    private DataRepository repository;
    private LiveData<List<Data>> allData;


    public DataViewModel(@NonNull Application application)
    {
        super(application);
        repository = new DataRepository(application);
        allData = repository.getAllData();
    }

    public int convertStringtoInt(String num)
    {
        String[] strNum = num.split(":", 0);
        if (strNum.length ==3)
        {
            int hour =Integer.parseInt(strNum[0]) * 3600;
            int minute =Integer.parseInt(strNum[1]) * 60;
            int second =Integer.parseInt(strNum[0]);
            return hour + minute + second;
        }
        else
        {
            int minute =Integer.parseInt(strNum[1]) * 60;
            int second =Integer.parseInt(strNum[0]);
            return minute + second;
        }
    }

    public void insert(Data data)
    {
        repository.insert(data);
    }

    public void update(Data data)
    {
        repository.update(data);
    }

    public void delete(Data data)
    {
        repository.delete(data);
    }

    public void deleteAll()
    {
        repository.deleteAllData();
    }

    public LiveData<List<Data>> getAllData()
    {
        return allData;
    }

    public void openFile()
    {

    }
}
