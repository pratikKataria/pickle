package com.example.pickle.utils;

import android.os.Bundle;

import static com.example.pickle.utils.Constant.BEVERAGES;
import static com.example.pickle.utils.Constant.DAIRY;
import static com.example.pickle.utils.Constant.FRUITS;
import static com.example.pickle.utils.Constant.PRODUCT_BUNDLE;
import static com.example.pickle.utils.Constant.VEGETABLES;

public class NavigationUtils {
    public static Bundle getNavBundle(String navigateTo) {
        Bundle bundle = new Bundle();
        switch (navigateTo) {
            case FRUITS:
                bundle.putString(PRODUCT_BUNDLE, FRUITS);
                return bundle;
            case BEVERAGES:
                bundle.putString(PRODUCT_BUNDLE, BEVERAGES);
                return bundle;
            case VEGETABLES:
                bundle.putString(PRODUCT_BUNDLE, VEGETABLES);
                return bundle;
            case DAIRY:
                bundle.putString(PRODUCT_BUNDLE, DAIRY);
                return bundle;
        }
        return bundle;
    }
}
