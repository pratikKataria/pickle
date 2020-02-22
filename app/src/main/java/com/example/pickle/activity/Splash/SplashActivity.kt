package com.example.pickle.activity.Splash

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pickle.R
import com.example.pickle.activity.Main.MainActivity
import com.example.pickle.utils.PermissionUtils


class SplashActivity : AppCompatActivity() {

    private val SEND_SMS_PERMISSION_REQUEST_CODE = "10000"
    private val TAG = "TEST"

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

        startNextActivity()

    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (PermissionUtils().neverAskAgainSelected(this, Manifest.permission.SEND_SMS)) {
//                displayNeverAskAgainDialog();
            } else {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS))
            }
        }
    }

    fun displayNeverAskAgainDialog() {
        var build : AlertDialog.Builder = AlertDialog.Builder(this)
        build.setMessage("we need to send SMS for performing necessary task. Please permit the permission through" +
        "Setting screen. \n\n Select Permission -> Enable permission");
        build.setCancelable(false)
        build.setPositiveButton("Permit Manually") { dialogInterface: DialogInterface, i: Int ->
            var intent = Intent()
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            var uri = Uri.fromParts("package", packageName, null)
            intent.setData(uri)
            startActivity(intent)
        }

        build.setNegativeButton("Cancel", null);
        build.show()

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