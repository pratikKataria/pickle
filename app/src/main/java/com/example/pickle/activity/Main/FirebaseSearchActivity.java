package com.example.pickle.activity.Main;

import android.content.SharedPreferences;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.pickle.Adapters.AutoCompleteAdapter;
import com.example.pickle.Adapters.FirebaseSearchRecyclerAdapter;
import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.CardViewSearchItemBinding;
import com.example.pickle.ui.SearchViewBottomSheetDialog;
import com.example.pickle.utils.SharedPrefsUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

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
        _autoCompleteTextView = findViewById(R.id.edit_query);
        searchArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.vegetables)));
        ref = FirebaseDatabase.getInstance().getReference("Products/Vegetables");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_search);

        init_fields();
        init_cartMap();
        init_recyclerview();
        popupLayoutDecoration();

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
                if (s.length() > 3) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

    }

    private void init_recyclerview() {
        _searchRecyclerView = findViewById(R.id.searchResultView);
        _searchRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        _searchRecyclerView.setHasFixedSize(true);

        Query query = ref.orderByChild("itemName");

        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(query, snapshot -> {
                    ProductModel productModel = snapshot.getValue(ProductModel.class);
                    if (productModel != null) {
                        if (cartMap.containsKey(productModel.getItemId())) {
                            productModel.setQuantityCounter(cartMap.get(productModel.getItemId()).getQuantityCounter());
                        }
                    }
                    return productModel != null ? productModel : new ProductModel();
                }).build();

        if (cartMap.size() > 0) {
            searchRecyclerAdapter = new FirebaseSearchRecyclerAdapter(options, this, cartMap);
        } else {
            searchRecyclerAdapter = new FirebaseSearchRecyclerAdapter(options, this);
        }
        _searchRecyclerView.setAdapter(searchRecyclerAdapter);
    }

    private void init_cartMap() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences != null) {
            Map<String, ?> allEntries = preferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                String list = SharedPrefsUtils.getStringPreference(this, entry.getKey(), 0);
                ProductModel[] models = new Gson().fromJson(list, ProductModel[].class);

                if (list != null && models != null) {
                    for (ProductModel productModel : models) {
                        cartMap.put(productModel.getItemId(), productModel);
                    }
                }
            }
        }
    }

    private void popupLayoutDecoration() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        _autoCompleteTextView.setDropDownHorizontalOffset(110);
        _autoCompleteTextView.setDropDownWidth(width - 150);
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        searchRecyclerAdapter.stopListening();
    }
}
