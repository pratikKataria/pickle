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
import com.example.pickle.activity.Main.MainActivity
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
                    checkDoc()
                } else {
                    // Sign in failed, display a message and update the UI
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this@OtpActivity, "error: ${(task.exception as FirebaseAuthInvalidCredentialsException).message}", Toast.LENGTH_SHORT).show()
                    } else
                        Toast.makeText(this@OtpActivity, "error: ${(task.exception as FirebaseException).message}",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkDoc() {
        val reference = FirebaseDatabase.getInstance().getReference("Customers")
            .child(FirebaseAuth.getInstance().uid!! + "/c_uid")
            reference.keepSynced(true)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    checkDeviceToken("Customers", FirebaseAuth.getInstance().uid!!)
                } else {
                    Handler().postDelayed( {
                        startActivity(Intent(this@OtpActivity, CustomerDetailActivity::class.java))
                    }, 1200)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        })
    }

    private fun checkDeviceToken(path: String, id: String) {
        if (FirebaseAuth.getInstance().uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference(path)
            ref.keepSynced(true)
            ref.child("$id/d_token").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        val token = dataSnapshot.getValue(String::class.java)
                        if (token != null && token == FirebaseInstanceId.getInstance().token) {
                            Toast.makeText(this@OtpActivity, "Token Verified", Toast.LENGTH_SHORT).show()
                            sendUserToHome()
                        } else {
                            ref.child("$id/d_token").setValue(FirebaseInstanceId.getInstance().token) {
                                    databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
                                    Toast.makeText(this@OtpActivity, "Token Changed", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
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
