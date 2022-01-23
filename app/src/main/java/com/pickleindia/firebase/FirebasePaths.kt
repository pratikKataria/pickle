package com.pickleindia.firebase

import com.google.firebase.auth.FirebaseAuth

class FirebaseFields {
    companion object {
        val uid get() = FirebaseAuth.getInstance().uid ?: ""
        const val address = "Addresses"
        const val customer = "Customers"
        const val personalInformation = "personalInformation"
        const val username = "username"
        const val addressJson = "addressJson"
    }
}

class FirebasePaths {
    companion object {
        val ADD_ADDRESS = "${FirebaseFields.address}/${FirebaseFields.uid}/${FirebaseFields.addressJson}"
        val GET_ADDRESS = "${FirebaseFields.address}/${FirebaseFields.uid}/${FirebaseFields.addressJson}"
    }
}


