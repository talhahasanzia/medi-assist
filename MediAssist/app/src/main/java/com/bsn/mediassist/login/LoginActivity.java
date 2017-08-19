package com.bsn.mediassist.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bsn.mediassist.R;
import com.bsn.mediassist.main.MainActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    @OnClick({R.id.login_button})
    public void onClick(View v) {

        authenticate();
    }

    private void authenticate() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
    }


}
