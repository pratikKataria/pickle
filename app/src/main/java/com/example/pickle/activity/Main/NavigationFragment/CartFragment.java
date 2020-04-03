package com.example.pickle.activity.Main.NavigationFragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pickle.Adapters.CartRecyclerViewAdapter;
import com.example.pickle.Adapters.CategoryRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.example.pickle.data.SharedPrefsUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CartFragment extends Fragment {

    RecyclerView fruitsRecyclerView;
    ArrayList<ProductModel> cartList;
    CartRecyclerViewAdapter adapter;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartList = new ArrayList<>();
        populateList();
        init_recyclerView(view);
        return view;
    }

    private void init_recyclerView(View view) {
        fruitsRecyclerView = view.findViewById(R.id.cartRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        adapter = new CartRecyclerViewAdapter(getActivity(), cartList);
        fruitsRecyclerView.setLayoutManager(linearLayoutManager);
        fruitsRecyclerView.setAdapter(adapter);
    }

    private void populateList() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferences != null) {
            Map<String, ?> allEntries = preferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                String list = SharedPrefsUtils.getStringPreference(getContext(), entry.getKey(), 0);
                ProductModel[] models = new Gson().fromJson(list, ProductModel[].class);

                if (list != null && models != null) {
                    Toast.makeText(getContext(), " not emtpy" , Toast.LENGTH_SHORT).show();
                    cartList.addAll(Arrays.asList(models));
                    Toast.makeText(getContext(), " not emtpy" + cartList.size() , Toast.LENGTH_SHORT).show();
                }
            }

        }

    }


}
