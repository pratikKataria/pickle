package com.example.pickle

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val PERMISSION_ALL = 1

        var permission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_CONTACTS
        )

        if (!hasPermissions(this, permission)) {
            ActivityCompat.requestPermissions(this, permission, PERMISSION_ALL)
            return
        }

    }

    override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        return super.shouldShowRequestPermissionRationale(permission)
    }

    fun startNextActivity() {
        Handler().postDelayed({

            val sharedPref: SharedPreferences = getSharedPreferences("permissions", 0)

            if (sharedPref.getBoolean("firstRun", true)) {
                startActivity(Intent(this@SplashActivity, OnBoardingActivity::class.java))
                sharedPref.edit().putBoolean("firstRun", false).apply()
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

        }, 2000)
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}