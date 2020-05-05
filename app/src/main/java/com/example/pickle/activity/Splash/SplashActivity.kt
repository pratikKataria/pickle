package com.example.pickle.activity.Splash

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pickle.R
import com.example.pickle.activity.main.MainActivity


class SplashActivity : AppCompatActivity() {

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
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (hasPermissions(this, permission)) {
            startActivityMain()
        } else {
            ActivityCompat.requestPermissions(this, permission, PERMISSION_ALL)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //permission_granted = 0 // permission_denied = -1
        if (PERMISSION_ALL == requestCode) {

            var allImportantPermissionGranted = true
            for (index in permissions.indices) {
                for (reqIndex in required_permission.indices) {
                    if (grantResults.isNotEmpty() && permissions[index] == required_permission[reqIndex] && grantResults[index] == PackageManager.PERMISSION_DENIED) {
                        allImportantPermissionGranted = false
                    }
                }
            }
            if (allImportantPermissionGranted) {
                startActivityMain();
            }else displayNeverAskAgainDialog()

        }
    }

    private fun displayNeverAskAgainDialog() {
        val build: AlertDialog.Builder = AlertDialog.Builder(this)
        build.setMessage(
            "we need get location and send SMS for performing necessary task. Please permit the permission through " +
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

    private fun hasPermissions(context: Context, permissions: Array<String>):
            Boolean = permissions.all { ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    private fun startActivityMain() {
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish();
        }, 1500)
    }
}