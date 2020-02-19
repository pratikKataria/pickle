package com.example.pickle

import android.content.Intent
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var mCallBacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        activity_login_mb_gen_otp.setOnClickListener {

            if (activity_login_et_phone_number.text.isEmpty()) {
                activity_login_et_phone_number.error = "should not be empty"
                activity_login_et_phone_number.requestFocus()
                return@setOnClickListener
            }

            if (activity_login_et_phone_number.text.length < 10) {
                activity_login_et_phone_number.error = "invalid number"
                activity_login_et_phone_number.requestFocus()
                return@setOnClickListener
            }

            activity_login_pb.visibility = VISIBLE
            activity_login_mb_gen_otp.isEnabled = false

            val comp_phn_number = activity_login_et_country_code.text.toString() + activity_login_et_phone_number.text.toString()

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                comp_phn_number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallBacks
            )
        }


        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.


//                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token

//                startActivity(Intent(this@LoginActivity, ))
                Toast.makeText(this@LoginActivity, "code sent", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
