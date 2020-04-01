package com.example.pickle.data

import android.accessibilityservice.GestureDescription
import java.util.*

data class ProductModel(

    var itemName : String,
    var itemDesc : String,
    var itemBasePrice : Int,
    var itemSellPrice : Int,
    var itemMaxQtyPerUser : Int,
    var itemOffers : String,
    var itemQty : Int,

    var qtyType :String,
    var itemType: String,
    var itemCategory: String,

    var itemId: String,
    var itemUnits:Int,
    var date: Date,
    var itemAvailability: Boolean,

    var itemImageUrl: String,
    var itemThumbImage: String

)