package com.example.pickle.activity.Main.ProductsFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pickle.Adapters.CategoryRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class VegetableFragment extends Fragment {

    private ArrayList<ProductModel> _productList;
    private ArrayList<ProductModel> _cartList;
    private CategoryRecyclerViewAdapter adapter;

    public VegetableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vegetable, container, false);

        _productList = new ArrayList<>();

        init_recyclerView(view);
        populateList();


        return view;
    }


    private void init_recyclerView(View view) {
        RecyclerView _vegetableRecyclerView = view.findViewById(R.id.vegetable_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        adapter = new CategoryRecyclerViewAdapter(getActivity(), _productList, "Vegetables");
        _vegetableRecyclerView.setLayoutManager(linearLayoutManager);
        _vegetableRecyclerView.setAdapter(adapter);
    }

    private void populateList() {

        String cartProducts = SharedPrefsUtils.getStringPreference(getActivity(), "Vegetables", 0);
        ProductModel[] productModels = new Gson().fromJson(cartProducts, ProductModel[].class);

        if (productModels != null) {
            _cartList = new ArrayList<>(Arrays.asList(productModels));
        } else {
            _cartList = new ArrayList<>();
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products/Vegetables");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot m : dataSnapshot.getChildren()) {
                    if (m.exists()) {
                        Log.e("Fruit recycler view ", m +" ");
                        ProductModel model = m.getValue(ProductModel.class);

                        if (_cartList != null) {
                            for (ProductModel pm : _cartList) {
                                if (model.getItemId().equals(pm.getItemId())) {
                                    model.setQuantityCounter(pm.getQuantityCounter());
                                }
                            }
                        }

                        _productList.add(model);
                        adapter.notifyDataSetChanged();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
