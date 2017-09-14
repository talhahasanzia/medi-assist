package com.bsn.mediassist.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.bsn.mediassist.R;
import com.bsn.mediassist.customviews.CustomViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    CustomViewPager viewPager;

    private FirebaseAuth mAuth;


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



        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {

            isUserSignedIn = true;

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
            if (position == 0)
                return new HomeFragment();
            else
                return new DashboardFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                recreate();
            }
        });


    }
}