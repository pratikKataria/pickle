package com.example.pickle.viewpager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pickle.R;
import com.example.pickle.activity.Login.CustomerDetailActivity;
import com.example.pickle.activity.Main.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApartmentFragment extends Fragment {

    private int counter;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt("ARG_COUNT");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_apartment, container, false);

        EditText editTextName = view.findViewById(R.id.cd_et_userName);
        EditText editTextEmail = view.findViewById(R.id.cd_et_address);
        EditText editTextAddress = view.findViewById(R.id.cd_et_address);
        progressBar = view.findViewById(R.id.progressBar);

        MaterialButton materialButton = view.findViewById(R.id.cd_mb_save);

        materialButton.setOnClickListener(n -> {
            if (editTextAddress.getText().toString().isEmpty()) {
                editTextAddress.setError("should not be empty");
                editTextAddress.requestFocus();
                return;
            }

            if (editTextName.getText().toString().isEmpty()) {
                editTextName.setError("should not be empty");
                editTextName.requestFocus();
                return;
            }

            HashMap<String, Object> _customer = new HashMap<>();
            _customer.put("c_address",editTextAddress.getText().toString());
            _customer.put("c_name",editTextName.getText().toString());
            _customer.put("c_email", editTextEmail.getText().toString().isEmpty() ? " " : editTextEmail.getText().toString().toLowerCase());
            _customer.put("d_token", FirebaseInstanceId.getInstance().getToken());
            _customer.put("c_creation_date", new SimpleDateFormat("dd : MM : YYYY").format(new Date()));
            _customer.put("c_uid", FirebaseAuth.getInstance().getUid());

            uploadCustomerDetails(_customer);
        });

        return view;
    }

    public void uploadCustomerDetails(HashMap<String, Object> _customer) {

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customers").child(FirebaseAuth.getInstance().getUid());
        ref.updateChildren(_customer).addOnSuccessListener(task -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "details updated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getActivity(), "fragment no " + counter + 1, Toast.LENGTH_SHORT).show();
    }
}
