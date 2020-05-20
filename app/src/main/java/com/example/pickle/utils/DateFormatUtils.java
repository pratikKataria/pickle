package com.example.pickle.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {
    public static String getDate(Long timestamp) {
        if (timestamp == null) {
            return "";
        }
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return  sfd.format(new Date((long)timestamp*1000));
        } catch (Exception xe) {
            return "date";
        }
    }
}
