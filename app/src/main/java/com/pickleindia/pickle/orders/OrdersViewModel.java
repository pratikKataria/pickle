package com.pickleindia.pickle.orders;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pickleindia.pickle.models.Operation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.pickleindia.pickle.utils.Constant.LIMIT;
import static com.pickleindia.pickle.utils.Constant.ONGOING_LIMIT;

public class OrdersViewModel extends ViewModel {

    LiveData<Operation> currentOrders() {
        Query QUERY_ORDERS_REF = FirebaseDatabase.getInstance().getReference("UserOrders").child(FirebaseAuth.getInstance().getUid()).orderByChild("date_orderId").limitToFirst(ONGOING_LIMIT);
        return new OrdersFirebaseQueryLiveData(QUERY_ORDERS_REF);
    }

    LiveData<Operation> loadMoreOrders(String endKey) {
        Query QUERY_ORDERS_REF = FirebaseDatabase.getInstance().getReference("UserOrders").child(FirebaseAuth.getInstance().getUid()).orderByChild("date_orderId").startAt(endKey).limitToFirst(LIMIT);
        return new OrdersFirebaseQueryLiveData(QUERY_ORDERS_REF);
    }
}
