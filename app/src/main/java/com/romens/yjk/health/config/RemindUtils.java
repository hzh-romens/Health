package com.romens.yjk.health.config;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.ui.components.RemindReceiver;
import com.romens.yjk.health.ui.utils.TransformDateUitls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by anlc on 2016/3/24.
 */
public class RemindUtils {
    //添加提醒到系统中
    public static void setRemind(RemindEntity entity, Context context) {
        long startDateLong = TransformDateUitls.getDate(entity.getStartDate());
        setRemindTime(startDateLong, entity.getFirstTime(), entity, context);
        setRemindTime(startDateLong, entity.getSecondtime(), entity, context);
        setRemindTime(startDateLong, entity.getThreeTime(), entity, context);
        setRemindTime(startDateLong, entity.getFourTime(), entity, context);
        setRemindTime(startDateLong, entity.getFiveTime(), entity, context);
    }

    public static void setRemindTime(long startDateLong, String timeStr, RemindEntity entity, Context context) {
        Calendar currentDate = Calendar.getInstance();
        long intervalTime = 1000 * 60 * 60 * 24 * entity.getIntervalDay();
        if (!timeStr.equals("-1")) {
//            long time = TransformDateUitls.getTimeLong(timeStr);
//            long remindTime = (startDateLong+time) + currentDate.getTimeZone().getRawOffset();
//            long startTime = TransformDateUitls.getTimeLong(timeStr) + startDateLong;
//            long remindTime = startTime + currentDate.getTimeZone().getRawOffset();
            long remindTime = getTime(timeStr) ;
            if (remindTime < currentDate.getTimeInMillis()) {
                remindTime += intervalTime;
            }
            Intent intent = new Intent(context, RemindReceiver.class);
            intent.putExtra("type", (int) remindTime);
            intent.putExtra("remindInfoEntity", entity);
            startAlarmRemind(intent, remindTime, intervalTime, (int) remindTime, context);
        }
    }

    public static void startAlarmRemind(Intent intent, long startTime, long intervalTime, int type, Context context) {
        PendingIntent sender = PendingIntent.getBroadcast(context, type, intent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, sender);
    }

    public static void cancelRemind(RemindEntity entity, Context context) {
        long startDateLong = TransformDateUitls.getDate(entity.getStartDate());
        setCancelRemindTime(startDateLong, entity.getFirstTime(), context);
        setCancelRemindTime(startDateLong, entity.getSecondtime(), context);
        setCancelRemindTime(startDateLong, entity.getThreeTime(), context);
        setCancelRemindTime(startDateLong, entity.getFourTime(), context);
        setCancelRemindTime(startDateLong, entity.getFiveTime(), context);
    }

    public static void setCancelRemindTime(long startDateLong, String timeStr, Context context) {
        Calendar currentDate = Calendar.getInstance();
        if (!timeStr.equals("-1")) {
            long time = TransformDateUitls.getTimeLong(timeStr);
            long remindTime = (startDateLong + time) + currentDate.getTimeZone().getRawOffset();
            Intent intent = new Intent(context, RemindReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, (int) remindTime, intent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            manager.cancel(sender);
        }
    }

    //通过 08:00 获取时间戳
    private static long getTime(String time){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd ");
        SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            String t1 = dateFormat.format(new Date()) ;
            String t2 = t1+time ;
            long t = timeFormat.parse(t2).getTime() ;
            return t ;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
