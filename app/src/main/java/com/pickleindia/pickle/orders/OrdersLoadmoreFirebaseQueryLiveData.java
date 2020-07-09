package com.pickleindia.pickle.orders;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.pickleindia.pickle.models.Operation;
import com.pickleindia.pickle.models.Orders;
import com.pickleindia.pickle.models.OrdersDetails;
import com.pickleindia.pickle.utils.DateUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.pickleindia.pickle.utils.Constant.ADD;
import static com.pickleindia.pickle.utils.Constant.MODIFIED;
import static com.pickleindia.pickle.utils.Constant.ORDERS;
import static com.pickleindia.pickle.utils.Constant.ORDERS_DETAILS;

public class OrdersLoadmoreFirebaseQueryLiveData extends LiveData<Operation>{
    private Query ordersFirebaseQuery;
    private final MyChildEventListener listener = new MyChildEventListener();

    public OrdersLoadmoreFirebaseQueryLiveData(Query query) {
        this.ordersFirebaseQuery = query;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onActive() {
        ordersFirebaseQuery.addChildEventListener(listener);
        Log.e("firebase query live data", "ONACTIVE ");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onInactive() {
        ordersFirebaseQuery.removeEventListener(listener);
        Log.e("firebase query live data", "ONINACTIVE ");
    }

    private class MyChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            String key = dataSnapshot.getKey();

            DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference(ORDERS).child(key);
            orderDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ordersDataSnapshot) {
                    Orders orders = ordersDataSnapshot.getValue(Orders.class);

                    DatabaseReference orderDetailsDatabaseReference = FirebaseDatabase.getInstance().getReference(ORDERS_DETAILS).child(key);
                    orderDetailsDatabaseReference.keepSynced(true);
                    orderDetailsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot orderDetailsDataSnapshot) {
                                if (orders != null && orders.getOrderId() != null && orderDetailsDataSnapshot.exists()) {
                                    OrdersDetails ordersDetails = orderDetailsDataSnapshot.getValue(OrdersDetails.class);
                                    ordersDetails.date = orders.getDate();
                                    ordersDetails.isPastOrder = !DateUtils.isEqual(orders.getDate());
                                    ordersDetails.status = orders.getOrderStatus();
                                    ordersDetails.orderId = orders.getOrderId();
                                    Operation<OrdersDetails> operation = new Operation<>(ordersDetails, ADD);
                                    setValue(operation);
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Orders ordersChanged = dataSnapshot.getValue(Orders.class);
            Operation<Orders> ordersOperation = new Operation<>(ordersChanged, MODIFIED);
            Log.e("OrdersFirebaseQata",  "changed ");
            setValue(ordersOperation);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    }
}