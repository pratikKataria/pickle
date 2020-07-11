package com.pickleindia.pickle.reward;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

        final ObservableInt rewardPriceObserver = new ObservableInt(0);

        fragmentRewardBinding.setRewardEarned(rewardPriceObserver);

        fragmentRewardBinding.inviteMaterialButton.setOnClickListener(n -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).createReferLink();
            }
        });

        fragmentRewardBinding.closeImageButton.setOnClickListener(n -> {
            getActivity().onBackPressed();
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customers")
                .child(FirebaseAuth.getInstance().getUid())
                .child("referralReward/pcoins");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(RewardFragment.class.getName(), snapshot+"");
                int rewardPrice = snapshot.getValue(Integer.class);
                rewardPriceObserver.set(rewardPrice);
                fragmentRewardBinding.executePendingBindings();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "error Loading data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(RewardFragment.class.getName(), error.getMessage());
            }
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