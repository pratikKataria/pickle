package com.example.pickle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.content.SharedPreferences
import android.os.SharedMemory
import com.google.android.gms.common.util.SharedPreferencesUtils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({

            val sharedPref: SharedPreferences = getSharedPreferences("permissions", 0)

            if (sharedPref.getBoolean("firstRun", true)) {
                startActivity(Intent(this@SplashActivity, OnBoardingActivity::class.java))
                sharedPref.edit().putBoolean("firstRun", false).apply()
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }

        }, 2000)

    }
}
