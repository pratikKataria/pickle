package com.example.pickle.activity.Main.ProductsFragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.Adapters.CategoryRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class VegetableFragment extends Fragment {

    private ArrayList<ProductModel> _productList;
    private ArrayList<ProductModel> _cartList;
    private CategoryRecyclerViewAdapter adapter;
    private ChildEventListener childEventListener;
    private DatabaseReference reference;


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

        reference = FirebaseDatabase.getInstance().getReference("Products/Vegetables");
        Query query = reference.orderByChild("itemName").limitToFirst(15);

        childEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Log.e("Fruit recycler view ", dataSnapshot + " ");
                    ProductModel newProduct = dataSnapshot.getValue(ProductModel.class);
                    if (_cartList != null) {
                        for (ProductModel pm : _cartList) {
                            if (newProduct.getItemId().equals(pm.getItemId())) {
                                newProduct.setQuantityCounter(pm.getQuantityCounter());
                            }
                        }
                    }

                    _productList.add(newProduct);
                    adapter.notifyDataSetChanged();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int position = 0;
                Log.e("onChildChanged", "key s " + s + " datasnaphot" + dataSnapshot);
                Iterator<ProductModel> iterator = _productList.iterator();
                while (iterator.hasNext()) {
                    position++;
                    ProductModel product = iterator.next();
                    ProductModel newProduct = dataSnapshot.getValue(ProductModel.class);
                    if (newProduct != null && product.getItemId().equals(newProduct.getItemId())) {
                        iterator.remove();
                        break;
                    }
                }

                //set stored cart quantity
                ProductModel newProduct = dataSnapshot.getValue(ProductModel.class);
                if (newProduct != null) {
                    for (ProductModel product : _cartList) {
                        if (product.getItemId().equals(newProduct.getItemId())) {
                            newProduct.setQuantityCounter(product.getQuantityCounter());
                        }
                    }
                    if ((position - 1) <= _productList.size()) {
                        _productList.add(position - 1, newProduct);
                        adapter.notifyDataSetChanged();
                    } else {
                        _productList.add(newProduct);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().postDelayed(this::populateList,1200);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (childEventListener != null) {
            reference.removeEventListener(childEventListener);
        }
    }

}
