package com.bsn.mediassist.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bsn.mediassist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private EditText mEmailField;
    private EditText mPasswordField;

    Button signInButton, createNewAccButton;


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        signInButton = (Button) findViewById(R.id.email_sign_in_button);
        createNewAccButton = (Button) findViewById(R.id.email_create_account_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        });


        createNewAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class), 234);
            }
        });

        mAuth = FirebaseAuth.getInstance();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 234) {

            if (resultCode == RESULT_OK) {


                if(data.getBooleanExtra("result", false))
                {Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                finish();}
                else
                {

                    Toast.makeText(this, "Sign Up Not Successfull!", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser currentUser = mAuth.getCurrentUser();


    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {

          Toast.makeText(this, "Already signed in.", Toast.LENGTH_SHORT).show();
            finish();



        } else {



        }

    }

    private void signIn(String email, String password) {
        Log.d("Login", "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Failed to authenticate!", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
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


    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

}
