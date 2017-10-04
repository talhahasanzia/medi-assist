package com.bsn.mediassist.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bsn.mediassist.R;
import com.bsn.mediassist.emergencycalls.EmergencyCallActivity;
import com.bsn.mediassist.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    @BindView(R.id.login_button)
    TextView loginTextView;

    @BindView(R.id.dashboard_layout)
    LinearLayout dashBoardLayout;

    @BindView(R.id.verify_email_layout)
    LinearLayout verifyEmailLayout;


    @BindView(R.id.send_again_text)
    TextView sendAgainText;

    @OnClick({R.id.logout_text, R.id.emergency_call})
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.logout_text:

                mAuth.signOut();
                dashBoardLayout.setVisibility(GONE);
                loginTextView.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_SHORT).show();
                break;

            case R.id.emergency_call:

                startActivity(new Intent(getActivity(), EmergencyCallActivity.class));

                break;

        }


    }

    boolean isUserSignedIn = false;

    FirebaseAuth mAuth;

    FirebaseUser user;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        isUserSignedIn = ((MainActivity) getActivity()).isUserSignedIn;

    }

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getInstance().getCurrentUser();


        ButterKnife.bind(this, v);


        verifyEmailLayout.setVisibility(View.GONE);

        if (user != null) {

            isUserSignedIn = true;

        } else {

            isUserSignedIn = false;
        }


        if (!isUserSignedIn) {
            loginTextView.setVisibility(View.VISIBLE);
            dashBoardLayout.setVisibility(GONE);

        } else {
            loginTextView.setVisibility(View.GONE);
            dashBoardLayout.setVisibility(View.VISIBLE);

        }


        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });


        return v;
    }

}
