package com.example.pickle.address_book;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pickle.Login.CustomerDetailActivity;
import com.example.pickle.R;
import com.example.pickle.databinding.FragmentAddressBookBinding;


public class AddressBookFragment extends Fragment {

    private AddressBookViewModel addressBookViewModel;
    private FragmentAddressBookBinding fragmentAddressBookBinding;

    public AddressBookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAddressBookBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_address_book,
                container,
                false
        );

        fragmentAddressBookBinding.setActivity(getActivity());

        addressBookViewModel = new ViewModelProvider(this).get(AddressBookViewModel.class);
        loadAddress();

        fragmentAddressBookBinding.includeSlot.cardView.setOnClickListener(n -> startActivity(new Intent(getActivity(), CustomerDetailActivity.class).putExtra("UPDATE_ADDRESS", true)));

        fragmentAddressBookBinding.swipeToRefresh.setOnRefreshListener(() -> {
            loadAddress();
            if (fragmentAddressBookBinding.swipeToRefreshText.getVisibility() == View.VISIBLE)
                fragmentAddressBookBinding.swipeToRefreshText.setVisibility(View.GONE);
        });

        return fragmentAddressBookBinding.getRoot();
    }

    private void loadAddress() {
        addressBookViewModel.getAddressFromFirebaseDatabase();
        addressBookViewModel.getUserAddressLiveData().observe(getViewLifecycleOwner(), address -> {
            fragmentAddressBookBinding.setAddress(address.toString());
            fragmentAddressBookBinding.swipeToRefresh.setRefreshing(false);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentAddressBookBinding.swipeToRefreshText.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            if (fragmentAddressBookBinding != null) {
                fragmentAddressBookBinding.swipeToRefreshText.setVisibility(View.GONE);
            }
        }, 8000);
    }
}