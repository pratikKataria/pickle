package com.example.pickle.splash

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import com.example.pickle.R
import com.example.pickle.main.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    private val PERMISSION_ALL = 110

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


        val sharedPreferences = getSharedPreferences("permissions", 0)
        if (sharedPreferences.getBoolean("FIRST_RUN", true)) {
            startActivity(
                Intent(this@SplashActivity, OnBoardingActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                ).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            )
            sharedPreferences.edit().putBoolean("FIRST_RUN", false).apply()
            finish()
        } else {
            if (hasPermissions(this, required_permission)) {
                startActivityMain()
            } else {
                ActivityCompat.requestPermissions(this, permission, PERMISSION_ALL)
            }
        }


        //set status transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                statusBarColor = Color.TRANSPARENT
            }
        }


        //handle animation
        var constrantSet = ConstraintSet();
        var view = splashActivity
        view.post {

            Handler().postDelayed({
                var hText = logoText;
                hText.animateText(" pickle kare so aaj kar")
            }, 800)

            val transition = ChangeBounds()
            transition.interpolator = OvershootInterpolator(2.0f)
            constrantSet.clone(this@SplashActivity, R.layout.splash_activity_off);
            TransitionManager.beginDelayedTransition(view, transition)
            constrantSet.applyTo(view)
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
            if (allImportantPermissionGranted)
                startActivityMain()
            else
                displayNeverAskAgainDialog()
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

    override fun onResume() {
        super.onResume()
        if (hasPermissions(this, required_permission)) {
            startActivityMain()
        }
    }
}