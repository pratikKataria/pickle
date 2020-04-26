package com.example.pickle.activity.Main.ProductsFragment;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.Adapters.CategoryRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.data.ProductModel;
import com.example.pickle.utils.SharedPrefsUtils;
import com.example.pickle.databinding.FragmentFruitsBinding;
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
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class FruitsFragment extends Fragment {

    private DatabaseReference reference;
    private ChildEventListener childEventListener;

    public FruitsFragment() {
        // Required empty public constructor
    }

    RecyclerView fruitsRecyclerView;
    ArrayList<ProductModel> fruitList;
    ArrayList<ProductModel> cartList;
    CategoryRecyclerViewAdapter adapter;

    FragmentFruitsBinding fruitsBinding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_fruits, container, false);

        Log.e("onCreateView", "fruit fragment");

        fruitsBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_fruits,
                container,
                false
        );

        //offline carted products list
        String cartProducts = SharedPrefsUtils.getStringPreference(getActivity(), "Fruits", 0);
        ProductModel[] productModels = new Gson().fromJson(cartProducts, ProductModel[].class);

        if (productModels != null) {
            cartList = new ArrayList<>(Arrays.asList(productModels));
        } else {
            cartList = new ArrayList<>();
        }

        View view = fruitsBinding.getRoot();
        fruitList = new ArrayList<>();
        init_recyclerView();
        new Handler().postDelayed(this::populateList,1200);

        return view;
    }


    private void init_recyclerView() {
        fruitsRecyclerView = fruitsBinding.fruitsRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        adapter = new CategoryRecyclerViewAdapter(getActivity(), fruitList, "Fruits");
        fruitsRecyclerView.setLayoutManager(linearLayoutManager);
        fruitsRecyclerView.setAdapter(adapter);
//        adapter.setOnItemClickListener(position -> {
//            EachItemDataModel eidm = fruitList.get(position);
//            startActivity(new Intent(FootwearActivity.this, PlaceItemOrderActivity.class).putExtra("DATA_MODEL", (Serializable) eidm));
//            Toast.makeText(FootwearActivity.this, "posittion" + position, Toast.LENGTH_SHORT).show();
//        });
    }

    private void populateList() {
        //online database
        reference = FirebaseDatabase.getInstance().getReference("Products/Fruits");

        Query query = reference.orderByChild("itemName").limitToLast(15);

        childEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        Log.e("Fruit recycler view ", dataSnapshot +" ");
                        ProductModel model = dataSnapshot.getValue(ProductModel.class);

                        if (cartList != null) {
                            for (ProductModel pm : cartList) {
                                if (model.getItemId().equals(pm.getItemId())) {
                                    model.setQuantityCounter(pm.getQuantityCounter());
                                    break;
                                }
                            }
                        }

                        fruitList.add(model);
                        adapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int position = 0;
                Log.e("onChildChanged", "key s "+ s + " datasnaphot"+ dataSnapshot);
                Iterator<ProductModel> iterator = fruitList.iterator();
                while (iterator.hasNext()) {
                    position++;
                    ProductModel product = iterator.next();
                    ProductModel newProduct = dataSnapshot.getValue(ProductModel.class);
                    if (newProduct != null && product.getItemId().equals(newProduct.getItemId())) {
                        iterator.remove();
                        break;
                    }
                }

                ProductModel newProduct = dataSnapshot.getValue(ProductModel.class);
                if (newProduct != null) {
                    for (ProductModel product :cartList) {
                        if (product.getItemId().equals(newProduct.getItemId())) {
                            newProduct.setQuantityCounter(product.getQuantityCounter());
                        }
                    }
                    if ((position-1) <= fruitList.size()) {
                        fruitList.add(position-1, newProduct);
                        adapter.notifyDataSetChanged();
                    } else {
                        fruitList.add(newProduct);
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
    public void onStop() {
        super.onStop();
        if (childEventListener != null) {
            reference.removeEventListener(childEventListener);
        }
    }
}
