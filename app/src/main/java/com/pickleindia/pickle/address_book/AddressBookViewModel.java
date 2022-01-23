package com.pickleindia.pickle.address_book;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.pickleindia.firebase.FirebasePaths;
import com.pickleindia.pickle.models.Address;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pickleindia.pickle.ui.addAddress.model.AddAddressRequest;

import org.jetbrains.annotations.NotNull;

public class AddressBookViewModel extends ViewModel {
    MutableLiveData<AddAddressRequest> userAddressMutableLiveData = new MutableLiveData<>();

    void getAddressFromFirebaseDatabase() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Addresses").child(FirebaseAuth.getInstance().getUid()).child("addressJson");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Log.e(getClass().getName(), "onDataChange: " + snapshot );
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }


    public LiveData<AddAddressRequest> getUserAddressLiveData() {
        return userAddressMutableLiveData;
    }
}
