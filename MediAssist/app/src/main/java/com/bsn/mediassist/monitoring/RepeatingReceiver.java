package com.bsn.mediassist.monitoring;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.bsn.mediassist.R;
import com.bsn.mediassist.data.User;
import com.bsn.mediassist.ecg.MessageConstants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;

public class RepeatingReceiver extends BroadcastReceiver {


    FirebaseAuth mAuth;

    String baseURL = "/users/";

    Context context;

    User mUser;

    int bpm;

    FirebaseDatabase database;
    FusedLocationProviderClient client;

    @Override
    public void onReceive(Context context, Intent intent) {


        client = LocationServices.getFusedLocationProviderClient(context);

        mAuth = FirebaseAuth.getInstance();

        this.context = context;
       
        if (haveNetworkConnection(context)) {
            processUserData(context);

        } else {


            sendNotification(context, "Warning", "No Internet, you maybe at risk!");
        }


    }

    public void sendNotification(Context context, String title, String message) {

//Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message);


        NotificationManager mNotificationManager =

                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(001, mBuilder.build());
    }

    void processUserData(final Context context) {


        if (mAuth != null && mAuth.getCurrentUser() != null) {

            // Get a reference to our posts
            database = FirebaseDatabase.getInstance();
            final DatabaseReference ref = database.getReference(baseURL + mAuth.getCurrentUser().getUid());

            // final ProgressDialog progress = ProgressDialog.show(this, "Getting data", "Please wait");
            // progress.setIndeterminate(true);
            //  progress.show();

            // Attach a listener to read the data at our posts reference
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ref.removeEventListener(this);

                    User user = dataSnapshot.getValue(User.class);

                    mUser = user;

                    bpm = getBPM();

                    setUpBluetooth(context);


                    //  progress.dismiss();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    // progress.dismiss();
                    ref.removeEventListener(this);
                }
            });


        } else {


            Toast.makeText(context, "No user logged in.", Toast.LENGTH_SHORT).show();

        }

    }


    void setUpBluetooth(Context context) {


        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> bluetoothDevices = bluetooth.getBondedDevices();

        BluetoothDevice requiredDevice = null;

        SharedPreferences prefs = context.getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);


        String address = prefs.getString("BT", null);

        if (address == null || bluetoothDevices.size() == 0)
            Toast.makeText(context, "No bluetooth device", Toast.LENGTH_SHORT).show();

        else {


            for (BluetoothDevice device : bluetoothDevices) {

                if (address.equals(device.getAddress())) {

                    requiredDevice = device;
                    Toast.makeText(context, "Found a device connected!", Toast.LENGTH_SHORT).show();
                    break;
                }

            }

        }


        if (requiredDevice != null) {

            startReceivingData(requiredDevice);
        } else {

            Toast.makeText(context, "No device connected!", Toast.LENGTH_SHORT).show();

        }

    }

    void sendMessagesAndCallDoctor(Context context, User user) {


        requestLocation(context);


    }

    String smsBody;

    void sendSMSWithLocation(Context context, Location location, User user, int bpm) {

        String locationString = location.getLatitude() + "," + location.getLongitude();

        smsBody = "Your patient, " + user.name + ", is at a risk of heart attack, and is here " + locationString + " (location) at the moment.";

        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        // Send a text based SMS
        try {
            smsManager.sendTextMessage(user.doctorNumber, null, smsBody, null, null);
            smsManager.sendTextMessage(user.relativeNumber1, null, smsBody, null, null);
            smsManager.sendTextMessage(user.relativeNumber2, null, smsBody, null, null);
            Toast.makeText(context, "Messages sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {

            Toast.makeText(context, "Number not valid", Toast.LENGTH_SHORT).show();
            Log.e("EmergencyActivity", "onClick: ", e);

        }

        client.removeLocationUpdates(mLocationCallback);

    }


    boolean checkGPS(Context context) {

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }


        return gps_enabled && network_enabled;

    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            sendSMSWithLocation(context, locationResult.getLastLocation(), mUser, bpm);
        }
    };


    void requestLocation(final Context context) {
        try {
            if (checkGPS(context)) {

                client.requestLocationUpdates(LocationRequest.create(), mLocationCallback, null);

            } else {
                client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.getResult() != null)
                            sendSMSWithLocation(context, task.getResult(), mUser, bpm);
                        else
                            Toast.makeText(context, "Location Services are disabled.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (SecurityException sc) {

        }
    }

    boolean warnUser(int bpm, User user) {
        boolean warning = false;


        if (user.gender.equals("Male")) {


            int age = Integer.parseInt(user.age);

            if (age >= 18 && age <= 25) {

                if (bpm > 82)
                    warning = true;


            } else if (age >= 26 && age <= 35) {

                if (bpm > 82)
                    warning = true;


            } else if (age >= 36 && age <= 45) {

                if (bpm > 83)
                    warning = true;


            }
            if (age >= 46 && age <= 55) {

                if (bpm > 84)
                    warning = true;


            }
            if (age >= 56 && age <= 65) {

                if (bpm > 82)
                    warning = true;


            }
            if (age > 65) {

                if (bpm > 80)
                    warning = true;


            }
        }
        if (user.gender.equals("Female")) {


            int age = Integer.parseInt(user.age);

            if (age >= 18 && age <= 25) {

                if (bpm > 85)
                    warning = true;


            } else if (age >= 26 && age <= 35) {

                if (bpm > 83)
                    warning = true;


            } else if (age >= 36 && age <= 45) {

                if (bpm > 85)
                    warning = true;


            }
            if (age >= 46 && age <= 55) {

                if (bpm > 84)
                    warning = true;


            }
            if (age >= 56 && age <= 65) {

                if (bpm > 84)
                    warning = true;


            }
            if (age > 65) {

                if (bpm > 84)
                    warning = true;


            }
        }

        if (bpm < 50)
            warning = true;

        return warning;
    }

    private boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    int getBPM() {

        Random rand = new Random();

        int n = rand.nextInt(150) + 55;

        return n;

    }


    User updateUserData() {

        User tempUser = mUser;

        String monitoringData = tempUser.monitoringData;


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");


        String today = simpleDateFormat.format(Calendar.getInstance().getTimeInMillis());

        if (monitoringData.equals("none")) {
            monitoringData = today + ":" + bpm;
        } else {


            monitoringData += "#" + today + ":" + bpm;

        }

        tempUser.monitoringData = monitoringData;

        showLogs(monitoringData);

        return tempUser;
    }

    void showLogs(String data) {

        String[] dataArray = data.split("#");

        for (String dataEntry : dataArray) {

            String today = dataEntry.split(":")[0];
            String bpm = dataEntry.split(":")[1];
            Log.d("DATA_ARRAY", today + " BPM: " + bpm);


        }


    }


    BluetoothSocket socket;
    ConnectedThread connectedThread;

    void startReceivingData(BluetoothDevice bluetoothDevice) {


        try {

            socket = (BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(bluetoothDevice, 1);

            socket.connect();


            Toast.makeText(context, "Connected to a device!", Toast.LENGTH_SHORT).show();
            connectedThread = new ConnectedThread(socket);


        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (InvocationTargetException e) {
            e.printStackTrace();

        } catch (NoSuchMethodException e) {

        } catch (IOException e) {
            e.printStackTrace();

        }


    }


    public class ConnectedThread extends Thread {


        private BluetoothSocket mmSocket;
        private InputStream mmInStream = null;
        private OutputStream mmOutStream = null;
        private byte[] mmBuffer; // mmBuffer store for the stream

        private Handler mHandler;


        String TAG = "WORKER_CONNECTION";

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            mHandler = new Handler();
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
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = new Message();


                    Bundle bundle = new Bundle();


                    if (numBytes > 0) {
                        bundle.putByteArray("data", mmBuffer);
                        bundle.putInt("status", 1);
                        readMsg.setData(bundle);
                        readMsg.setTarget(_handler);
                        readMsg.sendToTarget();
                        break;
                    }


                    //_handler.sendMessage(readMsg);
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);

                    Message readMsg = new Message();


                    Bundle bundle = new Bundle();

                    bundle.putInt("status", -1);
                    readMsg.setData(bundle);
                    readMsg.setTarget(_handler);
                    readMsg.sendToTarget();
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


    public Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("HANDLER_MESSAGE", String.format("Handler.handleMessage(): msg=%s", msg));

            byte[] bytes = msg.getData().getByteArray("data");

            try {

                String str = new String(bytes, "UTF-8");


                Log.d("BT_DATA", "handleMessage: " + str);


                if (str != null) {
                    connectedThread.cancel();
                    socket.close();


                    try {

                        if (str.contains("\n"))
                            str = str.split("\n")[0];

                        str = str.replaceAll("\\D+", "");

                        bpm = Integer.parseInt(str);

                        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTimeInMillis());

                        Intent intent = new Intent("ACTION_BPM");
                        intent.putExtra("bpm", bpm);
                        intent.putExtra("date", date);

                        context.sendBroadcast(intent);

                        User updatedUser = updateUserData();

                        DatabaseReference updateRef = database.getReference("users");


                        updateRef.child(mAuth.getCurrentUser().getUid()).setValue(updatedUser);

                        if (warnUser(bpm, mUser)) {

                            sendNotification(context, "Patient is at Risk", "Bpm:" + bpm + " Please contact doctor urgently!");
                            sendMessagesAndCallDoctor(context, mUser);
                        }
                    } catch (NumberFormatException nfe) {

                        sendNotification(context, "Bluetooth Error", "Bpm:" + bpm + " Please setup bluetooth device properly");

                        Toast.makeText(context, "Invalid data from bluetooth", Toast.LENGTH_SHORT).show();
                    }
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                connectedThread.cancel();


            } catch (NullPointerException e) {
                e.printStackTrace();
                connectedThread.cancel();


            } catch (IOException e) {
                e.printStackTrace();
            }


            // This is where main activity thread receives messages
            // Put here your handling of incoming messages posted by other threads

            super.handleMessage(msg);
        }

    };

}
