package com.bsn.mediassist.ecg;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsn.mediassist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcgInputFragment extends Fragment {


    public EcgInputFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ecg_input, container, false);
    }

}
