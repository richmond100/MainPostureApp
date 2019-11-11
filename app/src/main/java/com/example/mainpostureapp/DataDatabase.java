package com.example.mainpostureapp;

import androidx.annotation.NonNull;
import androidx.appcompat.view.SupportActionModeWrapper;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;
import android.os.AsyncTask;

import javax.annotation.Nonnull;

@Database(entities = Data.class, version = 1)
public abstract class DataDatabase extends RoomDatabase
{
    private static DataDatabase instance;

    public abstract DataDao DataDao();

    public static synchronized DataDatabase getInstance(Context context)
    {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), DataDatabase.class, "data_database").fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    /*
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db)
        {
            super.onCreate(db);
            new PopulateDbAsync(instance).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>
    {
        private DataDao dataDao;
        private PopulateDbAsync(DataDatabase db)
        {
            dataDao = db.DataDao();
        }

        @Override
        protected void doInBackground(Void...voids)
        {
            dataDao.insert();
        }
    }

     */
}
