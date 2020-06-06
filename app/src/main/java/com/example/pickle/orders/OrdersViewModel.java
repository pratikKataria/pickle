package com.example.pickle.orders;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.pickle.models.Operation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import static com.example.pickle.utils.Constant.LIMIT;
import static com.example.pickle.utils.Constant.ONGOING_LIMIT;

public class OrdersViewModel extends ViewModel {

    LiveData<Operation> orders() {
        Query QUERY_ORDERS_REF = FirebaseDatabase.getInstance().getReference("UserOrders").child(FirebaseAuth.getInstance().getUid()).orderByChild("date_orderId").limitToFirst(ONGOING_LIMIT);
        return new OrdersFirebaseQueryLiveData(QUERY_ORDERS_REF);
    }

    LiveData<Operation> loadMoreOrders(String endKey) {
        Query QUERY_MORE_ORDERS = FirebaseDatabase.getInstance().getReference("UserOrders").child(FirebaseAuth.getInstance().getUid()).orderByChild("date_orderId").startAt(endKey).limitToFirst(LIMIT-1);
        return new OrdersLoadmoreFirebaseQueryLiveData(QUERY_MORE_ORDERS);
    }
}
