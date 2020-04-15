package com.example.pickle.utils;

import android.util.Log;

public class PriceFormatUtils {
    public static String getStringFormattedPrice(int price) {
        return "\u20b9"+String.valueOf(price);
    }

    public static int getIntFormattedPrice(String price) {
        Log.e("PriceFormatUtils" , price.substring(0) + " " + price.substring(1));
        try {
            return Integer.parseInt(price);
        } catch (NumberFormatException xe) {
            return Integer.parseInt(price.substring(1));
        }
    }
}
