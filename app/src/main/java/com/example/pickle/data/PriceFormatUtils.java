package com.example.pickle.data;

public class PriceFormatUtils {
    public static String getStringFormattedPrice(int price) {
        return "\u20b9"+String.valueOf(price);
    }

    public int getIntFormattedPrice(String price) {
        return Integer.parseInt(price.substring(1));
    }
}
