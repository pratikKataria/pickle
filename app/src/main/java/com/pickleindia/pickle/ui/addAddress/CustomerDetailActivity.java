package com.pickleindia.pickle.ui.addAddress;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.util.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.pickleindia.firebase.FirebasePaths;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.databinding.ActivityCustomerAddressBinding;
import com.pickleindia.pickle.ui.addAddress.model.AddAddressRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CustomerDetailActivity extends AppCompatActivity {


    private ActivityCustomerAddressBinding activityCustomerAddressBinding;
    private ArrayList<AddAddressRequest.Data> addressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCustomerAddressBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_address);
        init();
    }

    void init() {
        Intent intent = getIntent();
        if (intent != null) {
            AddAddressRequest addAddressRequest = (AddAddressRequest) intent.getSerializableExtra("address");
            if (addAddressRequest != null)     addressList.addAll(addAddressRequest.getData());
        }


        activityCustomerAddressBinding.cdSaveAddress.setOnClickListener(v -> {
            String addressUserName = activityCustomerAddressBinding.cdEtUserName.getText().toString();
            String addressUserMobileNumber = activityCustomerAddressBinding.cdEtMobile.getText().toString();
            String addressCompleteAddress = activityCustomerAddressBinding.cdEtCompleteAddress.getText().toString();
            String addressInstruction = activityCustomerAddressBinding.cdEtInstruction.getText().toString();

            if (Strings.isEmptyOrWhitespace(addressUserName)) {
                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Strings.isEmptyOrWhitespace(addressUserMobileNumber)) {
                Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (addressUserMobileNumber.length() < 10) {
                Toast.makeText(this, "Please enter correct mobile number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Strings.isEmptyOrWhitespace(addressCompleteAddress)) {
                Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
                return;
            }


            AddAddressRequest.Data currentAddress = new AddAddressRequest.Data();
            currentAddress.setAddress(addressCompleteAddress);
            currentAddress.setLandmark(addressInstruction);
            currentAddress.setAreaPin("451115");
            currentAddress.setMobileNo(addressUserMobileNumber);
            currentAddress.setId(getSaltString());

            addressList.add(currentAddress);
            AddAddressRequest s = new AddAddressRequest();
            s.setData(addressList);

            Gson g = new Gson();
            String address = g.toJson(s);
            Log.e(getClass().getName(), "init: " + address);
            Log.e(getClass().getName(), "init: " + FirebaseAuth.getInstance().getUid());

            atomicUpdate(addressUserName, address);
        });
    }

    private void atomicUpdate(String customerName, String currentAddress) {
//        currentLocationBinding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> update = new HashMap<>();
        String uid = FirebaseAuth.getInstance().getUid();
        Log.e(getClass().getName(), "atomicUpdate: " + uid);
        update.put("Customers/" + uid + "/personalInformation/username", customerName);
        update.put(FirebasePaths.Companion.getADD_ADDRESS(), currentAddress);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.updateChildren(update).addOnSuccessListener(task -> {
//            currentLocationBinding.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Address Updated", Toast.LENGTH_SHORT).show();
            finish();
//            showSnackBar();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            currentLocationBinding.progressBar.setVisibility(View.GONE);
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
