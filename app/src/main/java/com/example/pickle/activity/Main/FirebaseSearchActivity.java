package com.example.pickle.activity.Main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.CategoryProductCardViewBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseSearchActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    private DatabaseReference reference;
    private ArrayList<String> keys;
    private FirebaseRecyclerAdapter<ProductModel, FirebaseSearchViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_search);

        keys = new ArrayList<>();

        Button button = findViewById(R.id.search_button);
        EditText text = findViewById(R.id.edit_query);
        recyclerView = findViewById(R.id.searchResultView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
//        reference = FirebaseDatabase.getInstance().getReference("Products");
        reference = FirebaseDatabase.getInstance().getReference("Products/Vegetables");

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("beforeTextChanged", " " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged", " " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afterTextChanged", " " + s);
            }
        });

        button.setOnClickListener(n -> {
            firebaseUserSearch(text.getText().toString() + "");
        });
    }

    private void firebaseUserSearch(String queryString) {

        Log.e("FirebaseSearchActivity", queryString);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot documentSnapshot1 : dataSnapshot.getChildren()) {
                    Log.e("FirebaseSearchActivity", "key " + documentSnapshot1.getKey());
                    if (documentSnapshot1 != null)
                        keys.add(documentSnapshot1.getKey());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query  = reference.orderByChild("itemName").startAt(queryString).endAt(queryString + "\uf8ff");;
        for (String  key : keys) {
//            Query query = reference.child(key).orderByChild("itemName").startAt(queryString).endAt(queryString + "\uf8ff");

//            query.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Log.e("FirebaseSearch", dataSnapshot + " ");
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
        }

        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(query, ProductModel.class).build();

        firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<ProductModel, FirebaseSearchViewHolder>(options) {

                    @NonNull
                    @Override
                    public FirebaseSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        CategoryProductCardViewBinding cardViewBinding = CategoryProductCardViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

                        return new FirebaseSearchViewHolder(cardViewBinding);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull FirebaseSearchViewHolder holder, int position, @NonNull ProductModel model) {

                        Log.e("FirebaseSearchActivity", "Firebase view holder");


                        holder.setBinding(model);

                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();

    }

    public class FirebaseSearchViewHolder extends RecyclerView.ViewHolder {

        CategoryProductCardViewBinding itemView;

        public FirebaseSearchViewHolder(@NonNull CategoryProductCardViewBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }

        void setBinding(ProductModel productModel) {
            itemView.setProduct(productModel);
        }
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
        firebaseRecyclerAdapter.stopListening();
    }
}
