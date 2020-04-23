package com.example.pickle.viewpager;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private static final int LAYOUT_COUNT = 3;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = ApartmentFragment.newInstance(1);
                return fragment;
            case 1:
                fragment = new IndividualHouse();
                return fragment;
            case 2:
                fragment = new CurrentLocationFragment();
                return fragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return LAYOUT_COUNT;
    }

}
