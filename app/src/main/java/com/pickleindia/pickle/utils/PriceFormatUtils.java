package com.pickleindia.pickle.utils;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PriceFormatUtils {
    /**
     * this will format price form int to string and append rupees symbol at start
     * @param price
     * @return
     */
    public static String getStringFormattedPrice(double price) {
        return new DecimalFormat("\u20b9#,##0.00").format(price);
    }

    public static String getStringDoublePrice(double price) {
        return new DecimalFormat("#,##0.00").format(price);
    }

    public static int getIntFormattedPrice(String price) {
        Log.e("PriceFormatUtils" , price.substring(0) + " " + price.substring(1));
        try {
            return Integer.parseInt(price);
        } catch (NumberFormatException xe) {
            return Integer.parseInt(price.substring(1));
        }
    }

    public static double getDoubleFormat(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
