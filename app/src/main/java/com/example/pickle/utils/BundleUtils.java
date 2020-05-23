package com.example.pickle.utils;

import android.os.Bundle;

import com.example.pickle.R;

import static com.example.pickle.utils.Constant.BEVERAGES;
import static com.example.pickle.utils.Constant.DAIRY;
import static com.example.pickle.utils.Constant.FRUITS;
import static com.example.pickle.utils.Constant.PRODUCT_BUNDLE;
import static com.example.pickle.utils.Constant.VEGETABLES;

public class BundleUtils {

    public static Bundle setNavigationBundle(String navigateTo) {

        Bundle bundle = new Bundle();

        switch (navigateTo) {
            case FRUITS:
                bundle.putString(PRODUCT_BUNDLE, FRUITS);
                bundle.putInt("IMAGE_RES", R.drawable.ic_fruits);
                bundle.putInt("BACKGROUND_COLOR", R.color.icFruitBg);
                return bundle;
            case BEVERAGES:
                bundle.putString(PRODUCT_BUNDLE, BEVERAGES);
                bundle.putInt("IMAGE_RES", R.drawable.ic_beverages);
                bundle.putInt("BACKGROUND_COLOR", R.color.icBeveragesBg);
                return bundle;
            case VEGETABLES:
                bundle.putString(PRODUCT_BUNDLE, VEGETABLES);
                bundle.putInt("IMAGE_RES", R.drawable.ic_vegetable);
                bundle.putInt("BACKGROUND_COLOR", R.color.icVegetableBg);
                return bundle;
            case DAIRY:
                bundle.putString(PRODUCT_BUNDLE, DAIRY);
                bundle.putInt("IMAGE_RES", R.drawable.ic_vegetable);
                bundle.putInt("BACKGROUND_COLOR", R.color.icDairyBg);
                return bundle;
        }
        return bundle;
    }
}
