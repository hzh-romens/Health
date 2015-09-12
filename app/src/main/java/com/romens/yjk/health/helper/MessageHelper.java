package com.romens.yjk.health.helper;

import com.romens.android.log.FileLog;
import com.romens.yjk.health.db.entity.MessageObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by siery on 15/7/6.
 */
public class MessageHelper {
    public static final SimpleDateFormat formatterDay = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat formatterWeek = new SimpleDateFormat("EEE");
    public static final SimpleDateFormat formatterMonth = new SimpleDateFormat("MM-dd");
    public static final SimpleDateFormat formatterYear = new SimpleDateFormat("yy-MM-dd");

    public static String stringForMessageListDate(long date) {
        try {
            Calendar rightNow = Calendar.getInstance();
            int day = rightNow.get(Calendar.DAY_OF_YEAR);
            int year = rightNow.get(Calendar.YEAR);
            rightNow.setTimeInMillis(date);
            int dateDay = rightNow.get(Calendar.DAY_OF_YEAR);
            int dateYear = rightNow.get(Calendar.YEAR);

            if (year != dateYear) {
                return formatterYear.format(new Date(date));
            } else {
                int dayDiff = dateDay - day;
                if (dayDiff == 0 || dayDiff == -1 && (int) System.currentTimeMillis() - date < 60 * 60 * 8) {
                    return formatterDay.format(new Date(date));
                } else if (dayDiff > -7 && dayDiff <= -1) {
                    return formatterWeek.format(new Date(date));
                } else {
                    return formatterMonth.format(new Date(date));
                }
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
        return "XX";
    }

    public static boolean isLocationMessage(MessageObject message) {
        if (message == null) {
            return false;
        }
        return message.contentType == MessageObject.MESSAGE_TYPE_SENT_LOCATION || message.contentType == MessageObject.MESSAGE_TYPE_RECV_LOCATION;
    }
}
