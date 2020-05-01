package com.example.pickle.activity.main.options;

import android.os.Bundle;
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

import com.example.pickle.Adapters.PlaceOrderAdapter;
import com.example.pickle.R;
import com.example.pickle.data.CustomerOrdersData;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.FragmentOrdersPlacedBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersPlacedFragment extends Fragment {

    private FragmentOrdersPlacedBinding binding;
    private ArrayList<CustomerOrdersData> customerOrdersDataArrayList;
    private ArrayList<ProductModel> productModelArrayList;
    private PlaceOrderAdapter placeOrderAdapter;

    public OrdersPlacedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_orders_placed,
                container,
                false
        );

        View view = binding.getRoot();

        customerOrdersDataArrayList = new ArrayList<>();
        productModelArrayList = new ArrayList<>();
        populateList();
        init_recyclerView();

        return view;
    }

    public void init_recyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        placeOrderAdapter = new PlaceOrderAdapter(getActivity(), customerOrdersDataArrayList, productModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(placeOrderAdapter);
    }

    public void populateList() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders/" + FirebaseAuth.getInstance().getUid());
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("OrdersPlacedFragment", dataSnapshot + "'''''''''''  onChildAdded   ''''''''' " + " " + s);
                Log.e("OrdersPlaceFragment", dataSnapshot + " ");
                CustomerOrdersData customerOrdersData = dataSnapshot.getValue(CustomerOrdersData.class);
                customerOrdersDataArrayList.add(customerOrdersData);

                placeOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("OrdersPlacedFragment", dataSnapshot + "'''''''''''  onChildChanged   '''''''''" + s);
                Iterator<CustomerOrdersData> iterator = customerOrdersDataArrayList.iterator();
                while (iterator.hasNext()) {
                    CustomerOrdersData customerOrdersData =  iterator.next();
                    if (customerOrdersData != null && customerOrdersData.getItemId().equals(s)) {
                        iterator.remove();
                    }
                }

                customerOrdersDataArrayList.add(dataSnapshot.getValue(CustomerOrdersData.class));
                placeOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.e("OrdersPlacedFragment", dataSnapshot + "'''''''''''  onChildRemoved   '''''''''");

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("OrdersPlacedFragment", dataSnapshot + "'''''''''''  onChildMoved   '''''''''");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("OrdersPlacedFragment", dataSnapshot+"'''''''''''  onChildChanged   '''''''''");


            }
        });

    }


}


//    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders/"+ FirebaseAuth.getInstance().getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//@Override
//public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//        Log.e("OrdersPlaceFragment", dataSnapshot + " ");
//        CustomerOrdersData placeOrderModel = snapshot.getValue(CustomerOrdersData.class);
//        customerOrdersDataArrayList.add(placeOrderModel);
//        Log.e("placeOrderAdapter", placeOrderModel.getItemCategory()+"");
//
//
//
//        placeOrderAdapter.notifyDataSetChanged();
//        }
//        placeOrderAdapter.notifyDataSetChanged();
//        }
//
//@Override
//public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//        });
