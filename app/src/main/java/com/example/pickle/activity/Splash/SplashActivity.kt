package com.example.pickle.activity.Splash

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pickle.R
import com.example.pickle.activity.Main.MainActivity
import com.example.pickle.utils.PermissionUtils


class SplashActivity : AppCompatActivity() {

    private val SEND_LOCATION_PERMISSION_REQUEST_CODE = 10000
    private val TAG = "TEST"
    private val PERMISSION_ALL = 1
    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.INTERNET,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CONTACTS
    )

    private val required_permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)




        if (!hasPermissions(this, permission)) {
            ActivityCompat.requestPermissions(this, permission, PERMISSION_ALL)
        }

    }

    override fun onStart() {
        super.onStart()
        if (hasPermissions(this, permission)) {
            startNextActivity()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //permission_granted = 0 // permission_denied = -1
        if (PERMISSION_ALL == requestCode) {
//            for (index in permissions.indices) {
//                if (grantResults.isNotEmpty() && grantResults[index] == PackageManager.PERMISSION_DENIED ) {
//                    displayNeverAskAgainDialog()
//                    Log.e(TAG, "<|> if block <|>  + grantResults  ${grantResults[index]} + permissions +  ${permissions[index]} +  requestCode  + $requestCode")
//                }
//            }
            var isAllPermitted = true
            for (index in permissions.indices) {
                for (reqIndex in required_permission.indices) {
                    if (grantResults.isNotEmpty() && permissions[index] == required_permission[reqIndex] && grantResults[index] == PackageManager.PERMISSION_DENIED) {
                        isAllPermitted = false
                    }
                }
            }
            if (isAllPermitted) startNextActivity()
            else displayNeverAskAgainDialog()

//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
//                Log.e(TAG, "if block <|> Permission : <|> + grantResults  $grantResults + permissions +  $permissions +  requestCode  + $requestCode")
//            } else {
//                PermissionUtils().setShouldShowStatus(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                Log.e(TAG, "else block <|> Permission : <|> + grantResults  $grantResults + permissions +  $permissions +  requestCode  + $requestCode")
//            }
        }
    }

    private fun displayNeverAskAgainDialog() {
        val build: AlertDialog.Builder = AlertDialog.Builder(this)
        build.setMessage(
            "we need to send SMS for performing necessary task. Please permit the permission through " +
                    "Setting screen. \n\n Select Permission -> Enable permission"
        )
        build.setCancelable(false)
        build.setPositiveButton("Permit Manually") { _: DialogInterface, _: Int ->
            val intent = Intent()
            intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        build.show()

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

    override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        return super.shouldShowRequestPermissionRationale(permission)
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}