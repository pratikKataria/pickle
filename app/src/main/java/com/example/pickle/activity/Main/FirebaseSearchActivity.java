package com.example.pickle.activity.Main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.pickle.Adapters.AutoCompleteAdapter;
import com.example.pickle.Adapters.FirebaseSearchRecyclerAdapter;
import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.example.pickle.utils.CartHandler;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirebaseSearchActivity extends AppCompatActivity {


    private DatabaseReference ref;
    private RecyclerView _searchRecyclerView;
    private FirebaseSearchRecyclerAdapter searchRecyclerAdapter;
    private AutoCompleteTextView _autoCompleteTextView;
    private ArrayList<String> searchArrayList;
    private Map<String, ProductModel> cartMap;

    private void init_fields() {
        cartMap = new HashMap<>();
        cartMap = new CartHandler(this).getCachedProductsMap();
        _autoCompleteTextView = findViewById(R.id.edit_query);
        searchArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.vegetables)));
        ref = FirebaseDatabase.getInstance().getReference("Products");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_search);

        init_fields();
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                searchArrayList
        );

        _autoCompleteTextView.setAdapter(adapter);
        _autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    //todo add firestore search mechanism
                    //todo store shared pref in encrypted format
                    firebaseSearch(s.toString().toLowerCase(), (isFound, query) -> {
                        try {

                            FirebaseRecyclerOptions<ProductModel> options = new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(query, snapshot -> {
                                ProductModel productModel = snapshot.getValue(ProductModel.class);
                                if (productModel != null) {
                                    if (cartMap.containsKey(productModel.getItemId())) {
                                        productModel.setQuantityCounter(cartMap.get(productModel.getItemId()).getQuantityCounter());
                                    }
                                }
                                return productModel != null ? productModel : new ProductModel();
                            }).build();

                            if (cartMap.size() > 0) {
                                searchRecyclerAdapter = new FirebaseSearchRecyclerAdapter(options, FirebaseSearchActivity.this, cartMap);
                                searchRecyclerAdapter.startListening();
                            } else {
                                searchRecyclerAdapter = new FirebaseSearchRecyclerAdapter(options, FirebaseSearchActivity.this);
                                searchRecyclerAdapter.startListening();
                            }
                            _searchRecyclerView.setAdapter(searchRecyclerAdapter);

                        } catch (NullPointerException npe) {
                            Log.e("data found ", npe.getMessage());
                            return;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        _searchRecyclerView = findViewById(R.id.searchRecyclerView);
        _searchRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        _searchRecyclerView.setHasFixedSize(true);


    }

    private void firebaseSearch(String search, OnDataPresentListener onDataPresentListener) {

        DatabaseReference keysRef = FirebaseDatabase.getInstance().getReference("ProductCategories");
        Query keyQuery = keysRef.orderByKey();
        keyQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products/" + dataSnapshotChild.getValue(String.class));
                    Query query = reference.orderByChild("itemName").startAt(search).endAt(search + "\uf8ff").limitToFirst(15);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                onDataPresentListener.found(true, query);
                                Log.e("inner Query ", dataSnapshot + "");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("keyQuery ", "" + databaseError.getMessage());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (searchRecyclerAdapter != null)
            searchRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (searchRecyclerAdapter != null)
            searchRecyclerAdapter.stopListening();
    }
    interface  OnDataPresentListener {
        void found(boolean isFound, Query query);
    }

}