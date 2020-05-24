package com.example.pickle.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.pickle.models.Operation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.pickle.utils.Constant.ORDERS;

public class OrdersViewModel extends ViewModel {
    private static final DatabaseReference ORDERS_REF = FirebaseDatabase.getInstance().getReference(ORDERS);

    private final OrdersFirebaseQueryLiveData liveData = new OrdersFirebaseQueryLiveData(ORDERS_REF);

    LiveData<Operation> getLiveData() {
        return liveData;
    }
}
