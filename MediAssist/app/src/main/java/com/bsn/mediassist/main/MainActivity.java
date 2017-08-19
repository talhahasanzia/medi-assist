package com.bsn.mediassist.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bsn.mediassist.R;
import com.bsn.mediassist.ecg.EcgActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @OnClick(R.id.ecg_button)
    public void onClick(View v) {

        startActivity(new Intent(MainActivity.this, EcgActivity.class));


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }


}
