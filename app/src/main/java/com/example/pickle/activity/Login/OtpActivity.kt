package com.example.pickle.activity.Login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import com.example.pickle.R
import com.example.pickle.activity.Main.MainActivity
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_otp.*

class OtpActivity : AppCompatActivity() {

    var mAuth = FirebaseAuth.getInstance()

    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val AuthCredential : String = intent.getStringExtra("AuthCredentials")

        activity_otp_mb_login.setOnClickListener {
            if (activity_otp_et.text.isEmpty()) {
                activity_otp_et.error = "should not be empty"
                activity_otp_et.requestFocus()
                return@setOnClickListener
            }

            if (activity_otp_et.text.length < 6) {
                activity_otp_et.error = "invalid"
                activity_otp_et.requestFocus()
                return@setOnClickListener
            }

            var credential  = PhoneAuthProvider.getCredential(AuthCredential, activity_otp_et.text.toString())
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    sendUserToHome()
                    val user = task.result?.user
                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    private fun sendUserToHome() {
        val homeIntent = Intent(this@OtpActivity, MainActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(homeIntent)
        finish()
    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "double tap back to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({doubleBackToExitPressedOnce = false}, 2000)
    }
}
