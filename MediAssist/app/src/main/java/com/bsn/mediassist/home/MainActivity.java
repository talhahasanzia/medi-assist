package com.bsn.mediassist.home;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bsn.mediassist.R;
import com.bsn.mediassist.customviews.CustomViewPager;
import com.bsn.mediassist.data.User;
import com.bsn.mediassist.ecg.EcgActivity;
import com.bsn.mediassist.monitoring.RepeatingReceiver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    ArrayAdapter<String> arrayAdapter;

    Set<BluetoothDevice> pairedDevices = null;

    BluetoothAdapter bluetoothAdapter;

    private PendingIntent pendingIntent;

    String baseURL = "/users/";

    public User user;

    public static final int alarm_code = 345;

    @BindView(R.id.viewpager)
    CustomViewPager viewPager;

    private FirebaseAuth mAuth;

    UpdateOnUser updateOnUser;

    BottomNavigationView navigation;

    boolean isUserSignedIn = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (isUserSignedIn)
                        viewPager.setCurrentItem(0, true);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1, true);
                    return true;

            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {

            isUserSignedIn = true;
            if (haveNetworkConnection(this))
                getUserData();


        } else {

            isUserSignedIn = false;
        }


        viewPager.setPagingEnabled(false);


        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    navigation.setSelectedItemId(R.id.navigation_home);
                else
                    navigation.setSelectedItemId(R.id.navigation_dashboard);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (!isUserSignedIn)
            viewPager.setCurrentItem(1);


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    public class PagerAdapter extends FragmentStatePagerAdapter {


        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                HomeFragment homeFragment = new HomeFragment();
                updateOnUser = homeFragment;
                return homeFragment;
            } else
                return new DashboardFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    void setUpRepeatingAlarm(User user) {

          /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(MainActivity.this, RepeatingReceiver.class);
        alarmIntent.putExtras(user.toBundle());
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, alarm_code, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        manager.cancel(pendingIntent);


        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, alarm_code, alarmIntent, 0);


        int interval = 1000 * 60;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

    }


    void getUserData() {


        if (mAuth != null && mAuth.getCurrentUser() != null) {

            // Get a reference to our posts
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference ref = database.getReference(baseURL + mAuth.getCurrentUser().getUid());

            // final ProgressDialog progress = ProgressDialog.show(this, "Getting data", "Please wait");
            // progress.setIndeterminate(true);
            //  progress.show();

            // Attach a listener to read the data at our posts reference
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {

                        SharedPreferences prefs = MainActivity.this.getSharedPreferences(
                                "com.example.app", Context.MODE_PRIVATE);


                        String address = prefs.getString("BT", null);

                        if (address != null) {
                            if (bluetoothAdapter.isEnabled()) {

                           /* IntentFilter filter = new IntentFilter();
                            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
                            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                            registerReceiver(mReceiver, filter);

*/
                                setUpRepeatingAlarm(user);


                                if (updateOnUser != null)
                                    updateOnUser.update(user);


                            } else {
                                createAndShowAlertDialog();

                            }

                        } else {
                            createAndShowAlertDialog();

                        }
                    }

                    //  progress.dismiss();

                    ref.removeEventListener(this);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    // progress.dismiss();
                    ref.removeEventListener(this);
                }
            });


        } else {


            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        Intent alarmIntent = new Intent(MainActivity.this, RepeatingReceiver.class);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, alarm_code, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        manager.cancel(pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();


        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    isUserSignedIn = true;
                    firebaseAuth.getCurrentUser().reload();
                } else {
                    isUserSignedIn = false;
                }


                PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

                viewPager.setAdapter(pagerAdapter);


                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position == 0)
                            navigation.setSelectedItemId(R.id.navigation_home);
                        else
                            navigation.setSelectedItemId(R.id.navigation_dashboard);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                if (!isUserSignedIn)
                    viewPager.setCurrentItem(1);


                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS}, 99);
        }


    }

    boolean doItOnce = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 99) {

            for (int result : grantResults) {


                if (result == PackageManager.PERMISSION_DENIED) {

                    if (!doItOnce) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS}, 99);

                        doItOnce = true;
                    }

                }
            }


        }

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

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                createAndShowAlertDialog();
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Toast.makeText(context, "device is connected!", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                createAndShowAlertDialog();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                createAndShowAlertDialog();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

            }

        }
    };

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect to Bluetooth?" +
                "");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(MainActivity.this, EcgActivity.class));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
