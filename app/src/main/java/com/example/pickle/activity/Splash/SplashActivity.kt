package com.example.pickle.activity.Splash

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pickle.R
import com.example.pickle.activity.Login.CustomerDetailActivity
import com.example.pickle.activity.Login.LoginActivity
import com.example.pickle.activity.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SplashActivity : AppCompatActivity() {

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
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //todo remove this in splash activity
//        startActivity(Intent(this, FirebaseSearchActivity::class.java))


        if (hasPermissions(this, permission)) {
            if (checkAuth())
                checkDoc()
            else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        } else {
            ActivityCompat.requestPermissions(this, permission, PERMISSION_ALL)
        }

    }

    private fun checkAuth(): Boolean {
        return FirebaseAuth.getInstance().uid != null
    }

    private fun checkDoc() {
        Log.e("check doc ", " chek doc")
        var reference = FirebaseDatabase.getInstance().getReference("Customers")
            .child(FirebaseAuth.getInstance().uid!!)
            .child("personalInformation")
            .child("username")
        reference.keepSynced(true)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    Log.e("check doc ", " chek doc $dataSnapshot")
                    startMain()
                } else {
                    Log.e("check doc ", " chek doc $dataSnapshot" + " null")
                    Handler().postDelayed( {
                        startActivity(Intent(this@SplashActivity, CustomerDetailActivity::class.java))
                        finish()
                    }, 1200)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
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
                if (checkAuth()) {
                    checkDoc()
                } else {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish();
                }
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

    private fun startMain() {
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish();
        }, 2000)
    }

    private fun hasPermissions(context: Context, permissions: Array<String>):
            Boolean = permissions.all { ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
}