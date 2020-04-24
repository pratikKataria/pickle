package com.example.pickle.activity.Login.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.pickle.R;
import com.example.pickle.activity.Main.MainActivity;
import com.example.pickle.data.Customer;
import com.example.pickle.data.IndividualHouseDataModel;
import com.example.pickle.data.PersonalInformation;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndividualHouseFragment extends Fragment {

    private ProgressBar progressBar;

    public IndividualHouseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_house, container, false);

        EditText etName = view.findViewById(R.id.cd_et_userName);
        EditText etareaPin = view.findViewById(R.id.cd_et_pin_code);
        EditText etFloor = view.findViewById(R.id.cd_et_flat_house_no);
        EditText landmark = view.findViewById(R.id.cd_et_instruction);
        EditText editTextAddress = view.findViewById(R.id.cd_et_address);
        progressBar = view.findViewById(R.id.progressBar);

        MaterialButton materialButton = view.findViewById(R.id.cd_mb_save);

        materialButton.setOnClickListener(n -> {

            if (etName.getText().toString().isEmpty()) {
                etName.setError("should not be empty");
                etName.requestFocus();
                return;
            }


            if (!(etareaPin.getText().toString().length() == 6)) {
                etareaPin.setError("incorrect number");
                etareaPin.requestFocus();
                return;
            }


            if (etFloor.getText().toString().isEmpty()) {
                etFloor.setError("should not be empty");
                etFloor.requestFocus();
                return;
            }

            if (landmark.getText().toString().isEmpty()) {
                landmark.setError("should not be empty");
                landmark.requestFocus();
                return;
            }

            if (editTextAddress.getText().toString().isEmpty()) {
                editTextAddress.setError("should not be empty");
                editTextAddress.requestFocus();
                return;
            }

            Customer customer = new Customer(

                    new PersonalInformation(
                            etName.getText().toString(),
                            FirebaseAuth.getInstance().getUid(),
                            FirebaseInstanceId.getInstance().getToken(),
                            new SimpleDateFormat("dd : MM : YYYY ").format(new Date()),
                            FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                    )

            );

            uploadCustomerDetails(
                    customer,
                    new IndividualHouseDataModel(
                    etareaPin.getText().toString(),
                    editTextAddress.getText().toString(),
                    landmark.getText().toString(),
                    etFloor.getText().toString(),
                            "individualHouse"
            ));
        });

        return view;
    }

    public void uploadCustomerDetails(Customer _customer, IndividualHouseDataModel individualHouseDataModel) {

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customers").child(FirebaseAuth.getInstance().getUid());
        ref.setValue(_customer).addOnSuccessListener(task -> {
            updateAddress(individualHouseDataModel);
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }


    private void updateAddress(IndividualHouseDataModel individualHouseDataModel) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Addresses").child(FirebaseAuth.getInstance().getUid()).child("slot1");

        ref.setValue(individualHouseDataModel).addOnSuccessListener(task -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "details updated", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }
}