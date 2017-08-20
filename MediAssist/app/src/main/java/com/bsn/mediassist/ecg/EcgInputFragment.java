package com.bsn.mediassist.ecg;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsn.mediassist.R;
import com.bsn.mediassist.workers.ConnectThread;

import java.util.UUID;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcgInputFragment extends Fragment {


    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    private UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String NAME;


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
        DEFAULT_UUID = bluetoothDevice.getUuids()[0].getUuid();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ecg_input, container, false);

        ButterKnife.bind(this, v);


        ConnectThread connectThread = new ConnectThread(bluetoothDevice, bluetoothAdapter, NAME, DEFAULT_UUID);


        return v;
    }

}
