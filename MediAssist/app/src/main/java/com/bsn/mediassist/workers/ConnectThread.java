package com.bsn.mediassist.workers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static com.bsn.mediassist.ecg.EcgInputFragment.ACTION_CONNECTION_STATUS;

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    String TAG = "Connect Thread";

    private BluetoothAdapter mBluetoothAdapter;
    String NAME;
    UUID MY_UUID;
    Context context;

    public ConnectThread(Context context, BluetoothDevice device, BluetoothAdapter bluetoothAdapter, String NAME, UUID uuid) {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.

        this.context = context;
        mBluetoothAdapter = bluetoothAdapter;
        this.NAME = NAME;
        MY_UUID = uuid;        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        boolean connectionStatus = true;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            connectionStatus = false;
            Intent intent = new Intent(ACTION_CONNECTION_STATUS);
            intent.putExtra("status", connectionStatus);

            context.sendBroadcast(intent);
            Log.e("Connect hread", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.

        boolean connectionStatus = true;

        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            Log.e(TAG, "Could not close the client socket", connectException);
            connectionStatus = false;
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                connectionStatus = false;
                Log.e(TAG, "Could not close the client socket", closeException);
            }

        }


        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        //TODO: connected as client
        Intent intent = new Intent(ACTION_CONNECTION_STATUS);
        intent.putExtra("status", connectionStatus);

        context.sendBroadcast(intent);

    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}