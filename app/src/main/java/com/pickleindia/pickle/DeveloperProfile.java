package com.pickleindia.pickle;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.pickleindia.pickle.databinding.ActivityDeveloperProfileBinding;

public class DeveloperProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDeveloperProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_developer_profile);

        binding.closeImageButton.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}