package com.pickleindia.firebase

import com.google.firebase.auth.FirebaseAuth
import com.pickleindia.firebase.crud.Create
import com.pickleindia.firebase.crud.Delete
import com.pickleindia.firebase.crud.Read
import com.pickleindia.firebase.crud.Update

interface FirebaseFields {
    val uid get() = FirebaseAuth.getInstance().uid ?: ""
    val address get() = "Addresses"
    val customer get() = "Customers"
    val personalInformation get() = "personalInformation"
    val username get() = "username"
    val addressJson get() = "addressJson"
}

class FirebasePaths : Create, Read, Delete, Update {

}

