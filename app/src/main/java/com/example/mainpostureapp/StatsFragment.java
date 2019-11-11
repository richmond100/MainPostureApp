package com.example.mainpostureapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StatsFragment extends Fragment
{
    TextView errorBR, errorBL, errorTR, errorTL, errorSlouch;
    Context mContext;
    IntentFilter filter;
    private BroadcastReceiver updater;
    private ArrayList<Data> userData;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View  v = inflater.inflate(R.layout.fragment_stats, container, false);
        errorBR = v.findViewById(R.id.errorBR);
        errorBL = v.findViewById(R.id.errorBL);
        errorTR = v.findViewById(R.id.errorTR);
        errorTL = v.findViewById(R.id.errorTL);
        errorSlouch = v.findViewById(R.id.errorSlouch);
        updater = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                update();
            }
        };
        filter = new IntentFilter();
        filter.addAction("update");
        return v;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        update();
        mContext.registerReceiver(updater, filter);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        mContext.unregisterReceiver(updater);
    }
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mContext = context;

    }
    @Override
    public void onDetach()
    {
        super.onDetach();
        mContext = null;
    }

    private void update()
    {
        SharedPreferences data = mContext.getSharedPreferences("datalist", mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        Gson gson = new Gson();
        String json = data.getString("datalist", null);
        Type type = new TypeToken<ArrayList<Data>>() {}.getType();
        userData = gson.fromJson(json, type);
        if (userData != null)
        {
            errorBL.setText(Integer.toString(userData.get(userData.size()-1).getBottomL()));
            errorBR.setText(Integer.toString(userData.get(userData.size()-1).getBottomR()));
            errorTL.setText(Integer.toString(userData.get(userData.size()-1).getTopL()));
            errorTR.setText(Integer.toString(userData.get(userData.size()-1).getTopR()));
            errorSlouch.setText(Integer.toString(userData.get(userData.size()-1).getSlouch()));
        }

    }

}
