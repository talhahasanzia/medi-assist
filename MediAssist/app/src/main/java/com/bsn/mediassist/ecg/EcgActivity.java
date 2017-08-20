package com.bsn.mediassist.ecg;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.bsn.mediassist.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EcgActivity extends AppCompatActivity {


    public BluetoothAdapter mBluetoothAdapter;

    public BluetoothDevice currentBluetoothDevice;

    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;


    int currentScreen = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        ButterKnife.bind(this);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Slide slideTransition = new Slide(Gravity.RIGHT);
        slideTransition.setDuration(1000);
        BluetoothEnableFragment bluetoothEnableFragment = new BluetoothEnableFragment();
        bluetoothEnableFragment.setEnterTransition(slideTransition);
        setCurrentScreen(bluetoothEnableFragment);

    }


    public void setCurrentScreen(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();


    }
}
