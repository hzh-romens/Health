package com.romens.yjk.health.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.RemindDao;
import com.romens.yjk.health.db.entity.RemindEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Yu
 * @create 2016/4/14
 * @description
 */
public class RemindService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RemindDao remindDao = DBInterface.instance().openReadableDb().getRemindDao();
        List<RemindEntity> entities = remindDao.loadAll();
        for (int i = entities.size() - 1; i >= 0; i--) {
            if (entities.get(i).getIsRemind() == 0)
                entities.remove(i);
        }
        for (RemindEntity entity : entities) {
            //先取消提醒，不确定好用
            RemindReceiver.cancelRemind(getApplicationContext(), entity);
            setRemindTime(entity.getFirstTime(), entity);
            setRemindTime(entity.getSecondtime(), entity);
            setRemindTime(entity.getThreeTime(), entity);
            setRemindTime(entity.getFourTime(), entity);
            setRemindTime(entity.getFiveTime(), entity);
        }

        Intent myIntent = new Intent(getApplicationContext(), RemindAlarmServiceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        return START_STICKY;
    }

    public void setRemindTime(String timeStr, RemindEntity entity) {
        Calendar currentDate = Calendar.getInstance();
        long intervalTime = 1000 * 60 * 60 * 24 * entity.getIntervalDay();
        if (!timeStr.equals("-1")) {
            long remindTime = getTime(timeStr);
            if (remindTime < currentDate.getTimeInMillis()) {
                remindTime += intervalTime;
            }
            Intent intent = new Intent(getApplicationContext(), RemindReceiver.class);
            intent.putExtra("type", (int) remindTime);
            intent.putExtra("remindInfoEntity", entity);
            startAlarmRemind(getApplicationContext(), intent, remindTime, intervalTime, (int) remindTime);
        }
    }

    public static void startAlarmRemind(Context context, Intent intent, long startTime, long intervalTime, int type) {
        PendingIntent sender = PendingIntent.getBroadcast(context, type, intent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, sender);
    }


    //通过 08:00 获取时间戳
    private static long getTime(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            String t1 = dateFormat.format(new Date());
            String t2 = t1 + time;
            long t = timeFormat.parse(t2).getTime();
            return t;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
