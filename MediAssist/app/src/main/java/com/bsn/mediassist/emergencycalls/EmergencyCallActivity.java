package com.bsn.mediassist.emergencycalls;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bsn.mediassist.R;
import com.bsn.mediassist.data.User;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmergencyCallActivity extends AppCompatActivity {


    @BindView(R.id.doc_num)
    EditText docNum;

    @BindView(R.id.doc_call)
    ImageView docCall;

    @BindView(R.id.doc_msg)
    ImageView docMsg;


    @BindView(R.id.r1_num)
    EditText r1Num;

    @BindView(R.id.r1_call)
    ImageView r1Call;

    @BindView(R.id.r1_msg)
    ImageView r1Msg;


    @BindView(R.id.r2_num)
    EditText r2Num;

    @BindView(R.id.r2_call)
    ImageView r2Call;

    @BindView(R.id.r2_msg)
    ImageView r2Msg;


    @BindView(R.id.edit_button)
    Button editButton;


    @BindView(R.id.save_button)
    Button saveButton;

    FirebaseAuth auth;

    String baseURL = "/users/";

    User user;


    FusedLocationProviderClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_call);

        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();

        client = LocationServices.getFusedLocationProviderClient(EmergencyCallActivity.this);


        docNum.setEnabled(false);
        r1Num.setEnabled(false);
        r2Num.setEnabled(false);


        if (auth != null && auth.getCurrentUser() != null) {

            // Get a reference to our posts
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference(baseURL + auth.getCurrentUser().getUid());

            final ProgressDialog progress = ProgressDialog.show(this, "Syncing", "Please wait");
            progress.setIndeterminate(true);
            progress.show();

            // Attach a listener to read the data at our posts reference
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {

                        Log.d("USER_DATA", user.toString());


                        docNum.setText(user.doctorNumber);


                        r1Num.setText(user.relativeNumber1);


                        r2Num.setText(user.relativeNumber2);


                        docCall.setOnClickListener(callClickListener);
                        r1Call.setOnClickListener(callClickListener);
                        r2Call.setOnClickListener(callClickListener);


                        docMsg.setOnClickListener(msgClickListener);
                        r1Msg.setOnClickListener(msgClickListener);
                        r2Msg.setOnClickListener(msgClickListener);

                    }
                    progress.dismiss();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    progress.dismiss();
                }
            });


        } else {


            Toast.makeText(this, "User login failed.", Toast.LENGTH_SHORT).show();
            finish();
        }


    }


    View.OnClickListener callClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.doc_call:
                    String posted_by = docNum.getText().toString();

                    String uri = "tel:" + posted_by.trim();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));

                    if (ActivityCompat.checkSelfPermission(EmergencyCallActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(EmergencyCallActivity.this, "Call permission denied!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(intent);
                    break;

                case R.id.r1_call:

                    posted_by = r1Num.getText().toString();

                    uri = "tel:" + posted_by.trim();
                    intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));

                    if (ActivityCompat.checkSelfPermission(EmergencyCallActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(EmergencyCallActivity.this, "Call permission denied!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(intent);
                    break;


                case R.id.r2_call:
                    posted_by = r2Num.getText().toString();

                    uri = "tel:" + posted_by.trim();
                    intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));

                    if (ActivityCompat.checkSelfPermission(EmergencyCallActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(EmergencyCallActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(EmergencyCallActivity.this, "Call permission denied!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        startActivity(intent);
                    } catch (IllegalArgumentException e) {

                        Toast.makeText(EmergencyCallActivity.this, "Number not valid", Toast.LENGTH_SHORT).show();
                        Log.e("EmergencyActivity", "onClick: ", e);

                    }
                    break;
            }

        }
    };


    String phoneNumber = "9999999999";
    String smsBody = "Message from the Medi Assist App";


    View.OnClickListener msgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            switch (v.getId()) {

                case R.id.doc_msg:
                    phoneNumber = docNum.getText().toString();
                    break;

                case R.id.r1_msg:

                    phoneNumber = r1Num.getText().toString();
                    break;


                case R.id.r2_msg:
                    phoneNumber = r2Num.getText().toString();
                    break;
            }

            if (ActivityCompat.checkSelfPermission(EmergencyCallActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(EmergencyCallActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(EmergencyCallActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 99);

            } else {

                requestLocation();
            }


        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 99) {

            for (int result : grantResults) {


                if (result == PackageManager.PERMISSION_DENIED) {

                    Toast.makeText(this, "Location permissions not granted.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            requestLocation();

        }
    }

    void sendSMSWithLocation(Location location) {


        String locationString = location.getLatitude() + "," + location.getLongitude();

        smsBody = "Your patient, " + user.name + ", is at a risk of heart attack, and is here " + locationString + " (location) at the moment.";

        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        // Send a text based SMS
        try {
            smsManager.sendTextMessage(phoneNumber, null, smsBody, null, null);
            Toast.makeText(this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {

            Toast.makeText(EmergencyCallActivity.this, "Number not valid", Toast.LENGTH_SHORT).show();
            Log.e("EmergencyActivity", "onClick: ", e);

        }

        client.removeLocationUpdates(mLocationCallback);

    }


    boolean checkGPS() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            sendSMSWithLocation(locationResult.getLastLocation());
        }
    };


    void requestLocation() {
        try {
            if (checkGPS()) {

                client.requestLocationUpdates(LocationRequest.create(), mLocationCallback, null);

            } else {
                client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.getResult() != null)
                            sendSMSWithLocation(task.getResult());
                        else
                            Toast.makeText(EmergencyCallActivity.this, "Location Services are disabled.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (SecurityException sc) {

        }
    }


}
