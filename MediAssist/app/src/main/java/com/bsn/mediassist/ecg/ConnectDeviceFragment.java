package com.bsn.mediassist.ecg;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bsn.mediassist.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectDeviceFragment extends Fragment {


    BluetoothAdapter mBluetoothAdapter;


    Set<BluetoothDevice> pairedDevices = null;


    List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    @BindView(R.id.title)
    TextView titleText;


    @BindView(R.id.listview)
    ListView listView;

    ArrayAdapter<String> arrayAdapter;


    @OnClick(R.id.search)
    public void OnClick(View v) {

        if (!mBluetoothAdapter.isDiscovering())
            setDiscoverDevices();


    }


    public ConnectDeviceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mBluetoothAdapter = ((EcgActivity) getActivity()).mBluetoothAdapter;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connect_device, container, false);


        ButterKnife.bind(this, v);

        listView.setClickable(true);

        setPairedDevices();

        return v;
    }


    void setPairedDevices() {


        ArrayList<String> deviceNames = new ArrayList<>();

        if (mBluetoothAdapter != null) {
            pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();

                    deviceNames.add(deviceName);
                }
            } else {

                createAndShowAlertDialog();

            }
        }


        arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item,
                R.id.blue_list_item,
                deviceNames);


        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (pairedDevices != null && pairedDevices.size() > 0) {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) pairedDevices.toArray()[position];

                    if (bluetoothDevice != null) {
                        ((EcgActivity) getActivity()).currentBluetoothDevice = bluetoothDevice;
                        Slide slideTransition = new Slide(Gravity.RIGHT);
                        slideTransition.setDuration(1000);
                        EcgInputFragment ecgInputFragment = new EcgInputFragment();
                        ecgInputFragment.setEnterTransition(slideTransition);
                        ((EcgActivity) getActivity()).setCurrentScreen(ecgInputFragment);

                        ((EcgActivity) getActivity()).currentScreen = 1;

                    }
                }
            }
        });
    }


    void setDiscoverDevices() {


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    890);
        else {


            startDiscovery();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 890) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDiscovery();

            } else {

                Toast.makeText(getActivity(), "Bluetooth discovery also requires Location feature, please allow.", Toast.LENGTH_SHORT).show();

            }
        }

    }


    private void startDiscovery() {

        titleText.setText("Discovered devices:");

        IntentFilter filter = new IntentFilter(ACTION_FOUND);
        filter.addAction(ACTION_DISCOVERY_FINISHED);
        filter.addAction(ACTION_DISCOVERY_STARTED);
        getActivity().registerReceiver(mReceiver, filter);


        mBluetoothAdapter.startDiscovery();

        List<String> deviceNames = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item,
                R.id.blue_list_item,
                deviceNames);


        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (discoveredDevices != null && discoveredDevices.size() > 0) {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) discoveredDevices.toArray()[position];

                    if (bluetoothDevice != null) {
                        ((EcgActivity) getActivity()).currentBluetoothDevice = bluetoothDevice;


                        Slide slideTransition = new Slide(Gravity.RIGHT);
                        slideTransition.setDuration(1000);
                        EcgInputFragment ecgInputFragment = new EcgInputFragment();
                        ecgInputFragment.setEnterTransition(slideTransition);
                        ((EcgActivity) getActivity()).setCurrentScreen(ecgInputFragment);

                        ((EcgActivity) getActivity()).currentScreen = 1;

                    }
                }
            }
        });
    }


    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("No paired devices found, start searching?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setDiscoverDevices();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), "Please scan for devices using scan icon at top-right!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                discoveredDevices.add(device);

                arrayAdapter.add(device.getName());
                arrayAdapter.notifyDataSetChanged();


                // listView.setAdapter(arrayAdapter);


            }
            if (ACTION_DISCOVERY_FINISHED.equals(action)) {

                Toast.makeText(context, "Finished searching Bluetooth devices.", Toast.LENGTH_SHORT).show();

            }

            if (ACTION_DISCOVERY_STARTED.equals(action)) {

                Toast.makeText(context, "Started searching Bluetooth devices.", Toast.LENGTH_SHORT).show();

            }
        }
    };


}
