package com.example.pickle;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.pickle.models.Operation;
import com.example.pickle.models.Orders;
import com.example.pickle.models.OrdersDetails;
import com.example.pickle.utils.DateUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.pickle.interfaces.OrderStatus.CANCEL;
import static com.example.pickle.interfaces.OrderStatus.DELIVERED;
import static com.example.pickle.utils.Constant.ADD;
import static com.example.pickle.utils.Constant.MODIFIED;
import static com.example.pickle.utils.Constant.ORDERS_DETAILS;

public class FirebaseQueryLiveData extends LiveData<Operation> {
    private static final String LOG_TAG = "FirebaseQueryLiveData";

    private Query firebaseQuery;
    private final MyChildEventListener listener = new MyChildEventListener();

    public FirebaseQueryLiveData(DatabaseReference databaseReference) {
        this.firebaseQuery = databaseReference;
    }

    @Override
    protected void onActive() {
        firebaseQuery.addChildEventListener(listener);
        Log.e("firebase query live data", "ONACTIVE ");
    }

    @Override
    public void onInactive() {
        firebaseQuery.removeEventListener(listener);
        Log.e("firebase query live data", "ONINACTIVE ");
    }

    private class MyChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Orders orders = dataSnapshot.getValue(Orders.class);
            DatabaseReference orderDetailsDatabaseReference = FirebaseDatabase.getInstance().getReference(ORDERS_DETAILS);
            Query orderDetailsQuery = orderDetailsDatabaseReference.orderByKey().equalTo(orders.getOrderId()).limitToLast(20);
            orderDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        if (s.exists()) {
                            OrdersDetails ordersDetails = s.getValue(OrdersDetails.class);
                            ordersDetails.isPastOrder = !DateUtils.isEqual(orders.getDate()) ||
                                    orders.getOrderStatus() == CANCEL ||
                                    orders.getOrderStatus() == DELIVERED;
                            ordersDetails.status = orders.getOrderStatus();
                            ordersDetails.orderId = orders.getOrderId();
                            Operation<OrdersDetails> operation = new Operation<>(ordersDetails, ADD);
                            setValue(operation);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Orders orders = dataSnapshot.getValue(Orders.class);
            Operation<Orders> ordersOperation = new Operation<>(orders, MODIFIED);
            setValue(ordersOperation);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + firebaseQuery, databaseError.toException());

        }
    }
}
