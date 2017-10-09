package com.bsn.mediassist.home;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bsn.mediassist.R;
import com.bsn.mediassist.data.User;
import com.bsn.mediassist.emergencycalls.EmergencyCallActivity;
import com.bsn.mediassist.login.LoginActivity;
import com.bsn.mediassist.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    @BindView(R.id.name_text)
    TextView nameText;


    @BindView(R.id.send_again_text)
    TextView sendAgainText;

    @BindView(R.id.profile_textview)
    TextView profileText;


    String baseURL = "/users/";

    DatabaseReference ref;

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
        ButterKnife.bind(this, v);

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();


        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getInstance().getCurrentUser();


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
            profileText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                }
            });

        }


        if (mAuth != null && mAuth.getCurrentUser() != null) {

            // Get a reference to our posts
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
           ref= database.getReference(baseURL + mAuth.getCurrentUser().getUid());

            final ProgressDialog progress = ProgressDialog.show(getActivity(), "Syncing", "Please wait");
            progress.setIndeterminate(true);
            progress.show();

            // Attach a listener to read the data at our posts reference
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {


                        nameText.setText("Hi, " + user.name);
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


            Toast.makeText(getActivity(), "No user logged in.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
