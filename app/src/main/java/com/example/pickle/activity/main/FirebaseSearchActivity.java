package com.example.pickle.activity.main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.pickle.R;
import com.example.pickle.binding.IMainActivity;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.ActivityFirebaseSearchBinding;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class FirebaseSearchActivity extends AppCompatActivity implements IMainActivity {

    private ValueEventListener valueEventListener;

    ArrayList<ProductModel> searchList;
    private DatabaseReference keysRef;
    private ActivityFirebaseSearchBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_firebase_search
        );

        ArrayList<String> searchSuggestionList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.vegetables)));
        searchList = new ArrayList<>();

        binding.setProductModelList(searchList);
        binding.setAutoCompleteList(searchSuggestionList);

        binding.editQuery.addTextChangedListener(new TextWatcher() {
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

    }

    private void firebaseSearch(String search) {
        searchList.clear();
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
                                    ProductModel newProduct = snapshot.getValue(ProductModel.class);
                                    if (!searchList.contains(newProduct)) {
                                        String productStringGson = SharedPrefsUtils.getStringPreference(FirebaseSearchActivity.this, newProduct.getItemId(), 0);
                                        ProductModel cartProduct = new Gson().fromJson(productStringGson, ProductModel.class);
                                        if (cartProduct != null)
                                            newProduct.setQuantityCounter(cartProduct.getQuantityCounter());

                                        searchList.add(newProduct);
                                        if (binding.searchRecyclerView.getAdapter() != null) {
                                            binding.searchRecyclerView.getAdapter().notifyDataSetChanged();
                                        }
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (keysRef != null && valueEventListener != null) {
            keysRef.removeEventListener(valueEventListener);
        }
    }

    @Override
    public void updateQuantity(ProductModel productModel, int quantity) {
        if (productModel != null) {
            productModel.setQuantityCounter(quantity);
            SharedPrefsUtils.setStringPreference(this, productModel.getItemId(), new Gson().toJson(productModel));
        }
    }

    @Override
    public void removeProduct(ProductModel productModel) {
        SharedPrefsUtils.removeValuePreference(this, productModel.getItemId());
    }
}