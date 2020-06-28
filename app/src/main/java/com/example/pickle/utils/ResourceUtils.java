package com.example.pickle.utils;

import com.example.pickle.R;

import java.util.HashMap;
import java.util.Map;

import static com.example.pickle.utils.Constant.CAT_FOUR;
import static com.example.pickle.utils.Constant.CAT_THREE;
import static com.example.pickle.utils.Constant.CAT_ONE;
import static com.example.pickle.utils.Constant.CAT_TWO;

public class ResourceUtils {
    public static Map<String, Integer> backgroundColors = new HashMap<>();
    public static Map<String, Integer> foregroundImages = new HashMap<>();

    static {
        //static background color
        backgroundColors.put(CAT_FOUR, R.color.icVegetableBg);
        backgroundColors.put(CAT_TWO, R.color.icVegetableBg);
        backgroundColors.put(CAT_THREE, R.color.icDairyBg);
        backgroundColors.put(CAT_ONE, R.color.icFruitBg);

        //static product images
        foregroundImages.put(CAT_FOUR, R.drawable.ic_grocery);
        foregroundImages.put(CAT_TWO, R.drawable.ic_vegetable);
        foregroundImages.put(CAT_THREE, R.drawable.ic_dairy);
        foregroundImages.put(CAT_ONE, R.drawable.ic_fruits);
    }
}
