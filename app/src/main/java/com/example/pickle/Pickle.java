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

    }
}
