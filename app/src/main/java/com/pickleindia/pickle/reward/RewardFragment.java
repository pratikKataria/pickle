package com.pickleindia.pickle.reward;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialSharedAxis;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.databinding.FragmentRewardBinding;
import com.pickleindia.pickle.main.MainActivity;

public class RewardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentRewardBinding fragmentRewardBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reward,
                container,
                false
        );
        setEnterTransition(MaterialSharedAxis.create(MaterialSharedAxis.Z, true));


        fragmentRewardBinding.inviteMaterialButton.setOnClickListener(n -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).createReferLink();
            }
        });

        fragmentRewardBinding.closeImageButton.setOnClickListener(n -> {
            getActivity().onBackPressed();
        });

        return fragmentRewardBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.colorAccent));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().setStatusBarColor(Color.WHITE);
    }
}