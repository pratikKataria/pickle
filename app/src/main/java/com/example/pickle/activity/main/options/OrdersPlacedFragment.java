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
import com.example.pickle.adapters.VisitorForList;
import com.example.pickle.interfaces.OrderStatus;
import com.example.pickle.databinding.FragmentOrdersPlacedBinding;
import com.example.pickle.interfaces.Visitable;
import com.example.pickle.interfaces.Visitor;
import com.example.pickle.models.EmptyState;
import com.example.pickle.models.Orders;
import com.example.pickle.models.OrdersDetails;
import com.example.pickle.utils.BundleUtils;
import com.example.pickle.utils.DateUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.pickle.utils.Constant.FIREBASE_AUTH_ID;
import static com.example.pickle.utils.Constant.FRUITS;
import static com.example.pickle.utils.Constant.ORDERS;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersPlacedFragment extends Fragment {

    private FragmentOrdersPlacedBinding binding;
    private ArrayList<Visitable> ordersList;
    private ArrayList<Visitable> pastOrdersList;

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
        pastOrdersList = new ArrayList<>();
        binding.setOrdersList(ordersList);
        binding.setPastOrdersList(pastOrdersList);

        Visitor visitor = new VisitorForList();
        binding.setVisitor(visitor);
        ordersList.add(new EmptyState());

        pastOrdersList.add(new EmptyState(R.drawable.crd_empty_past_order_bg, R.drawable.empty_past_orders_img, "No past orders", "you haven't order any thing within past 6 month's"));

//        populateList();
        return view;
    }


    public void populateList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(ORDERS);
        Query query = reference.orderByChild("userId").equalTo(FIREBASE_AUTH_ID).limitToLast(5);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Orders orders = dataSnapshot.getValue(Orders.class);
                    int orderStatus  = orders.getOrderStatus();
                    if (DateUtils.isEqual(orders.getDate()) && orderStatus == OrderStatus.PROCESSING || orderStatus == OrderStatus.ORDERED) {
                        try {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OrdersDetails/");
                            Query detailQuery = reference.orderByKey().equalTo(orders.getOrderId()).limitToLast(15);
                            detailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                                        if (s.exists()) {
                                            OrdersDetails ordersDetails = s.getValue(OrdersDetails.class);
                                            ordersDetails.isPastOrder = false;
                                            ordersDetails.status = orders.getOrderStatus();
                                            ordersDetails.orderId = orders.getOrderId();
                                            ordersList.add(ordersDetails);
                                            notifyChanges();
                                            Log.e(OrdersPlacedFragment.class.getName(),  ordersDetails.toString());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } catch (Exception e) {
                            Log.e(OrdersPlacedFragment.class.getName(), e.getMessage());
                        }
                    } else if (orderStatus == OrderStatus.PROCESSING || orderStatus == OrderStatus.DELIVERED || orderStatus == OrderStatus.CANCEL) {
                        try {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("OrdersDetails/");
                            Query detailQuery = reference.orderByKey().equalTo(orders.getOrderId()).limitToLast(15);
                            detailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                                        if (s.exists()) {
                                            OrdersDetails ordersDetails = s.getValue(OrdersDetails.class);
                                            ordersDetails.isPastOrder = true;
                                            ordersDetails.orderId = orders.getOrderId();
                                            ordersDetails.status = orders.getOrderStatus();
                                            pastOrdersList.add(ordersDetails);
                                            notifyChanges();
                                            Log.e(OrdersPlacedFragment.class.getName(),  ordersDetails.toString());
                                        }
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
            binding.recyclerViewPastOrders.getAdapter().notifyDataSetChanged();
        } catch (NullPointerException npe) {
            Log.e(OrdersPlacedFragment.class.getName(), npe.getMessage());
        }
    }


}
