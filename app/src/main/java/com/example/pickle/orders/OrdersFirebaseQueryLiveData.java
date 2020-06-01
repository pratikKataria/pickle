package com.example.pickle.orders;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import com.example.pickle.interfaces.IFirebaseState;
import com.example.pickle.models.Operation;
import com.example.pickle.models.Orders;
import com.example.pickle.models.OrdersDetails;
import com.example.pickle.utils.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import static com.example.pickle.utils.Constant.ADD;
import static com.example.pickle.utils.Constant.FAILED;
import static com.example.pickle.utils.Constant.LIMIT;
import static com.example.pickle.utils.Constant.LOADING;
import static com.example.pickle.utils.Constant.MODIFIED;
import static com.example.pickle.utils.Constant.ORDERS_DETAILS;
import static com.example.pickle.utils.Constant.SUCCESS;

public class OrdersFirebaseQueryLiveData extends LiveData<Operation> implements IFirebaseState {
    private static final String LOG_TAG = "OrdersFirebaseQueryLiveData";

    private Query ordersFirebaseQuery;
    private final MyChildEventListener listener = new MyChildEventListener();

    public OrdersFirebaseQueryLiveData(DatabaseReference databaseReference) {
        this.ordersFirebaseQuery = databaseReference.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getUid()).limitToLast(LIMIT);
    }

    @Override
    protected void onActive() {
        ordersFirebaseQuery.addChildEventListener(listener);
        Log.e("firebase query live data", "ONACTIVE ");
    }

    @Override
    public void onInactive() {
        ordersFirebaseQuery.removeEventListener(listener);
        Log.e("firebase query live data", "ONINACTIVE ");
    }

    @Override
    public void state(int iFirebaseState) {

    }

    private class MyChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            state(LOADING);

            Orders orders = dataSnapshot.getValue(Orders.class);
            DatabaseReference orderDetailsDatabaseReference = FirebaseDatabase.getInstance().getReference(ORDERS_DETAILS);
            orderDetailsDatabaseReference.keepSynced(true);

            if (orders != null && orders.getOrderId() != null) {
                ordersFirebaseQuery = orderDetailsDatabaseReference.orderByKey().equalTo(orders.getOrderId()).limitToLast(LIMIT);

                ordersFirebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                            if (s.exists()) {
                                OrdersDetails ordersDetails = s.getValue(OrdersDetails.class);
                                ordersDetails.date = orders.getDate();
                                ordersDetails.isPastOrder = !DateUtils.isEqual(orders.getDate());
                                ordersDetails.status = orders.getOrderStatus();
                                ordersDetails.orderId = orders.getOrderId();
                                Operation<OrdersDetails> operation = new Operation<>(ordersDetails, ADD);
                                setValue(operation);

                                state(SUCCESS);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("OrdersFirebaseQata", databaseError.getCode() + " ");
                        //                    if (databaseError.getCode() ==  DatabaseError.NETWORK_ERROR) {
                        state(FAILED);
//                    }
                    }
                });
            }

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
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + ordersFirebaseQuery, databaseError.toException());

        }
    }
}
