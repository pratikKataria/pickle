package com.example.pickle.activity.main.products_fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.FragmentVegetableBinding;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class VegetableFragment extends Fragment {

    private ArrayList<ProductModel> _productList;
    private ChildEventListener childEventListener;
    private DatabaseReference reference;
    private FragmentVegetableBinding binding;


    public VegetableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_vegetable,
                container,
                false
        );

        _productList = new ArrayList<>();
        binding.setProductList(_productList);
        new Handler().postDelayed(this::populateList,1500);

        return binding.getRoot();
    }

    private void populateList() {
        reference = FirebaseDatabase.getInstance().getReference("Products/Vegetables");
        Query query = reference.orderByChild("itemName").limitToFirst(15);

        childEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    ProductModel product = dataSnapshot.getValue(ProductModel.class);

                    String cartProductJson = SharedPrefsUtils.getStringPreference(getContext(), product.getItemId(), 0);
                    ProductModel cartProduct = new Gson().fromJson(cartProductJson, ProductModel.class);

                    if (product.equals(cartProduct))
                        product.setQuantityCounter(cartProduct.getQuantityCounter());

                    _productList.add(product);
                    notifyChanges();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int position = 0;
                Iterator<ProductModel> iterator = _productList.iterator();
                while (iterator.hasNext()) {
                    position++;
                    ProductModel product = iterator.next();
                    ProductModel newProduct = dataSnapshot.getValue(ProductModel.class);
                    if (product.equals(newProduct)) {
                        iterator.remove();
                        break;
                    }
                }

                //set stored cart quantity
                ProductModel newProduct = dataSnapshot.getValue(ProductModel.class);
                if (newProduct != null) {

                    //on item changed get default saved product and set saved quantity counted to the new product
                    String cartProductJson = SharedPrefsUtils.getStringPreference(getContext(), newProduct.getItemId(), 0);
                    ProductModel cartProduct = new Gson().fromJson(cartProductJson, ProductModel.class);

                    if (newProduct.equals(cartProduct))
                        newProduct.setQuantityCounter(cartProduct.getQuantityCounter());
                    //closed

                    if ((position - 1) <= _productList.size()) {
                        _productList.add(position - 1, newProduct);
                        if (binding.vegetableRecyclerView.getAdapter() != null)
                            notifyChanges();
                    } else {
                        _productList.add(newProduct);
                        notifyChanges();
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

    private void notifyChanges() {
        try {
            binding.vegetableRecyclerView.getAdapter().notifyDataSetChanged();
        } catch (NullPointerException npe) {
            Log.e("FruitFragment", "npe exception " + npe.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (childEventListener != null) {
            reference.removeEventListener(childEventListener);
        }
    }

}
