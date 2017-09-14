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

    @OnClick({R.id.logout_image, R.id.logout_text, R.id.logout_view})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.logout_image:
            case R.id.logout_text:
            case R.id.logout_view:
                mAuth.signOut();
                getActivity().recreate();
                Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_SHORT).show();
                break;

        }


    }

    boolean isUserSignedIn = false;

    FirebaseAuth mAuth;

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

        ButterKnife.bind(this, v);

        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {

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
