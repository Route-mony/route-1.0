package com.beyondthehorizon.routeapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatter {

    public DateFormatter() {
    }

    public static String formatMonthDayYear(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date value = parseDateFromUTC(dateTime);
        if (value == null) {
            return null;
        }
        return dateFormat.format(value);
    }

    private static Date parseDateFromUTC(String dateTime) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatter.parse(dateTime);
        } catch (Exception e) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                return formatter.parse(dateTime);
            } catch (Exception ex) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return formatter.parse(dateTime);
                } catch (Exception exy) {
                    exy.printStackTrace();
                }
            }
        }
        return null;
    }
}