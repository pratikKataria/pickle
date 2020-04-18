package com.example.pickle.activity.Main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.Adapters.AutoCompleteAdapter;
import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.CardViewSearchItemBinding;
import com.example.pickle.ui.SearchViewBottomSheetDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Arrays;

public class FirebaseSearchActivity extends AppCompatActivity {


    private RecyclerView _searchRecyclerView;
    private DatabaseReference ref;
    private FirebaseRecyclerAdapter<ProductModel, FirebaseSearchViewHolder> firebaseRecyclerAdapter;
    private AutoCompleteTextView _autoCompleteTextView;
    private ArrayList<String> searchArrayList;

    private void init_fields() {
        _autoCompleteTextView = findViewById(R.id.edit_query);
        _searchRecyclerView = findViewById(R.id.searchResultView);

        searchArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.vegetables)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_search);


        init_fields();
        popupLayoutDecoration();

        _searchRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        _searchRecyclerView.setHasFixedSize(true);

        ref = FirebaseDatabase.getInstance().getReference("Products/Vegetables");


        AutoCompleteAdapter adapter = new AutoCompleteAdapter(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                searchArrayList
        );

        _autoCompleteTextView.setAdapter(adapter);

        _autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("beforeTextChanged", " " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 3) {
                    firebaseUserSearch(s + "");
                }
                Log.e("onTextChanged", " " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afterTextChanged", " " + s);
            }
        });
    }

    private void firebaseUserSearch(String queryString) {

        Query query = ref.orderByChild("itemName").startAt(queryString).endAt(queryString + "\uf8ff");

        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(query, ProductModel.class).build();

        firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<ProductModel, FirebaseSearchViewHolder>(options) {

                    @NonNull
                    @Override
                    public FirebaseSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        CardViewSearchItemBinding cardViewBinding = CardViewSearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

                        return new FirebaseSearchViewHolder(cardViewBinding);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull FirebaseSearchViewHolder holder, int position, @NonNull ProductModel model) {

                        Log.e("FirebaseSearchActivity", "Firebase view holder");


                        holder.setBinding(model);

                    }
                };
        firebaseRecyclerAdapter.startListening();
        _searchRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();

    }

    public class FirebaseSearchViewHolder extends RecyclerView.ViewHolder {

        private CardViewSearchItemBinding itemView;

        public FirebaseSearchViewHolder(@NonNull CardViewSearchItemBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;

            CardView cardView = itemView.cardView;

            cardView.setOnClickListener(n -> {
                showBottomSheet();
            });
        }

        void setBinding(ProductModel productModel) {
            itemView.setProduct(productModel);
        }

    }


    private void popupLayoutDecoration() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        _autoCompleteTextView.setDropDownHorizontalOffset(110);
        _autoCompleteTextView.setDropDownWidth(width - 150);
    }

    private void showBottomSheet() {
        SearchViewBottomSheetDialog bottomSheetDialog = new SearchViewBottomSheetDialog();
        bottomSheetDialog.show(getSupportFragmentManager(), "searchViewBottomSheet");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null)
           firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }
}
