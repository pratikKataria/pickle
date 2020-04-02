package com.example.pickle.data

import java.io.Serializable
import java.lang.Double.NaN
import java.util.*
import kotlin.math.roundToInt

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

) : Serializable {

    constructor() : this(
        "",
        "",
        -9999999,
        -9999999,
        -9999999,
        "",
        -9999999,
        "",
        "",
        "",
        "",-9999999, Date(), true, "", ""

    )
}