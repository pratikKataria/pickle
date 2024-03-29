package com.pickleindia.pickle.Login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.pickleindia.pickle.R
import com.pickleindia.pickle.cart.CartActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var phoneNumber: EditText
    private lateinit var countryCode: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var countDownTimer: TextView
    private lateinit var sendOtpButton: MaterialButton
    private lateinit var timer: CountDownTimer

    private fun initFields() {
        progressBar = findViewById(R.id.activity_login_pb_counter)
        countryCode = findViewById(R.id.activity_login_et_country_code)
        sendOtpButton = findViewById(R.id.activity_login_mb_send_otp)
        phoneNumber = findViewById(R.id.activity_login_et_phone_number)
        countDownTimer = findViewById(R.id.activity_login_tv_count_down_time)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initFields()

        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countDownTimer.text = "" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                countDownTimer.text = ""
                progressBar.visibility = INVISIBLE
                sendOtpButton.isEnabled = true
            }
        }

        sendOtpButton.setOnClickListener {

            if (phoneNumber.text.isEmpty()) {
                phoneNumber.error = "should not be empty"
                phoneNumber.requestFocus()
                return@setOnClickListener
            }

            if (!(phoneNumber.text.length == 10)) {
                phoneNumber.error = "invalid number"
                phoneNumber.requestFocus()
                return@setOnClickListener
            }

            timer.cancel()

            progressBar.visibility = VISIBLE
            countDownTimer.visibility = VISIBLE


            val completePhoneNumber = countryCode.text.toString() + phoneNumber.text.toString()

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    completePhoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    mCallBacks
            )

            timer.start()
        }



        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e)
                timer.cancel()
                progressBar.visibility = INVISIBLE
                countDownTimer.visibility = INVISIBLE

                if (e is FirebaseAuthInvalidCredentialsException) {
                    activity_login_tv_error.visibility = VISIBLE
                    activity_login_tv_error.text = e.message
                } else if (e is FirebaseTooManyRequestsException) {
                    activity_login_tv_error.visibility = VISIBLE
                    activity_login_tv_error.text = e.message
                } else {
                    activity_login_tv_error.visibility = VISIBLE
                    activity_login_tv_error.text = e.message
                }

                Handler().postDelayed({
                    activity_login_tv_error.visibility = GONE
                    activity_login_tv_error.text = ""
                }, 10000)
            }

            override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("Login", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token

                Handler().postDelayed(
                        {
                            val otpIntent = Intent(this@LoginActivity, OtpActivity::class.java)
                            otpIntent.putExtra("AuthCredentials", verificationId)

                            val bundle = intent.extras
                            if (bundle != null && bundle.containsKey(CartActivity::class.java.name)) {
                                otpIntent.putExtra(CartActivity::class.java.name, "cart")
                            }

                            startActivityForResult(otpIntent, requestCode)
                            progressBar.visibility = GONE
                            timer.cancel()
                            sendOtpButton
                        }, 1500
                )
                Toast.makeText(this@LoginActivity, "otp sent", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val requestCode = 1001

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == requestCode && this != null) {
            finish()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}