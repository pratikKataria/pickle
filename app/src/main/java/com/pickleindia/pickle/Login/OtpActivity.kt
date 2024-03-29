package com.pickleindia.pickle.Login

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.pickleindia.pickle.R
import com.pickleindia.pickle.cart.CartActivity
import com.pickleindia.pickle.databinding.LayoutRewardGrantedAlertdialogBinding
import com.pickleindia.pickle.main.MainActivity
import com.pickleindia.pickle.utils.Constant.PERMISSION_PREFS_KEY
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.*
import kotlin.concurrent.timerTask

class OtpActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var editTextOtp: EditText
    private lateinit var verifyBtn: MaterialButton

    private lateinit var timer: Timer
    private lateinit var progressDialog: ProgressDialog

    private var doubleBackToExitPressedOnce = false

    private fun initFields() {
        editTextOtp = findViewById(R.id.activity_otp_et_enter_otp)
        verifyBtn = findViewById(R.id.activity_otp_mb_submit_otp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        initFields()

        val authCredential: String = intent.getStringExtra("AuthCredentials")

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
            val credential = PhoneAuthProvider.getCredential(authCredential, editTextOtp.text.toString())
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d("otp", "signInWithCredential $credential")
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    val isNew = it.additionalUserInfo?.isNewUser
                    if (isNew != null && isNew) {
                        createUser()
                    } else {
                        // if user is old user and has stored referral then this code invoked
                        val sharedPreferences: SharedPreferences = getSharedPreferences(PERMISSION_PREFS_KEY, 0)
                        val referredBy = sharedPreferences.getString("referredBy", "")
                        if (referredBy!!.isNotEmpty()) {
                            Toast.makeText(this@OtpActivity, "Referral only works for new user", Toast.LENGTH_LONG).show()
                            sharedPreferences.edit().remove("referredBy").apply()
                        }
                        updateDeviceTokenForOldAccount()
                    }
                }.addOnFailureListener {
                    if (it is FirebaseAuthInvalidCredentialsException)
                        Toast.makeText(this@OtpActivity, "error wrong otp", Toast.LENGTH_LONG).show()
                    Log.e(OtpActivity::class.java.name, it.message as String)
                    activity_otp_progress.visibility = GONE
                }
    }

    private fun createUser() {
        val updateData = userPersonalInformation()

        val referredBy = getReferral()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait while verifying...")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(R.style.progressDialog)
        val drawable = ProgressBar(this).indeterminateDrawable.mutate()
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        progressDialog.setIndeterminateDrawable(drawable)
        progressDialog.show()

        timer = Timer()
        val timerTask = timerTask {
            run {
                timer.cancel()
                progressDialog.dismiss()

                runOnUiThread { showAlertDialog() }
            }
        }
        timer.schedule(timerTask, 12000)

        if (referredBy.isEmpty()) {
            updateDataWithTimeOut(updateData, false)
            return
        }

        getReferrerPCoins(referredBy, object : ReferrerCoinListener {
            override fun received(coins: Int) {
                updateData["$referredBy/referralReward/pcoins"] = coins
                updateDataWithTimeOut(updateData, true)
            }
        })
    }

    private fun userPersonalInformation(): MutableMap<String, Any> {
        val updateData: MutableMap<String, Any> = mutableMapOf()
        updateData["${FirebaseAuth.getInstance().uid}/personalInformation/creationDate"] = ServerValue.TIMESTAMP
        updateData["${FirebaseAuth.getInstance().uid}/personalInformation/deviceToken"] = FirebaseInstanceId.getInstance().token as String
        updateData["${FirebaseAuth.getInstance().uid}/personalInformation/userId"] = FirebaseAuth.getInstance().uid as String
        updateData["${FirebaseAuth.getInstance().uid}/personalInformation/userPhoneNo"] = FirebaseAuth.getInstance().currentUser?.phoneNumber as String
        updateData["${FirebaseAuth.getInstance().uid}/personalInformation/username"] = " "

        val referredBy = getReferral()
        if (referredBy.isEmpty()) {
            updateData["${FirebaseAuth.getInstance().uid}/personalInformation/referredBy"] = "NaN"
            updateData["${FirebaseAuth.getInstance().uid}/referralReward/pcoins"] = 0
        } else {
            updateData["${FirebaseAuth.getInstance().uid}/personalInformation/referredBy"] = referredBy
            updateData["${FirebaseAuth.getInstance().uid}/referralReward/pcoins"] = 10
        }

        return updateData;
    }

    private fun getReferral(): String {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PERMISSION_PREFS_KEY, 0)
        return sharedPreferences.getString("referredBy", "").toString()
    }

    private fun getReferrerPCoins(referredBy: String, referreCoinListener: ReferrerCoinListener) {
        val referredByDatabaseReference = FirebaseDatabase.getInstance().getReference("Customers").child(referredBy).child("referralReward").child("pcoins")
        referredByDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("OtpActivity", "$snapshot ")
                val pcoins = if (snapshot.exists()) {
                    (snapshot.value as Long).toInt() + 10
                } else {
                    10
                }
                referreCoinListener.received(pcoins)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(OtpActivity::class.java.name, error.message);
            }
        })
    }

    interface ReferrerCoinListener {
        fun received(coins: Int)
    }

    private fun updateDataWithTimeOut(data: MutableMap<String, Any>, isReferredBySomeone: Boolean) {
        val reference = FirebaseDatabase.getInstance().getReference("Customers")

        reference.updateChildren(data).addOnSuccessListener {
            timer.cancel()
            progressDialog.dismiss()
            if (isReferredBySomeone) {
                showRewardGivenDialog()
            } else {
                setResult(1001)
                startActivity(Intent(this@OtpActivity, CustomerDetailActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }.addOnFailureListener {
            Log.e("OtpActivity", it.message + " ")
        }
    }

    private fun showAlertDialog() {
        val alertDialog = MaterialAlertDialogBuilder(this)
        alertDialog.setTitle("Failed to upload ").setMessage("please check your internet connection and retry to login").setPositiveButton("Retry") { _, _ ->
            createUser()
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showRewardGivenDialog() {
        var alertDialog: AlertDialog? = null
        val alertDialogBuilder = MaterialAlertDialogBuilder(this)
        val binding: LayoutRewardGrantedAlertdialogBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.layout_reward_granted_alertdialog,
                null,
                false
        )
        alertDialogBuilder.setView(binding.root)
        binding.nextBtn.setOnClickListener {
            val sharedPreferences: SharedPreferences = getSharedPreferences(PERMISSION_PREFS_KEY, 0)
            sharedPreferences.edit().remove("referredBy").apply()
            setResult(1001)
            startActivity(Intent(this@OtpActivity, CustomerDetailActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
            if (alertDialog != null)
                alertDialog!!.dismiss()
        }

        alertDialog?.setCancelable(false)
        alertDialog = alertDialogBuilder.show()
    }


    private fun updateDeviceTokenForOldAccount() {
        val tokenDatabaseReference = FirebaseDatabase.getInstance().getReference("Customers")
                .child(FirebaseAuth.getInstance().uid!!)
                .child("personalInformation")
                .child("deviceToken")

        tokenDatabaseReference.setValue(FirebaseInstanceId.getInstance().token as String).addOnSuccessListener {
            checkAddress()
        }.addOnFailureListener {
            Log.e("OtpActivity", it.message as String)
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
                            setResult(1001)
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
        val bundle = intent.extras
        val intent = if (bundle != null && bundle.containsKey(CartActivity::class.java.name)) {
            Intent(this@OtpActivity, CartActivity::class.java)
        } else {
            Intent(this@OtpActivity, MainActivity::class.java)
        }

        setResult(1001)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "double tap back to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}
