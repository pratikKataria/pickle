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
            SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM hh:mm a");
            return sdf.format(new Date((long) timestamp));
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

    public static boolean isPastOrder(long timestamp) {
        long diff = new Date().getTime() - timestamp;
        long diffHours = diff / (60 * 60 * 1000);
        return diffHours > 24;
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

            Log.e("DateUtils", "delivery time " + deliveryTime.toString() + " order time " + orderTime.toString());

            isAhead = orderTime.after(deliveryTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isAhead;
    }

    /**
     * @param orderDate
     * @param amount
     * @param hour
     * @param minute
     * @return
     */
    public static boolean orderDateIsBeforeCancelDate(long orderDate, int amount, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(orderDate));

        calendar.add(Calendar.DATE, amount);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        Date currentTime = new Date();
        Date cancelTime = calendar.getTime();

        Log.e("DateUtils", currentTime.toString() + " cancel Time" + cancelTime.toString());
        return currentTime.before(cancelTime);
    }

    public static boolean orderTimeIsAfter(long orderDate, String time) {
        boolean isValid = false;
        try {
            Date date = new Date(orderDate);
            Date orderTime = new SimpleDateFormat("hh:mm a").parse(new SimpleDateFormat("hh: mm a").format(date));
            Date cancelTime = new SimpleDateFormat("hh:mm a").parse(time);

            if (orderTime.after(cancelTime)) {
                isValid = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    public static boolean orderDateIsToday(long orderDate) {
        boolean isToday = false;

        Date date = new Date(orderDate);
        Date currDate = new Date();

        return date.getDate() == currDate.getDate();
    }

    public static boolean isCartProductIsValid(long cartAddedDate) {
        Date addedDate = new Date(cartAddedDate);
        Date currDate = new Date();
        return currDate.getDate() - addedDate.getDate() >= 2;
    }
}
