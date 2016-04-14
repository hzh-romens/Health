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
import com.romens.yjk.health.ui.components.RemindReceiver;
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

    private Context context ;
    private RemindDao mRemindDao ;
    private List<RemindEntity> mRemindEntities ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this ;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getReminds() ;
        setReminds(mRemindEntities);
        return START_STICKY;
    }

    private void getReminds(){
        mRemindDao = DBInterface.instance().openReadableDb().getRemindDao();
        mRemindEntities = mRemindDao.loadAll() ;
        for (int i=mRemindEntities.size()-1;i>=0;i--){
            if (mRemindEntities.get(i).getIsRemind() == 0)
                mRemindEntities.remove(i) ;
        }
    }

    private void setReminds(List<RemindEntity> reminds){
        for (int i=0;i<reminds.size();i++) {
            setRemindTime(reminds.get(i).getFirstTime(), reminds.get(i));
            setRemindTime(reminds.get(i).getSecondtime(), reminds.get(i));
            setRemindTime(reminds.get(i).getThreeTime(), reminds.get(i));
            setRemindTime(reminds.get(i).getFourTime(), reminds.get(i));
            setRemindTime(reminds.get(i).getFiveTime(), reminds.get(i));
        }
    }

    public void setRemindTime(String timeStr, RemindEntity entity) {
        Calendar currentDate = Calendar.getInstance();
        long intervalTime = 1000 * 60 * 60 * 24 * entity.getIntervalDay();
        if (!timeStr.equals("-1")) {
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

    private void setRemind(RemindEntity remind){

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
