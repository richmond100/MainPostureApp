package com.example.mainpostureapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.ContextWrapper;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.SharedPreferences;


public class ConnectFragment extends Fragment
{
    private Context mContext;
    private ContextWrapper wrapper;
    private View v;

    // Debugging for LOGCAT
    private static final String TAG = "MainActivity";
    private static final boolean D = true;


    // declare button for launching website and textview for connection status
    Button tlbutton;
    TextView textView1;

    // EXTRA string to send on to mainactivity
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.fragment_connect, container, false);
        ContextWrapper wrapper = new ContextWrapper(mContext);
        checkBTState();

        textView1 = (TextView) v.findViewById(R.id.connecting);
        textView1.setTextSize(40);
        textView1.setText(" ");

        // Initialize array adapter for paired devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.list_item);

        // Find and set up the ListView for paired devices
        ListView pairedListView = v.findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices and append to 'pairedDevices'
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Add previosuly paired devices to the array
        if (pairedDevices.size() > 0) {
            v.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);//make title viewable
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            mPairedDevicesArrayAdapter.add("No Devices Paired");
        }
        return v;
    }



    private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
        {
            if (isRunning() == false)
            {
                textView1.setText("Connecting...");
                // Get the device MAC address, which is the last 17 chars in the View
                String info = ((TextView) v).getText().toString();
                String address = info.substring(info.length() - 17);
                SharedPreferences device = mContext.getSharedPreferences("address", mContext.MODE_PRIVATE);
                SharedPreferences.Editor editor = device.edit();
                editor.putString("address", address);
                editor.apply();
                Log.d("DEBUG", "saved");
                mContext.startService(new Intent(mContext, BluetoothService.class));
                ///v.findViewById(R.id.title_paired_devices).setVisibility(View.GONE);
                textView1.setText("Device Connected");
            }
        }
    };

    private void checkBTState() {
        // Check device has Bluetooth and that it is turned on
        mBtAdapter=BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBtAdapter==null) {
            Toast.makeText(wrapper.getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
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

    private boolean isRunning()
    {
        SharedPreferences status = mContext.getSharedPreferences("status", mContext.MODE_PRIVATE);
        return status.getBoolean("status", false);
    }

    @Override
    public  void onResume()
    {
        if (isRunning() == false)
        {
            v.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            textView1.setText("");
            Log.d(TAG, "Dead");
        }
        super.onResume();
    }
}
