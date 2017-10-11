package com.bsn.mediassist.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bsn.mediassist.R;
import com.bsn.mediassist.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {


    private static final String TAG = "SIGN_UP";
    @BindView(R.id.signup_email)
    EditText emailText;

    @BindView(R.id.signup_password)
    EditText passText;


    @BindView(R.id.signup_age)
    EditText ageText;

    @BindView(R.id.signup_doctor_name)
    EditText docNameText;

    @BindView(R.id.signup_doctor_number)
    EditText docNumText;

    @BindView(R.id.signup_relative1_name)
    EditText relative1NameText;

    @BindView(R.id.signup_relative1_number)
    EditText relative1NumText;

    @BindView(R.id.signup_relative2_name)
    EditText relative2NameText;

    @BindView(R.id.signup_relative2_number)
    EditText relative2NumText;

    @BindView(R.id.signup_name)
    EditText nameText;


    @BindView(R.id.signup_male_radio)
    RadioButton maleRadioButton;

    @BindView(R.id.signup_athlete_checkbox)
    CheckBox athleteCheckbox;


    @BindView(R.id.signup_blood_p_checkbox)
    CheckBox bloodPressureCheckBox;


    @OnClick(R.id.sign_up_create_account_button)
    public void OnClick(View v) {


        if (haveNetworkConnection())
            createAccount(emailText.getText().toString(), passText.getText().toString());
        else
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();

    }


    boolean successfulSignUp;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);


        mAuth = FirebaseAuth.getInstance();

    }


    private void createAccount(String email, String password) {

        if (!validateForm()) {
            return;
        }

        final String name = nameText.getText().toString();
        final String age = ageText.getText().toString();
        final String gender = maleRadioButton.isChecked() ? "Male" : "Female";
        final String bloodPressure = bloodPressureCheckBox.isChecked() ? "Yes" : "No";
        final String athlete = athleteCheckbox.isChecked() ? "Yes" : "No";
        final String doctorName = docNameText.getText().toString();
        final String doctorNum = docNumText.getText().toString();
        final String r1Name = relative1NameText.getText().toString();
        final String r1Num = relative1NumText.getText().toString();
        final String r2Name = relative2NameText.getText().toString();
        final String r2Num = relative2NumText.getText().toString();

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = authResult.getUser();


                        user.sendEmailVerification();
                        // set data here

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("users");


                        final User userData = new User(
                                age, doctorName, doctorNum, gender, athlete, bloodPressure, name, r1Name,
                                r2Name, r1Num, r2Num,"none"

                        );

                        myRef.child(user.getUid()).setValue(userData);


                        myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                User user = dataSnapshot.getValue(User.class);

                                if (user != null) {
                                    Intent returnIntent = new Intent();
                                    successfulSignUp = true;
                                    Toast.makeText(SignUpActivity.this, "Welcome, " + user.name, Toast.LENGTH_SHORT).show();
                                    returnIntent.putExtra("result", successfulSignUp);
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    hideProgressDialog();
                                    finish();
                                } else {
                                    successfulSignUp = false;
                                    Toast.makeText(SignUpActivity.this, "Authentication failed. User data was not saved.",
                                            Toast.LENGTH_SHORT).show();
                                    successfulSignUp = false;
                                    hideProgressDialog();
                                }

                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                successfulSignUp = false;
                                Toast.makeText(SignUpActivity.this, "Authentication failed. Process got canceled",
                                        Toast.LENGTH_SHORT).show();
                                successfulSignUp = false;
                                hideProgressDialog();
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        successfulSignUp = false;
                        Toast.makeText(SignUpActivity.this, "Authentication failed. There was error processing the request.",
                                Toast.LENGTH_SHORT).show();
                        successfulSignUp = false;
                        hideProgressDialog();
                    }
                });


        ;
        // [END create_user_with_email]
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", successfulSignUp);
        setResult(Activity.RESULT_OK, returnIntent);
    }

    boolean validateForm() {

        boolean isValid = true;

        String email = emailText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailText.setError("Required.");
            isValid = false;
        } else {

            Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

            if (pattern.matcher(email).matches()) {
                emailText.setError(null);


            } else {
                emailText.setError("Invalid email");

                isValid = false;

            }

        }

        String password = passText.getText().toString();
        if (TextUtils.isEmpty(password)) {

            passText.setError("Required.");
            isValid = false;

        } else {


            passText.setError(null);
        }

        if (TextUtils.isEmpty(nameText.getText().toString())) {

            nameText.setError("Required.");
            isValid = false;

        } else {


            nameText.setError(null);
        }

        if (TextUtils.isEmpty(ageText.getText().toString())) {

            ageText.setError("Required.");

            isValid = false;

        } else {

            if (TextUtils.isDigitsOnly(ageText.getText().toString()))

                ageText.setError(null);
            else {
                ageText.setError("Invalid Age");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(docNameText.getText().toString())) {

            docNameText.setError("Required.");
            isValid = false;

        } else {


            docNameText.setError(null);
        }

        if (TextUtils.isEmpty(docNumText.getText().toString())) {

            docNumText.setError("Required.");

            isValid = false;

        } else {


            Pattern pattern = Patterns.PHONE;
            if (pattern.matcher(docNumText.getText().toString()).matches())

                docNumText.setError(null);
            else {
                docNumText.setError("Invalid Phone Number");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(relative1NameText.getText().toString())) {

            relative1NameText.setError("Required.");
            isValid = false;

        } else {


            relative1NameText.setError(null);
        }

        if (TextUtils.isEmpty(relative1NumText.getText().toString())) {

            relative1NumText.setError("Required.");

            isValid = false;

        } else {


            Pattern pattern = Patterns.PHONE;
            if (pattern.matcher(relative1NumText.getText().toString()).matches())

                relative1NumText.setError(null);
            else {
                relative1NumText.setError("Invalid Phone Number");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(relative2NameText.getText().toString())) {

            relative2NameText.setError("Required.");
            isValid = false;

        } else {


            relative2NameText.setError(null);
        }

        if (TextUtils.isEmpty(relative2NumText.getText().toString())) {
            relative2NumText.setError("Required.");

            isValid = false;
        } else {


            Pattern pattern = Patterns.PHONE;
            if (pattern.matcher(relative2NumText.getText().toString()).matches())

                relative2NumText.setError(null);
            else {
                relative2NumText.setError("Invalid Phone Number");
                isValid = false;
            }
        }


        return isValid;
    }


    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

}
