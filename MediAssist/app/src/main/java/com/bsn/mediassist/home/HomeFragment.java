package com.bsn.mediassist.home;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsn.mediassist.R;
import com.bsn.mediassist.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    boolean isUserSignedIn = false;


    FirebaseUser user;

    Dialog dialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        isUserSignedIn = ((MainActivity) getActivity()).isUserSignedIn;

    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        return v;
    }


    @Override
    public void onPause() {
        super.onPause();

        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
