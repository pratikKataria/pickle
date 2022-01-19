package com.pickleindia.firebase.crud

import com.pickleindia.firebase.CRUDMarker
import com.pickleindia.firebase.FirebaseFields

interface Create : FirebaseFields {
    val createAddress get() = "$address/$uid/$addressJson"
}