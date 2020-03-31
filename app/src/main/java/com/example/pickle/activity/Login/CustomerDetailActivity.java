package com.example.pickle.activity.Login;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pickle.R;
import com.example.pickle.activity.Main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.HashMap;

public class CustomerDetailActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        EditText editTextName = findViewById(R.id.cd_et_userName);
        EditText editTextEmail = findViewById(R.id.cd_et_email);
        EditText editTextAddress = findViewById(R.id.cd_et_address);
        progressBar = findViewById(R.id.progressBar);

        MaterialButton materialButton = findViewById(R.id.cd_mb_save);

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
            _customer.put("c_creation_date", new Date());
            _customer.put("c_uid", FirebaseAuth.getInstance().getUid());

            uploadCustomerDetails(_customer);
        });
    }

    public void uploadCustomerDetails(HashMap<String, Object> _customer) {

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customers").child(FirebaseAuth.getInstance().getUid());
        ref.updateChildren(_customer).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CustomerDetailActivity.this, "details uploaded successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CustomerDetailActivity.this, MainActivity.class));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(CustomerDetailActivity.this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }
}
