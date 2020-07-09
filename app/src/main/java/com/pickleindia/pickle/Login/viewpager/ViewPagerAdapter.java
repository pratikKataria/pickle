package com.pickleindia.pickle.Login.viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pickleindia.pickle.utils.Constant;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private static final int LAYOUT_COUNT = 3;
    private boolean updateAddress;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle, boolean updateAddress) {
        super(fragmentManager, lifecycle);
        this.updateAddress = updateAddress;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = ApartmentFragment.newInstance(Constant.APARTMENT, updateAddress);
                return fragment;
            case 1:
                fragment = ApartmentFragment.newInstance(Constant.INDIVIDUAL, updateAddress);
                return  fragment;
            case 2:
                fragment = CurrentLocationFragment.newInstance(updateAddress);
                return fragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return LAYOUT_COUNT;
    }

}
