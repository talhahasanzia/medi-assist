package com.bsn.mediassist.ecg;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bsn.mediassist.R;
import com.bsn.mediassist.workers.ConnectThread;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcgInputFragment extends Fragment {


    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    private UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String NAME;

    @BindView(R.id.connected_device)
    TextView connectedDevice;

    public static final String ACTION_CONNECTION_STATUS = "com.bsn.mediassist.BLUETOOTH_STATUS";

    public EcgInputFragment() {
        // Required empty public constructor

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        EcgActivity ecgActivity = (EcgActivity) getActivity();

        bluetoothAdapter = ecgActivity.mBluetoothAdapter;
        bluetoothDevice = ecgActivity.currentBluetoothDevice;
        NAME = bluetoothDevice.getName();
        if (bluetoothDevice.getUuids() != null)
            DEFAULT_UUID = bluetoothDevice.getUuids()[0].getUuid();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ecg_input, container, false);

        ButterKnife.bind(this, v);


        connectedDevice.setText("Pairing with the selected device...");
        IntentFilter intent = new IntentFilter(ACTION_CONNECTION_STATUS);

        getActivity().registerReceiver(mReceiver, intent);

        ConnectThread connectThread =
                new ConnectThread(getActivity(), bluetoothDevice, bluetoothAdapter, NAME, DEFAULT_UUID);

        connectThread.start();


        return v;
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals(ACTION_CONNECTION_STATUS)) {

                Toast.makeText(context, "Connection Status:" + intent.getBooleanExtra("status", false), Toast.LENGTH_SHORT).show();


                if (intent.getBooleanExtra("status", false)) {
                    connectedDevice.setText("Connected to: "+bluetoothDevice.getName());
                } else {

                    connectedDevice.setText("Failed to connect.");
                }

            }

        }
    };

}
