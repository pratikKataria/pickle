package com.example.pickle.activity.Login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pickle.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_otp.*

class OtpActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var editTextOtp: EditText
    private lateinit var verifyBtn: MaterialButton

    private var doubleBackToExitPressedOnce = false

    private fun initFields() {
        editTextOtp = findViewById(R.id.activity_otp_et_enter_otp)
        verifyBtn = findViewById(R.id.activity_otp_mb_submit_otp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        initFields()

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
                    setDeviceToken()
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

    private fun setDeviceToken() {
        if (FirebaseAuth.getInstance().uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("Customers")
                .child(FirebaseAuth.getInstance().uid!!)
                .child("personalInformation")
                .child("deviceToken")
            ref.keepSynced(true)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val token = dataSnapshot.value

                        activity_otp_progress.visibility = GONE
                        ref.setValue(FirebaseInstanceId.getInstance().token) { _: DatabaseError?, _: DatabaseReference? ->
                                activity_otp_progress.visibility = GONE
                                checkAddress()
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

    private fun checkAddress() {
        var databaseReference = FirebaseDatabase.getInstance().getReference("Addresses")
        if (FirebaseAuth.getInstance().uid != null) {
            databaseReference.child(FirebaseAuth.getInstance().uid!!).child("slot1");
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        sendUserToHome()
                    } else {
                        startActivity(
                            Intent(
                                this@OtpActivity,
                                CustomerDetailActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        finish()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@OtpActivity, "error: " + p0.message, Toast.LENGTH_SHORT)
                        .show();
                }
            })

        }
    }


    private fun sendUserToHome() {
//        val homeIntent = Intent(this@OtpActivity, MainActivity::class.java)
//        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        startActivity(homeIntent)
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
