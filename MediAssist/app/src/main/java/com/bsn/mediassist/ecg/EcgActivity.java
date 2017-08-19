package com.bsn.mediassist.ecg;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.bsn.mediassist.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EcgActivity extends AppCompatActivity {


    @BindView(R.id.view_pager)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        ButterKnife.bind(this);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                return true;
            }
        });


        ECGPager ecgPager = new ECGPager(getSupportFragmentManager());


        viewPager.setAdapter(ecgPager);



    }


    public class ECGPager extends FragmentStatePagerAdapter {


        public ECGPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:

                    return new BluetiithEnableFragment();

                case 1:
                    return new ConnectDeviceFragment();


                case 2:
                    return new EcgInputFragment();

                default:
                    return new BluetiithEnableFragment();

            }
        }


        @Override
        public int getCount() {
            return 3;
        }


    }
}
