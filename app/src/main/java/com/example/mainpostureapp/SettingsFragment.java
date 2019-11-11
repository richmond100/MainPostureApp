package com.example.mainpostureapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Button;
import java.util.Arrays;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.widget.Toast;

public class SettingsFragment extends Fragment
{
    private Switch appSwitch, topRSwitch, topLSwitch, bottomLSwitch, bottomRSwitch, slouchSwitch;
    private Button save;
    private int [] settings;
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        settings = new int[6];
        Arrays.fill(settings, 1);
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        appSwitch = v.findViewById(R.id.notification);
        topRSwitch = v.findViewById(R.id.topR);
        topLSwitch = v.findViewById(R.id.topL);
        bottomRSwitch = v.findViewById(R.id.bottomR);
        bottomLSwitch = v.findViewById(R.id.bottomL);
        slouchSwitch = v.findViewById(R.id.slouch);
        save = v.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                settings[0] = appSwitch.isChecked() ? 1:0;
                settings[1] = topRSwitch.isChecked() ? 1:0;
                settings[2] = topLSwitch.isChecked() ? 1:0;
                settings[3] = bottomLSwitch.isChecked() ? 1:0;
                settings[4] = bottomRSwitch.isChecked() ? 1:0;
                settings[5] = slouchSwitch.isChecked() ? 1:0;
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("settingslist", mContext.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("appSwitch", settings[0]);
                editor.putInt("topRSwitch", settings[1]);
                editor.putInt("topLSwitch", settings[2]);
                editor.putInt("bottomLSwitch", settings[3]);
                editor.putInt("bottomRSwitch", settings[4]);
                editor.putInt("slouchSwitch", settings[5]);
                editor.apply();
                Intent intent = new Intent("settings");
                mContext.sendBroadcast(intent);
            }
        });
        updateSwitches();
        return v;
    }
    private void updateSwitches()
    {
        if (settings != null)
        {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("settingslist", mContext.MODE_PRIVATE);
            settings[0] = sharedPreferences.getInt("appSwitch", 1);
            settings[1] = sharedPreferences.getInt("topRSwitch", 1);
            settings[2] = sharedPreferences.getInt("topLSwitch", 1);
            settings[3] = sharedPreferences.getInt("bottomLSwitch", 1);
            settings[4] = sharedPreferences.getInt("bottomRSwitch", 1);
            settings[5] = sharedPreferences.getInt("slouchSwitch", 1);
            appSwitch.setChecked((settings[0] == 1));
            topRSwitch.setChecked((settings[1] == 1));
            topLSwitch.setChecked((settings[2] == 1));
            bottomLSwitch.setChecked((settings[3] == 1));
            bottomRSwitch.setChecked((settings[4] == 1));
            slouchSwitch.setChecked((settings[5] == 1));
        }
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
}
