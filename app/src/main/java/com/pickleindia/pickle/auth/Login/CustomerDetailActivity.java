package com.pickleindia.pickle.auth.Login;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.util.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.databinding.ActivityCustomerAddressBinding;
import com.pickleindia.pickle.models.CurrentAddress;

import java.util.HashMap;

public class CustomerDetailActivity extends AppCompatActivity {


    private ActivityCustomerAddressBinding activityCustomerAddressBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCustomerAddressBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_address);
        init();
    }

    void init() {
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


            CurrentAddress currentAddress = new CurrentAddress();
            currentAddress.setAddress(addressCompleteAddress);
            currentAddress.setLandmark(addressInstruction);
            currentAddress.setAreaPin("451115");
            currentAddress.setMobileNo(addressUserMobileNumber);

            atomicUpdate(addressUserName, currentAddress);
        });
    }

    private void atomicUpdate(String customerName, CurrentAddress currentAddress) {
//        currentLocationBinding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> update = new HashMap<>();
        String uid = FirebaseAuth.getInstance().getUid();

        update.put("Customers/" + uid + "/personalInformation/username", customerName);
        update.put("Addresses/" + uid + "/slot1", currentAddress);

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
}
