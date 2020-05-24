package com.example.pickle;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.pickle.models.Operation;
import com.example.pickle.models.Orders;
import com.example.pickle.models.OrdersDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.pickle.utils.Constant.ORDERS;
import static com.example.pickle.utils.Constant.ORDERS_DETAILS;

public class OrdersViewModel extends ViewModel {
    private static final DatabaseReference ORDERS_REF = FirebaseDatabase.getInstance().getReference(ORDERS);

    private final  FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(ORDERS_REF);

    public LiveData<Operation> getLiveData() {
        return liveData;
    }
}
