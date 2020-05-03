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

import com.example.pickle.R;
import com.example.pickle.binding.OrderStatus;
import com.example.pickle.data.Orders;
import com.example.pickle.data.OrdersDetails;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.FragmentOrdersPlacedBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersPlacedFragment extends Fragment {

    private FragmentOrdersPlacedBinding binding;
    private ArrayList<OrdersDetails> ordersList;

    private static int countProcessing;

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

        ordersList = new ArrayList<>();
        binding.setOrdersList(ordersList);
        populateList();
        return view;
    }


    public void populateList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders/");
        Query query = reference.orderByChild("userId").equalTo("ddEk1gOv0hUFZVinEWzzdZNlBtF3");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Orders orders = dataSnapshot.getValue(Orders.class);
                    if (orders.getOrderStatus() == OrderStatus.PROCESSING) {
                        try {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OrdersDetails/");
                            Query detailQuery = reference.orderByKey().equalTo(orders.getOrderId()).limitToLast(15);
                            detailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                                        OrdersDetails ordersDetails = s.getValue(OrdersDetails.class);
                                        ordersList.add(ordersDetails);
                                        notifyChanges();
                                        Log.e(OrdersPlacedFragment.class.getName(),  ordersDetails.toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } catch (Exception e) {
                            Log.e(OrdersPlacedFragment.class.getName(), e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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


    void notifyChanges(){
        try {
            binding.recyclerView.getAdapter().notifyDataSetChanged();
        } catch (NullPointerException npe) {
            Log.e(OrdersPlacedFragment.class.getName(), npe.getMessage());
        }
    }


}
