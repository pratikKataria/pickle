package com.example.pickle.utils;

import com.example.pickle.R;

import java.util.HashMap;
import java.util.Map;

import static com.example.pickle.utils.Constant.BEVERAGES;
import static com.example.pickle.utils.Constant.DAIRY;
import static com.example.pickle.utils.Constant.FRUITS;
import static com.example.pickle.utils.Constant.VEGETABLES;

public class ResourceUtils {
    public static Map<String, Integer> backgroundColors = new HashMap<>();
    public static Map<String, Integer> foregroundImages = new HashMap<>();

    static {
        //static background color
        backgroundColors.put(BEVERAGES, R.color.icBeveragesBg);
        backgroundColors.put(VEGETABLES, R.color.icVegetableBg);
        backgroundColors.put(DAIRY, R.color.icDairyBg);
        backgroundColors.put(FRUITS, R.color.icFruitBg);

        //static product images
        foregroundImages.put(BEVERAGES, R.drawable.ic_beverages);
        foregroundImages.put(VEGETABLES, R.drawable.ic_vegetable);
        foregroundImages.put(DAIRY, R.drawable.ic_dairy);
        foregroundImages.put(FRUITS, R.drawable.ic_fruits);
    }
}
