package com.example.pickle.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String getDate(long timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return  sdf.format(new Date((long)timestamp));
        } catch (Exception xe) {
            return "date";
        }
    }

    public static boolean isEqual(long timestamp) {
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
            String date = sfd.format(new Date(timestamp));
            Log.e("date ", date);
            return date.equals(new SimpleDateFormat("dd-MM-yyyy").format(new Date(System.currentTimeMillis())));
        } catch (Exception xe) {
            return false;
        }
    }
}
