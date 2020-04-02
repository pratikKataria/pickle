package com.example.pickle.activity.Main.ProductsFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
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
import com.example.pickle.databinding.FragmentFruitsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FruitsFragment extends Fragment {

    public FruitsFragment() {
        // Required empty public constructor
    }

    RecyclerView fruitsRecyclerView;
    ArrayList<ProductModel> fruitList;
    CategoryRecyclerViewAdapter adapter;

    FragmentFruitsBinding fruitsBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_fruits, container, false);

        fruitsBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_fruits,
                container,
                false
        );

        View view = fruitsBinding.getRoot();

        fruitList = new ArrayList<>();

        init_recyclerView();
        populateList();

        return view;
    }

    private void init_recyclerView() {
        fruitsRecyclerView = fruitsBinding.fruitsRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        adapter = new CategoryRecyclerViewAdapter(getActivity(), fruitList);
        fruitsRecyclerView.setLayoutManager(linearLayoutManager);
        fruitsRecyclerView.setAdapter(adapter);
//        adapter.setOnItemClickListener(position -> {
//            EachItemDataModel eidm = fruitList.get(position);
//            startActivity(new Intent(FootwearActivity.this, PlaceItemOrderActivity.class).putExtra("DATA_MODEL", (Serializable) eidm));
//            Toast.makeText(FootwearActivity.this, "posittion" + position, Toast.LENGTH_SHORT).show();
//        });
    }

    private void populateList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products/Fruits");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot m : dataSnapshot.getChildren()) {
                    if (m.exists()) {
                        Log.e("Fruit recycler view ", m +" ");
                        ProductModel model = m.getValue(ProductModel.class);
                        fruitList.add(model);
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
