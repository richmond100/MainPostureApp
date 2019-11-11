package com.example.mainpostureapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.os.Message;
import android.os.Bundle;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BluetoothService extends Service {
    final int handlerState = 0;                        //used to identify handler message
    Handler bluetoothIn;
    private BluetoothAdapter btAdapter = null;

    private ConnectingThread mConnectingThread;
    private ConnectedThread mConnectedThread;

    private boolean stopThread;
    private boolean isRunning;
    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String for MAC address
    private static String MAC_ADDRESS;

    private StringBuilder recDataString = new StringBuilder();
    private StringBuilder writeDataString = new StringBuilder();

    private BroadcastReceiver receiver;
    private IntentFilter filter;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("BT SERVICE", "SERVICE CREATED");
        stopThread = false;
        isRunning = true;
        SharedPreferences status = getSharedPreferences("status", MODE_PRIVATE);
        SharedPreferences.Editor statusEditor = status.edit();
        statusEditor.putBoolean("status",isRunning);
        statusEditor.apply();
        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                SharedPreferences sharedPreferences = getSharedPreferences("settingslist", MODE_PRIVATE);
                writeDataString.append("a");
                writeDataString.append(sharedPreferences.getInt("bottomLSwitch", 1));
                writeDataString.append(sharedPreferences.getInt("bottomRSwitch", 1));
                writeDataString.append(sharedPreferences.getInt("topLSwitch", 1));
                writeDataString.append(sharedPreferences.getInt("topRSwitch", 1));
                writeDataString.append(sharedPreferences.getInt("slouchSwitch", 1));
                writeDataString.append("z");
                if (mConnectedThread != null)
                {
                    if(recDataString.length() == 0)
                    {
                        mConnectedThread.write(writeDataString.toString());
                        writeDataString.delete(0,writeDataString.length());
                        Toast.makeText(context,"Sending", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context,"Connection Busy, Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction("settings");
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        SharedPreferences address = getSharedPreferences("address", MODE_PRIVATE);
        MAC_ADDRESS = address.getString("address", "Null");
        Log.d("BT SERVICE", "SERVICE STARTED");
        bluetoothIn = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                Log.d("DEBUG", "handleMessage");
                if (msg.what == handlerState)
                { //if message is what we want
                    String readMessage = (String) msg.obj; // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);//`enter code here`
                    int startOfLineIndex = recDataString.indexOf("<");
                    int endOfLineIndex = recDataString.indexOf(">");
                    Log.d("RECORDED", String.valueOf(startOfLineIndex));
                    Log.d("RECORDED", String.valueOf(endOfLineIndex));
                    if (endOfLineIndex > 0 && startOfLineIndex > -1 && startOfLineIndex < endOfLineIndex)
                    {
                        Log.d("RECORDED", recDataString.toString());
                        String arduinoTxt = recDataString.substring(startOfLineIndex + 1, endOfLineIndex);
                        String [] txtArray = arduinoTxt.split("@",0);
                        for (int i = 0; i < txtArray.length;i++)
                        {
                            Log.d("RECORDED", txtArray[i]);
                        }
                        //String [] txtArray = new String []{"1", "2", "3", "4", "5"};
                        if (txtArray.length == 6)
                        {
                            int [] errorArray = new int []{Integer.parseInt(txtArray[0]), Integer.parseInt(txtArray[1]), Integer.parseInt(txtArray[2]), Integer.parseInt(txtArray[3]), Integer.parseInt(txtArray[4])};
                            DataRepository repository = new DataRepository(getApplication());
                            repository.dataCheck(errorArray);
                        }
                        Intent intent = new Intent("update");
                        sendBroadcast(intent);
                        recDataString.delete(0,recDataString.length());
                        mConnectedThread.write("q");
                    }
                }

            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        checkBTState();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        bluetoothIn.removeCallbacksAndMessages(null);
        stopThread = true;
        isRunning = false;
        if (mConnectedThread != null) {
            mConnectedThread.closeStreams();
        }
        if (mConnectingThread != null) {
            mConnectingThread.closeSocket();
        }
        SharedPreferences status = getSharedPreferences("status", MODE_PRIVATE);
        SharedPreferences.Editor statusEditor = status.edit();
        statusEditor.putBoolean("status",isRunning);
        statusEditor.apply();
        unregisterReceiver(receiver);
        Log.d("SERVICE", "onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        bluetoothIn.removeCallbacksAndMessages(null);
        stopThread = true;
        isRunning = false;
        if (mConnectedThread != null) {
            mConnectedThread.closeStreams();
        }
        if (mConnectingThread != null) {
            mConnectingThread.closeSocket();
        }
        SharedPreferences status = getSharedPreferences("status", MODE_PRIVATE);
        SharedPreferences.Editor statusEditor = status.edit();
        statusEditor.putBoolean("status",isRunning);
        statusEditor.apply();
        unregisterReceiver(receiver);
        Log.d("SERVICE", "onDestroy");
    }
    public boolean isRunning(Context context)
    {
        SharedPreferences status = context.getSharedPreferences("status", MODE_PRIVATE);
        return isRunning;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if (btAdapter == null)
        {
            Log.d("BT SERVICE", "BLUETOOTH NOT SUPPORTED BY DEVICE, STOPPING SERVICE");
            stopSelf();
        }
        else
            {
            if (btAdapter.isEnabled())
            {
                Log.d("DEBUG BT", "BT ENABLED! BT ADDRESS : " + btAdapter.getAddress() + " , BT NAME : " + btAdapter.getName());
                try
                {
                    BluetoothDevice device = btAdapter.getRemoteDevice(MAC_ADDRESS);
                    Log.d("DEBUG BT", "ATTEMPTING TO CONNECT TO REMOTE DEVICE : " + MAC_ADDRESS);
                    mConnectingThread = new ConnectingThread(device);
                    mConnectingThread.start();
                }
                catch (IllegalArgumentException e)
                {
                    Log.d("DEBUG BT", "PROBLEM WITH MAC ADDRESS : " + e.toString());
                    Log.d("BT SEVICE", "ILLEGAL MAC ADDRESS, STOPPING SERVICE");
                    stopSelf();
                }
            }
            else
                {
                    Log.d("BT SERVICE", "BLUETOOTH NOT ON, STOPPING SERVICE");
                    stopSelf();
                }
        }
    }

    // New Class for Connecting Thread
    private class ConnectingThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectingThread(BluetoothDevice device)
        {
            Log.d("DEBUG BT", "IN CONNECTING THREAD");
            mmDevice = device;
            BluetoothSocket temp = null;
            Log.d("DEBUG BT", "MAC ADDRESS : " + MAC_ADDRESS);
            Log.d("DEBUG BT", "BT UUID : " + BTMODULEUUID);
            try
            {
                temp = mmDevice.createRfcommSocketToServiceRecord(BTMODULEUUID);
                Log.d("DEBUG BT", "SOCKET CREATED : " + temp.toString());
            }
            catch (IOException e)
            {
                Log.d("DEBUG BT", "SOCKET CREATION FAILED :" + e.toString());
                Log.d("BT SERVICE", "SOCKET CREATION FAILED, STOPPING SERVICE");
                stopSelf();
            }
            mmSocket = temp;
        }

        @Override
        public void run()
        {
            super.run();
            Log.d("DEBUG BT", "IN CONNECTING THREAD RUN");
            // Establish the Bluetooth socket connection.
            // Cancelling discovery as it may slow down connection
            btAdapter.cancelDiscovery();
            try
            {
                mmSocket.connect();
                Log.d("DEBUG BT", "BT SOCKET CONNECTED");
                mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();
                Log.d("DEBUG BT", "CONNECTED THREAD STARTED");
                //I send a character when resuming.beginning transmission to check device is connected
                //If it is not an exception will be thrown in the write method and finish() will be called
                mConnectedThread.write("x");
            }
            catch (IOException e)
            {
                try
                {
                    Log.d("DEBUG BT", "SOCKET CONNECTION FAILED : " + e.toString());
                    Log.d("BT SERVICE", "SOCKET CONNECTION FAILED, STOPPING SERVICE");
                    mmSocket.close();
                    stopSelf();
                }
                catch (IOException e2)
                {
                    Log.d("DEBUG BT", "SOCKET CLOSING FAILED :" + e2.toString());
                    Log.d("BT SERVICE", "SOCKET CLOSING FAILED, STOPPING SERVICE");
                    stopSelf();
                    //insert code to deal with this
                }
            }
            catch (IllegalStateException e)
            {
                Log.d("DEBUG BT", "CONNECTED THREAD START FAILED : " + e.toString());
                Log.d("BT SERVICE", "CONNECTED THREAD START FAILED, STOPPING SERVICE");
                stopSelf();
            }
        }

        public void closeSocket()
        {
            try
            {
                //Don't leave Bluetooth sockets open when leaving activity
                mmSocket.close();
            }
            catch (IOException e2)
            {
                //insert code to deal with this
                Log.d("DEBUG BT", e2.toString());
                Log.d("BT SERVICE", "SOCKET CLOSING FAILED, STOPPING SERVICE");
                stopSelf();
            }
        }
    }

    // New Class for Connected Thread
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket)
        {
            Log.d("DEBUG BT", "IN CONNECTED THREAD");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e)
            {
                Log.d("DEBUG BT", e.toString());
                Log.d("BT SERVICE", "UNABLE TO READ/WRITE, STOPPING SERVICE");
                stopSelf();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            Log.d("DEBUG BT", "IN CONNECTED THREAD RUN");
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true)
            {
                try
                {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader stream = new BufferedReader(new InputStreamReader(mmInStream));
                    if ((bytes = stream.read()) != -1)
                    {
                        char c = (char) bytes;
                        if (sb.length() == 0 && c == '<')
                            sb.append(c);
                        while ((bytes = stream.read()) != -1)
                        {
                            c = (char) bytes;
                            sb.append(c);
                            if (c == '>')
                            {
                                break;
                            }
                        }
                    }
                    //bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    //String readMessage = new String(buffer, 0, bytes);

                    Log.d("DEBUG BT PART", "CONNECTED THREAD " + sb.toString());
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, sb.toString()).sendToTarget();
                }
                catch (IOException e)
                {
                    Log.d("DEBUG BT", e.toString());
                    Log.d("BT SERVICE", "UNABLE TO READ/WRITE, STOPPING SERVICE");
                    stopSelf();
                    break;
                }
            }
        }

        //write method
        public void write(String input)
        {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try
            {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            }
            catch (IOException e)
            {
                //if you cannot write, close the application
                Log.d("DEBUG BT", "UNABLE TO READ/WRITE " + e.toString());
                Log.d("BT SERVICE", "UNABLE TO READ/WRITE, STOPPING SERVICE");
                stopSelf();
            }
        }

        public void closeStreams()
        {
            try
            {
                //Don't leave Bluetooth sockets open when leaving activity
                mmInStream.close();
                mmOutStream.close();
            }
            catch (IOException e2)
            {
                //insert code to deal with this
                Log.d("DEBUG BT", e2.toString());
                Log.d("BT SERVICE", "STREAM CLOSING FAILED, STOPPING SERVICE");
                stopSelf();
            }
        }
    }
}
