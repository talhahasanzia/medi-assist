package com.bsn.mediassist.ecg;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bsn.mediassist.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcgInputFragment extends Fragment {


    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;

    InputStream aStream = null;
    InputStreamReader aReader = null;


    private static final String UUID_SERIAL_PORT_PROFILE
            = "00001101-0000-1000-8000-00805F9B34FB";

    private BluetoothSocket mSocket = null;
    private BufferedReader mBufferedReader = null;


    private UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String NAME;


    public Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("HANDLER_MESSAGE", String.format("Handler.handleMessage(): msg=%s", msg));

            byte[] bytes=msg.getData().getByteArray("data");

            try {

                String str = new String(bytes, "UTF-8");
                sampleText.setText(str);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }


            // This is where main activity thread receives messages
            // Put here your handling of incoming messages posted by other threads


            super.handleMessage(msg);
        }

    };

    @BindView(R.id.connected_device)
    TextView connectedDevice;


    @BindView(R.id.sample_data)
    TextView sampleText;

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


        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();

        startReceivingData();


        return v;
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals(ACTION_CONNECTION_STATUS)) {

                Toast.makeText(context, "Connection Status:" + intent.getBooleanExtra("status", false), Toast.LENGTH_SHORT).show();


                if (intent.getBooleanExtra("status", false)) {
                    connectedDevice.setText("Connected to: " + bluetoothDevice.getName());
                    startReceivingData();


                } else {

                    connectedDevice.setText("Failed to connect.");
                }

            }

        }
    };

    void startReceivingData() {


        try {

            BluetoothSocket socket = (BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(bluetoothDevice, 1);

            socket.connect();

            ConnectedThread connectedThread=new ConnectedThread(socket);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public class ConnectedThread extends Thread {


        private  BluetoothSocket mmSocket;
        private  InputStream mmInStream=null;
        private  OutputStream mmOutStream=null;
        private byte[] mmBuffer; // mmBuffer store for the stream

        private Handler mHandler;


        String TAG = "WORKER_CONNECTION";

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            mHandler=new Handler();
            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                mmOutStream = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }




            start();
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = new Message();



                    Bundle bundle=new Bundle();


                    if(numBytes>0) {
                        bundle.putByteArray("data", mmBuffer);
                        readMsg.setData(bundle);
                        readMsg.setTarget(_handler);
                        readMsg.sendToTarget();
                    }




                    //_handler.sendMessage(readMsg);
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
