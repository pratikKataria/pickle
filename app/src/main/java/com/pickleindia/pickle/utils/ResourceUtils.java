package com.pickleindia.pickle.utils;

import com.pickleindia.pickle.R;

import java.util.HashMap;
import java.util.Map;

public class ResourceUtils {

    public static Map<String, Integer> backgroundColors = new HashMap<>();
    public static Map<String, Integer> foregroundImages = new HashMap<>();

    static {
        //static background color
        backgroundColors.put(Constant.CAT_FOUR, R.color.icVegetableBg);
        backgroundColors.put(Constant.CAT_TWO, R.color.icVegetableBg);
        backgroundColors.put(Constant.CAT_THREE, R.color.icDairyBg);
        backgroundColors.put(Constant.CAT_ONE, R.color.icFruitBg);
        backgroundColors.put(Constant.CAT_FIVE, R.color.icHouseholdBg);
        backgroundColors.put(Constant.CAT_SIX, R.color.icDairyBg);

        //static product images
        foregroundImages.put(Constant.CAT_FOUR, R.drawable.ic_grocery);
        foregroundImages.put(Constant.CAT_TWO, R.drawable.ic_vegetable);
        foregroundImages.put(Constant.CAT_THREE, R.drawable.ic_dairy);
        foregroundImages.put(Constant.CAT_ONE, R.drawable.ic_fruits);
        foregroundImages.put(Constant.CAT_FIVE, R.drawable.ic_household);
        foregroundImages.put(Constant.CAT_SIX, R.drawable.ic_personal_care);
    }
}
