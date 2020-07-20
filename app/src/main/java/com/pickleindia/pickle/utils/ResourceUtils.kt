package com.pickleindia.pickle.utils

import com.pickleindia.pickle.R
import java.util.*

object ResourceUtils {
    @JvmField
    var backgroundColors: MutableMap<String, Int> = HashMap()
    @JvmField
    var foregroundImages: MutableMap<String, Int> = HashMap()

    init {
        //static background color
        backgroundColors[Constant.CAT_FOUR] = R.color.icVegetableBg
        backgroundColors[Constant.CAT_TWO] = R.color.icVegetableBg
        backgroundColors[Constant.CAT_THREE] = R.color.icDairyBg
        backgroundColors[Constant.CAT_ONE] = R.color.icFruitBg
        backgroundColors[Constant.CAT_FIVE] = R.color.icHouseholdBg
        backgroundColors[Constant.CAT_SIX] = R.color.icDairyBg

        //static product images
        foregroundImages[Constant.CAT_FOUR] = R.drawable.ic_grocery
        foregroundImages[Constant.CAT_TWO] = R.drawable.ic_vegetable
        foregroundImages[Constant.CAT_THREE] = R.drawable.ic_dairy
        foregroundImages[Constant.CAT_ONE] = R.drawable.ic_fruits
        foregroundImages[Constant.CAT_FIVE] = R.drawable.ic_household
        foregroundImages[Constant.CAT_SIX] = R.drawable.ic_personal_care
    }
}