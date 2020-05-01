package com.example.pickle.activity.Login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import com.example.pickle.R
import com.example.pickle.activity.main.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
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

            if (!(editTextOtp.text.length == 6)) {
                editTextOtp.error = "invalid"
                editTextOtp.requestFocus()
                return@setOnClickListener
            }
            activity_otp_progress.visibility = VISIBLE
            val credential  = PhoneAuthProvider.getCredential(AuthCredential, editTextOtp.text.toString())
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    checkDoc()
                } else {
                    // Sign in failed, display a message and update the UI
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        activity_otp_progress.visibility = GONE
                        Toast.makeText(this@OtpActivity, "error: ${(task.exception as FirebaseAuthInvalidCredentialsException).message}", Toast.LENGTH_SHORT).show()
                    } else {
                        activity_otp_progress.visibility = GONE
                        Toast.makeText(this@OtpActivity, "error: ${(task.exception as FirebaseException).message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun checkDoc() {
        val reference = FirebaseDatabase.getInstance().getReference("Customers")
            .child(FirebaseAuth.getInstance().uid!!)
            .child("personalInformation")
            .child("username/")
            reference.keepSynced(true)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.value as String
                    Log.e("otp activity ", data)
                    Log.e("otp activity ", "$dataSnapshot")
                    checkDeviceToken()
                } else {
                    Handler().postDelayed( {
                        activity_otp_progress.visibility = GONE
                        startActivity(Intent(this@OtpActivity, CustomerDetailActivity::class.java))
                        finish()
                    }, 1200)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        })
    }

    private fun checkDeviceToken() {
        if (FirebaseAuth.getInstance().uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("Customers")
                .child(FirebaseAuth.getInstance().uid!!)
                .child("personalInformation")
                .child("deviceToken")
            ref.keepSynced(true)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        Log.e("Opt activity ", " curr ent token "+ dataSnapshot)
                        val token = dataSnapshot.value
                        if (token != null && token == FirebaseInstanceId.getInstance().token) {
                            activity_otp_progress.visibility = GONE
                            sendUserToHome()
                            Log.e("Opt activity ", " curr ent token "+ token)
                        } else {
                            ref.setValue(FirebaseInstanceId.getInstance().token) { _: DatabaseError?, _: DatabaseReference? ->
                                activity_otp_progress.visibility = GONE
                                Toast.makeText(this@OtpActivity, "Token Updated", Toast.LENGTH_SHORT).show()
                                sendUserToHome()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    activity_otp_progress.visibility = GONE
                    Toast.makeText(this@OtpActivity, "error ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
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
