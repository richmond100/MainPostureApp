package com.example.mainpostureapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class ImportExportFragment extends Fragment {
    private Context mContext;
    private static final String TAG = "MainActivity";
    private DataViewModel viewModel;
    private List<Data> userData;
    private static final int READ_REQUEST_CODE = 42;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_importexport, container, false);
        //Create viewmodel scoped to this fragment
        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        userData = new ArrayList<>();
        //call getAllData to populate viewmodel live data, then set fragment to observe
        viewModel.getAllData().observe(this, new Observer<List<Data>>()
        {
            @Override
            public void onChanged(List<Data> data) {
                //extract data and set it to userData
                userData = data;
            }
        });
        Button imp = v.findViewById(R.id.imp);
        imp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/csv");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
        Button export = v.findViewById(R.id.export);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] error = new int[6];
                Arrays.fill(error, 10);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                String currentDateandTime = sdf.format(new Date());
                Data data = new Data(error, currentDateandTime);
                viewModel.insert(data);
                viewModel.insert(data);
                try {
                    if (userData.isEmpty() == false) {
                        String format = "Date,Total Time,Good Posture Time,Poor Posture Time,Bottom left,Bottom Right,Top Left,Top Right,Slouch ";
                        File file = new File(mContext.getFilesDir(), "Posture" + userData.get(userData.size() - 1).getDate() + "to" + userData.get(0).getDate() + ".csv");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
                        writer.write(format);
                        writer.newLine();
                        for (int i = 0; i < userData.size(); i++) {
                            writer.write(userData.get(i).toString());
                            writer.newLine();
                        }
                        writer.close();
                        try {
                            String message = "Sent from Posture App";
                            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            emailIntent.setType("text/csv");
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                            Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                            emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            mContext.startActivity(Intent.createChooser(emailIntent, "Sending..."));
                        } catch (Throwable t) {
                            Toast.makeText(mContext, "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
                        }
                    } else
                        {
                        Toast.makeText(mContext, "No Data", Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable t) {
                    Toast.makeText(mContext, "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
                }

            }
        });

        Button clear = v.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                viewModel.deleteAll();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Uri uri = null;
            if (resultData != null)
            {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                try {
                    InputStream input = mContext.getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    try
                    {
                        reader.readLine();
                        String line = null;
                        while((line = reader.readLine()) != null)
                        {
                            Log.d("DEBUG", line);
                            String[] stringArray = line.split(",", 0);
                            int[] errorArray =new int[6];
                            errorArray[0] = viewModel.convertStringtoInt(stringArray[1]);
                            errorArray[1] = viewModel.convertStringtoInt(stringArray[4]);
                            errorArray[2] = viewModel.convertStringtoInt(stringArray[5]);
                            errorArray[3] = viewModel.convertStringtoInt(stringArray[6]);
                            errorArray[4] = viewModel.convertStringtoInt(stringArray[7]);
                            errorArray[5] = viewModel.convertStringtoInt(stringArray[8]);
                            Data data = new Data(errorArray, stringArray[0]);
                            viewModel.insert(data);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }


            }
        }
        else
        {
            Log.d("DEBUG", "fail");
        }
    }
}

