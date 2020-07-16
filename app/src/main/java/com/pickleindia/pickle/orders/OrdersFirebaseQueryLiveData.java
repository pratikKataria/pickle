package com.pickleindia.pickle.orders;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pickleindia.pickle.models.Operation;
import com.pickleindia.pickle.models.Orders;
import com.pickleindia.pickle.utils.DateUtils;

import java.util.Random;

import static com.pickleindia.pickle.utils.Constant.ADD;
import static com.pickleindia.pickle.utils.Constant.MODIFIED;
import static com.pickleindia.pickle.utils.Constant.ORDERS;
import static com.pickleindia.pickle.utils.Constant.ORDERS_DETAILS;

public class OrdersFirebaseQueryLiveData extends LiveData<Operation>  {

    private Query ordersFirebaseQuery;
    private final MyChildEventListener listener = new MyChildEventListener();

    public OrdersFirebaseQueryLiveData(Query query) {
        this.ordersFirebaseQuery = query;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onActive() {
        ordersFirebaseQuery.addChildEventListener(listener);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onInactive() {
        ordersFirebaseQuery.removeEventListener(listener);
    }


    private class MyChildEventListener implements ChildEventListener {

        @SuppressLint("LongLogTag")
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String key = dataSnapshot.getKey();

            DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference(ORDERS).child(key);
            Query orderQuery = orderDatabaseReference.orderByChild("date");
            orderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ordersDataSnapshot) {
                    String ids = ordersDataSnapshot.child("orderDetailsIds").getValue(String.class);

                    if (ids == null) return;

                    Orders orders = ordersDataSnapshot.getValue(Orders.class);
                    if (orders != null && orders.getOrderId() != null && ordersDataSnapshot.exists()) {
                        orders.isPastOrder = DateUtils.isPastOrder(orders.getDate());
                        Operation<Orders> operation = new Operation<>(orders, ADD);
                        setValue(operation);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//            Orders ordersChanged = dataSnapshot.getValue(Orders.class);
//            Operation<Orders> ordersOperation = new Operation<>(ordersChanged, MODIFIED);
//            Log.e("OrdersFirebaseQata",  "changed ");
//            setValue(ordersOperation);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @SuppressLint("LongLogTag")
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}
