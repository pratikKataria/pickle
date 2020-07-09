package com.pickleindia.pickle.Login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.pickleindia.pickle.R
import com.pickleindia.pickle.cart.CartActivity
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

        val authCredential : String = intent.getStringExtra("AuthCredentials")

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
            val credential  = PhoneAuthProvider.getCredential(authCredential, editTextOtp.text.toString())
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    val isNew = it.additionalUserInfo?.isNewUser
                    if (isNew != null && isNew) {
                        updateAccountDetails()
                    } else {
                        updateDeviceTokenForOldAccount()
                    }
                }.addOnFailureListener {
                    Log.e(OtpActivity::class.java.name, it.message as String)
                }
    }

    private fun updateAccountDetails() {
        val reference = FirebaseDatabase.getInstance().getReference("Customers")
                .child(FirebaseAuth.getInstance().uid!!)
                .child("personalInformation")

        val data: MutableMap<String, Any> = mutableMapOf()
        data["creationDate"] = ServerValue.TIMESTAMP
        data["deviceToken"] = FirebaseInstanceId.getInstance().token as String
        data["userId"] = FirebaseAuth.getInstance().uid as String
        data["userPhoneNo"] = FirebaseAuth.getInstance().currentUser?.phoneNumber as String
        data["username"] = " "

        reference.setValue(data).addOnSuccessListener {
            startActivity(Intent(this@OtpActivity, CustomerDetailActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }

    }

    private fun updateDeviceTokenForOldAccount() {
        val tokenDatabaseReference = FirebaseDatabase.getInstance().getReference("Customers")
                .child(FirebaseAuth.getInstance().uid!!)
                .child("personalInformation")
                .child("deviceToken")

        tokenDatabaseReference.setValue(FirebaseInstanceId.getInstance().token as String).addOnSuccessListener {
            checkAddress()
        }.addOnFailureListener {
            Log.e("OtpActivity", it.message as String);
        }
    }

    private fun checkAddress() {
        var databaseReference = FirebaseDatabase.getInstance().getReference("Addresses")
        databaseReference.child(FirebaseAuth.getInstance().uid!!).child("slot1")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            sendUserToHome()
                        } else {
                            startActivity(Intent(this@OtpActivity, CustomerDetailActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            finish()
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@OtpActivity, "error: " + databaseError.message, Toast.LENGTH_SHORT).show()
                    }
                })
    }


    private fun sendUserToHome() {
        val homeIntent = Intent(this@OtpActivity, CartActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
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
