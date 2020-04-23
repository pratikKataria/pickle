package com.example.pickle.viewpager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pickle.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndividualHouse extends Fragment {

    public IndividualHouse() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_individual_house, container, false);
    }
}
