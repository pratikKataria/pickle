package com.example.pickle.activity.Main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.pickle.Adapters.AutoCompleteAdapter;
import com.example.pickle.Adapters.FirebaseSearchRecyclerAdapter;
import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class FirebaseSearchActivity extends AppCompatActivity {

    private RecyclerView _searchRecyclerView;
    private AutoCompleteTextView _autoCompleteTextView;
    private ArrayList<String> searchArrayList;
    private ValueEventListener valueEventListener;

    FirebaseSearchRecyclerAdapter recyclerAdapter;
    ArrayList<ProductModel> testArrayList;
    LinkedHashMap<String, ProductModel> productMap;
    private DatabaseReference keysRef;

    private void init_fields() {
        _autoCompleteTextView = findViewById(R.id.edit_query);
        searchArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.vegetables)));
        testArrayList = new ArrayList<>();
        productMap = new LinkedHashMap<>();
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
                    firebaseSearch(s.toString().toLowerCase());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        _searchRecyclerView = findViewById(R.id.searchRecyclerView);
        _searchRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        _searchRecyclerView.setHasFixedSize(true);
        recyclerAdapter = new FirebaseSearchRecyclerAdapter(this, testArrayList, 0);
        _searchRecyclerView.setAdapter(recyclerAdapter);
    }

    private void firebaseSearch(String search) {
        testArrayList.clear();
        keysRef = FirebaseDatabase.getInstance().getReference("ProductCategories");
        Query keyQuery = keysRef.orderByKey();
        valueEventListener = keyQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products/" + dataSnapshotChild.getValue(String.class));
                    Query query = reference.orderByChild("itemName").startAt(search).endAt(search + "\uf8ff").limitToFirst(15);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.exists()) {
                                    ProductModel productModel = snapshot.getValue(ProductModel.class);
                                    if (!testArrayList.contains(productModel)) {
                                        testArrayList.add(snapshot.getValue(ProductModel.class));
                                        recyclerAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(FirebaseSearchActivity.this, "error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FirebaseSearchActivity.this, "error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (keysRef != null && valueEventListener != null) {
            keysRef.removeEventListener(valueEventListener);
        }
    }

}