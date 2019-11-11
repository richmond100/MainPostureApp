package com.example.mainpostureapp;
import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DataRepository
{
    private DataDao dataDao;
    private LiveData<List<Data>> allData;

    public DataRepository(Application application)
    {
        DataDatabase database = DataDatabase.getInstance(application);
        dataDao = database.DataDao();
        allData = dataDao.getAllData();
    }
    public void dataCheck(int[] errorArray)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String currentDateandTime = sdf.format(new Date());
        Data data = new Data(errorArray, currentDateandTime);
        Data currentData = dataDao.getFirst();
        if (currentData != null && currentData.compare(data) == true)
        {
            currentData.update(data);
            update(currentData);
        }
        else
        {
            insert(data);
        }

    }
    public void insert(Data data)
    {
        new InsertAsync(dataDao).execute(data);
    }

    public void update(Data data)
    {
        new UpdateAsync(dataDao).execute(data);
    }

    public void delete(Data data)
    {
        new DeleteAsync(dataDao).execute(data);
    }

    public void deleteAllData()
    {
        new DeleteAllAsync(dataDao).execute();
    }

    public LiveData<List<Data>> getAllData()
    {
        return allData;
    }

    private static class InsertAsync extends AsyncTask<Data, Void, Void>
    {
        private DataDao dataDao;
        private InsertAsync(DataDao dataDao)
        {
            this.dataDao = dataDao;
        }
        @Override
        protected Void doInBackground(Data...data)
        {
            dataDao.insert(data[0]);
            return null;
        }
    }

    private static class UpdateAsync extends AsyncTask<Data, Void, Void>
    {
        private DataDao dataDao;
        private UpdateAsync(DataDao dataDao)
        {
            this.dataDao = dataDao;
        }
        @Override
        protected Void doInBackground(Data...data)
        {
            dataDao.update(data[0]);
            return null;
        }
    }

    private static class DeleteAsync extends AsyncTask<Data, Void, Void>
    {
        private DataDao dataDao;
        private DeleteAsync(DataDao dataDao)
        {
            this.dataDao = dataDao;
        }
        @Override
        protected Void doInBackground(Data...data)
        {
            dataDao.delete(data[0]);
            return null;
        }
    }

    private static class DeleteAllAsync extends AsyncTask<Void, Void, Void>
    {
        private DataDao dataDao;
        private DeleteAllAsync(DataDao dataDao)
        {
            this.dataDao = dataDao;
        }
        @Override
        protected Void doInBackground(Void...voids)
        {
            dataDao.deleteAllData();
            return null;
        }
    }
}
