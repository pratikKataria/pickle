package com.pickleindia.firebase.crud

import com.pickleindia.firebase.CRUDMarker

interface Update : CRUDMarker {
    val updateCustomerPersonalInformation get() = "$customer/$uid/$personalInformation/$username"

}