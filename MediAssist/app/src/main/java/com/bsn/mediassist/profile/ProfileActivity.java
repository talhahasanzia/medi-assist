package com.bsn.mediassist.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bsn.mediassist.R;
import com.bsn.mediassist.data.User;
import com.bsn.mediassist.emergencycalls.EmergencyCallActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class ProfileActivity extends AppCompatActivity {


    @BindView(R.id.name_textview)
    TextView nameText;

    @BindView(R.id.age_textview)
    TextView ageText;

    @BindView(R.id.gender_textview)
    TextView genderText;

    @BindView(R.id.athlete_textview)
    TextView athleteText;

    @BindView(R.id.bloodpressure_textview)
    TextView pressureText;


    @BindView(R.id.contact_details)
    Button contactButton;

    FirebaseAuth mAuth;

    FirebaseUser user;

    String baseURL = "/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);


    }


    @Override
    protected void onResume() {
        super.onResume();

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getInstance().getCurrentUser();


        if (mAuth != null && mAuth.getCurrentUser() != null) {

            // Get a reference to our posts
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference(baseURL + mAuth.getCurrentUser().getUid());

            final ProgressDialog progress = ProgressDialog.show(this, "Syncing", "Please wait");
            progress.setIndeterminate(true);
            progress.show();

            // Attach a listener to read the data at our posts reference
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {


                        nameText.setText(user.name);
                        ageText.setText(user.age);
                        genderText.setText(user.gender);
                        athleteText.setText(user.isAthlete);
                        pressureText.setText(user.isBloodPressure);


                        contactButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ProfileActivity.this, EmergencyCallActivity.class));
                            }
                        });


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

        }
    }
}
