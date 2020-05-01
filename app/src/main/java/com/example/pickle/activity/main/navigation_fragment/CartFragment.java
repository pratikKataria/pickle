package com.example.pickle.activity.main.navigation_fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pickle.Adapters.CartRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CartFragment extends Fragment {

    private ArrayList<ProductModel> cartList;
    private TextView _textViewCartTotal;
    private TextView _textViewDiscount;
    private TextView _textViewTotal;

    RecyclerView _cartRecyclerView;

    private BottomAppBar _bottomAppBar;
    private BottomSheetBehavior _bottomSheetBehavior;
    private LinearLayout _bottomSheet;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

//        _textViewCartTotal = view.findViewById(R.id.cartFragmentTextTotal);
//        _textViewDiscount = view.findViewById(R.id.cartFragmentDiscount);
//        _textViewTotal = view.findViewById(R.id.cartFragmentTotal);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, (float)5.0);

        _cartRecyclerView = view.findViewById(R.id.cartRecyclerView);

        _bottomAppBar = view.findViewById(R.id.bottomAppBar);

        _bottomSheet = view.findViewById(R.id.bottomSheet);

        _bottomSheetBehavior = BottomSheetBehavior.from(_bottomSheet);

        _bottomAppBar.setNavigationOnClickListener(v -> {
            _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            _cartRecyclerView.setLayoutParams(params);
        });

        _bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                params.weight = 9 - slideOffset;
                _cartRecyclerView.setLayoutParams(params);
            }
        });

        cartList = new ArrayList<>();
        populateList();
        cartPrice();
        init_recyclerView(view);
        return view;
    }

    private void init_recyclerView(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        CartRecyclerViewAdapter adapter = new CartRecyclerViewAdapter(getActivity(), cartList);
        _cartRecyclerView.setLayoutManager(linearLayoutManager);
        _cartRecyclerView.setAdapter(adapter);
    }

    private void populateList() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences != null) {
            Map<String, ?> allEntries = preferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                String list = SharedPrefsUtils.getStringPreference(getContext(), entry.getKey(), 0);
                ProductModel[] models = new Gson().fromJson(list, ProductModel[].class);

                if (list != null && models != null) {
                    cartList.addAll(Arrays.asList(models));
                }
            }

        }

    }

    private void cartPrice() {
        int price = 0;
        for (ProductModel model : cartList) {
            price += model.getItemBasePrice();
        }

//        _textViewCartTotal.setText(Integer.toString(price));
    }

}
