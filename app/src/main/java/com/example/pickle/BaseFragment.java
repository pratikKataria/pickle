package com.example.pickle;

// PERSISTENT ROOT VIEW

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    public boolean hasInitializedRootView = false;
    private View rootView;

    public View getPersistentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layout) {
        if (rootView == null) {
            // Inflate the layout for this fragment
            rootView = inflater.inflate(layout, null);
        } else {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove rootView from the existing parent view group
            // (it will be added back).
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        return rootView;
    }
}