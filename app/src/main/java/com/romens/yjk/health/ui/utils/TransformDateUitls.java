package com.romens.yjk.health.ui.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by anlc on 2015/8/27.
 */
public class TransformDateUitls {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public static String getYearDate(long dateLong) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(new Date(dateLong));
        int year = dateCalendar.get(Calendar.YEAR);
        int month = dateCalendar.get(Calendar.MONTH) + 1;
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        return year + "年" + month + "月" + day;
    }

    public static String getLong(Date date) {
        return dateFormat.format(date);
    }

    public static long getDate(String dateStr) {
        long date = 0;
        try {
            date = dateFormat.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getHourDate(long dateLong) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(new Date(dateLong));
        int hour = timeCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = timeCalendar.get(Calendar.MINUTE);
        return hour + ":" + minute;
    }

    public static Date getDate(long dateLong) {
        return new Date(dateLong);
    }

    public static long getDateLong(String dateStr) {
        return Long.parseLong(dateStr);
    }

    public static long getTimeLong(String timeStr) {
        long timeLong = 0;
        try {
            timeLong = timeFormat.parse(timeStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeLong;
    }
}
