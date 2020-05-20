
package com.example.pickle.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.pickle.models.ProductModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/**
 * A pack of helpful getter and setter methods for reading/writing to {@link SharedPreferences}.
 */
final public class SharedPrefsUtils {
    private SharedPrefsUtils() {}

    /**
     * Helper method to retrieve a String value from {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param i
     * @return The value from shared preferences, or null if the value could not be read.
     */
    public static String getStringPreference(Context context, String key, int i) {
        String value = null;
       try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences != null) {
                value = preferences.getString(key, null);
            }
        } catch (Exception xe) {
           Log.e("SharedPrefsUtils", xe.getMessage());
       }
        return value;
    }

    /**
     * Helper method to write a String value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setStringPreference(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * Helper method to retrieve a float value from {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    public static float getFloatPreference(Context context, String key, float defaultValue) {
        float value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getFloat(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write a float value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setFloatPreference(Context context, String key, float value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * Helper method to retrieve a long value from {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    public static long getLongPreference(Context context, String key, long defaultValue) {
        long value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getLong(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write a long value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setLongPreference(Context context, String key, long value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * Helper method to retrieve an integer value from {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    public static int getIntegerPreference(Context context, String key, int defaultValue) {
        int value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getInt(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write an integer value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setIntegerPreference(Context context, String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * Helper method to retrieve a boolean value from {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        boolean value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getBoolean(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write a boolean value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setBooleanPreference(Context context, String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * Helper method to write a boolean value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @return true if the new value was successfully written to persistent storage.
     * @Auth pratik katariya
     * @date 9/2019/03
     */
    public static void removeValuePreference(Context context, String key) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(key);
                editor.apply();
            }
        } catch (Exception xe) {
            Log.e("SharedPrefUtils", xe.getMessage());
        }
    }

    public static boolean clearCart(Context context) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor  editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            return true;
        } catch (Exception xe) {
            Log.e(SharedPrefsUtils.class.getName(), xe.getMessage());
            return  false;
        }
    }

    public static ArrayList<ProductModel> getAllProducts(Context context) {
        ArrayList<ProductModel> productModels = new ArrayList<>();
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Map<String, ?> prefEntry = preferences.getAll();
//            Log.e(SharedPrefsUtils.class.getName(), "" + prefEntry);
            for (String key : prefEntry.keySet()) {
                String productGson = (String) prefEntry.get(key);
                ProductModel productModel = new Gson().fromJson(productGson, ProductModel.class);
                productModels.add(productModel);
            }
            return productModels;
        } catch (Exception xe) {
            Log.e(SharedPrefsUtils.class.getName(), xe.getMessage());
            return productModels;
        }
    }
}