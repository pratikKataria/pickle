package com.example.pickle;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.pickle.activity.Splash.OnBoardingActivity;
import com.example.pickle.activity.Splash.SplashActivity;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Pickle extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("permissions", 0);
        if (sharedPreferences.getBoolean("FIRST_RUN", true)) {
            startActivity(new Intent(Pickle.this, OnBoardingActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            sharedPreferences.edit().putBoolean("FIRST_RUN", false).apply();
        }
    }
}
