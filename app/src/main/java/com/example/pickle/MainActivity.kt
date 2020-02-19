package com.example.pickle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

//    private lateinit var mAuth : FirebaseAuth
//    private lateinit var mCurrentUser : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        mAuth = FirebaseAuth.getInstance()
//        mCurrentUser = mAuth.currentUser!!
    }

    override fun onStart() {
        super.onStart()
//        if (mCurrentUser == null) {
//            var loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
//            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(loginIntent)
//            finish()
//        }

        val mFirebaseUser = FirebaseAuth.getInstance()
        if (mFirebaseUser.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }



}
