package com.example.pickle.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pickle.models.Operation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import static com.example.pickle.utils.Constant.LOADING;
import static com.example.pickle.utils.Constant.ORDERS;

public class OrdersViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoadingMutableLiveData = new MutableLiveData<>();
    private static final DatabaseReference ORDERS_REF = FirebaseDatabase.getInstance().getReference(ORDERS);
    private final OrdersFirebaseQueryLiveData liveData = new OrdersFirebaseQueryLiveData(ORDERS_REF) {
        @Override
        public void state(int iFirebaseState) {
            if (iFirebaseState == LOADING) {
                isLoadingMutableLiveData.setValue(true);
            }
        }
    };

    public LiveData<Boolean> getIsLoadingMutableLiveData() {
        return isLoadingMutableLiveData;
    }

    public void setIsLoadingMutableLiveData(MutableLiveData<Boolean> isLoadingMutableLiveData) {
        this.isLoadingMutableLiveData = isLoadingMutableLiveData;
    }

    LiveData<Operation> getLiveData() {
        return liveData;
    }
}
