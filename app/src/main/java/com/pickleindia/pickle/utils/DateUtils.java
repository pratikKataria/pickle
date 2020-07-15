package com.pickleindia.pickle.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    @SuppressLint("SimpleDateFormat")
    public static String getServerDate(long timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
            return  sdf.format(new Date((long)timestamp));
        } catch (Exception xe) {
            return "date";
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean isEqual(long timestamp) {
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
            String date = sfd.format(new Date(timestamp));
            return date.equals(new SimpleDateFormat("dd-MM-yyyy").format(new Date(System.currentTimeMillis())));
        } catch (Exception xe) {
            return false;
        }
    }

    /*
     * used to set date in include layout
     */
    @SuppressLint("SimpleDateFormat")
    public static String getNextDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date date = calendar.getTime();
        return new SimpleDateFormat("EEE dd MMM").format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate() {
        return new SimpleDateFormat("EEE dd MMM").format(new Date());
    }

    public static boolean currentTimeIsAfter(String start) {
        boolean isAhead = false;

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm");
            Date deliveryTime = simpleDateFormat.parse(start);
            Date orderTime = simpleDateFormat1.parse(new SimpleDateFormat("HH:mm").format(new Date()));

            Log.e("DateUtils", "delivery time " +deliveryTime.toString() +" order time " + orderTime.toString() );

            isAhead = orderTime.after(deliveryTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isAhead;
    }
}
