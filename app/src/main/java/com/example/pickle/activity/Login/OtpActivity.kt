package com.example.pickle.activity.Login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Handler
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import com.example.pickle.R
import com.example.pickle.activity.Main.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_otp.*

class OtpActivity : AppCompatActivity() {

    var mAuth = FirebaseAuth.getInstance()
    private lateinit var editTextOtp: EditText
    private lateinit var verifyBtn: MaterialButton

    private var doubleBackToExitPressedOnce = false

    private fun init_fields() {
        editTextOtp = findViewById(R.id.activity_otp_et_enter_otp)
        verifyBtn = findViewById(R.id.activity_otp_mb_submit_otp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        init_fields()

        val AuthCredential : String = intent.getStringExtra("AuthCredentials")

        activity_otp_mb_submit_otp.setOnClickListener {
            if (editTextOtp.text.isEmpty()) {
                editTextOtp.error = "should not be empty"
                editTextOtp.requestFocus()
                return@setOnClickListener
            }

            if (editTextOtp.text.length < 6) {
                editTextOtp.error = "invalid"
                editTextOtp.requestFocus()
                return@setOnClickListener
            }

            val credential  = PhoneAuthProvider.getCredential(AuthCredential, editTextOtp.text.toString())
            activity_otp_progress.visibility = VISIBLE
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    activity_otp_progress.visibility = GONE
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
