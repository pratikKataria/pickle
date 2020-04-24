package com.example.pickle.activity.Login.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.StrongBoxUnavailableException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pickle.R;
import com.example.pickle.activity.Main.MainActivity;
import com.example.pickle.data.Address;
import com.example.pickle.data.ApartmentDataModel;
import com.example.pickle.data.CurrentAddress;
import com.example.pickle.data.Customer;
import com.example.pickle.data.IndividualHouseDataModel;
import com.example.pickle.data.PersonalInformation;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */

public class ApartmentFragment extends Fragment {

    ProgressBar progressBar;

    public ApartmentFragment() {
        // Required empty public constructor
    }

    public static ApartmentFragment newInstance(Integer counter) {
        ApartmentFragment fragment = new ApartmentFragment();
        Bundle args = new Bundle();
        args.putInt("ARG_COUNT", counter);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_apartment, container, false);

        EditText etName = view.findViewById(R.id.cd_et_userName);
        EditText etApartment = view.findViewById(R.id.cd_et_apartment);
        EditText etTower = view.findViewById(R.id.cd_et_pin_code);
        EditText etFlat = view.findViewById(R.id.cd_et_flat_house_no);
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

            if (etApartment.getText().toString().isEmpty()) {
                etApartment.setError("should not be empty");
                etApartment.requestFocus();
                return;
            }

            if (!(etTower.getText().toString().length() == 6)) {
                etTower.setError("incorrect number");
                etTower.requestFocus();
                return;
            }


            if (etFlat.getText().toString().isEmpty()) {
                etFlat.setError("should not be empty");
                etFlat.requestFocus();
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
                    new ApartmentDataModel(
                    etName.getText().toString(),
                    etApartment.getText().toString(),
                    etFlat.getText().toString(),
                    editTextAddress.getText().toString(),
                    landmark.getText().toString(),
                            "apartment or gated society"
            ));
        });

        return view;
    }

    public void uploadCustomerDetails(Customer _customer, ApartmentDataModel apartmentDataModel) {

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customers").child(FirebaseAuth.getInstance().getUid());
        ref.setValue(_customer).addOnSuccessListener(task -> {
            updateAddress(apartmentDataModel);

        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }

    private void updateAddress(ApartmentDataModel apartmentDataModel) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Addresses");

        ref.child(FirebaseAuth.getInstance().getUid()).child("slot1").setValue(apartmentDataModel).addOnSuccessListener(task -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "details updated", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
