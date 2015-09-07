package com.romens.yjk.health.ui.components;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.ui.AddRemindActivity;
import com.romens.yjk.health.ui.AlarmActivity;

/**
 * Created by anlc on 2015/8/25.
 */
public class RemindReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        int type = intent.getIntExtra("type", -1);
        RemindEntity entity = (RemindEntity) intent.getSerializableExtra("remindInfoEntity");
        if (type != -1 && entity != null) {
            showNotification(type, entity);
        }
    }

    private void showNotification(int type, RemindEntity entity) {
        NotificationManager fm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(context).setContentTitle("要健康提醒您")// 设置通知栏标题
                .setContentText(entity.getUser()+"服药："+entity.getDrug()) // 设置通知栏显示内容</span>
                .setTicker("要健康提醒您") // 通知首次出现在通知栏，带上升动画效果的
                .setPriority(Notification.PRIORITY_DEFAULT) // 设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.drawable.ic_launcher)
                .build();// 设置通知小ICON

        fm.notify(type, notification);
    }
}
