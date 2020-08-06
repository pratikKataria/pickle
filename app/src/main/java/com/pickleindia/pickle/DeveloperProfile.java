package com.pickleindia.pickle;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

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

    public void openFacebookIntent(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            int versionCode = this.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            String uri = "";
            if (versionCode >= 3002850) {
                uri = "fb://facewebmodal/f?href=" + "https://www.facebook.com/byte.mint.3";
            } else {
                uri = "fb://page/" + "pickleindia.mart";
            }
            intent.setData(Uri.parse(uri));
            startActivity(intent);

        } catch (PackageManager.NameNotFoundException | ActivityNotFoundException e) {
            intent.setData(Uri.parse("https://www.facebook.com/byte.mint.3"));
            startActivity(intent);
        }
    }

    public void openInstagramIntent(View view) {
        Uri uri = Uri.parse("https://www.instagram.com/tricky__tweaks/");
        try {
            Intent intagram = new Intent(Intent.ACTION_VIEW);
            intagram.setData(uri);
            intagram.setPackage("com.instagram.android");
            startActivity(intagram);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
        }
    }

    public void openTwitterIntent(View view) {
        Uri uri = Uri.parse("https://twitter.com/byte_mint");

        try {
            Intent twitter = new Intent(Intent.ACTION_VIEW);
            twitter.setData(uri);
            twitter.setPackage("com.twitter.android");
            startActivity(twitter);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}