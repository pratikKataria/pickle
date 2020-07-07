package com.example.pickle.address_book;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pickle.models.Address;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddressBookViewModel extends ViewModel {
    MutableLiveData<Address> userAddressMutableLiveData = new MutableLiveData<>();

    void getAddressFromFirebaseDatabase() {
        if (FirebaseAuth.getInstance().getCurrentUser()!= null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Addresses").child(FirebaseAuth.getInstance().getUid()).child("slot1");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                            Address address = dataSnapshot.getValue(Address.class);
                            userAddressMutableLiveData.postValue(address);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    public LiveData<Address> getUserAddressLiveData() {
        return userAddressMutableLiveData;
    }
}
