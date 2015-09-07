package com.romens.yjk.health.ui;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/8/25.
 */
public class AlarmActivity extends BaseActivity {

    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // create the instance of NotificationManager
//        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        // create the instance of Notification
//        Notification n = new Notification();
//        // n.sound=Uri.parse("file:///sdcard/alarm.mp3");
//        n.sound = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "20");
//        // Post a notification to be shown in the status bar
//        nm.notify(NOTIFICATION_ID, n);
//
//        TextView tv = (TextView) findViewById(R.id.tvNotification);
//        tv.setText("Hello, it's time to bla bla...");
//
//        Button btnCancel = (Button) findViewById(R.id.btnCancel);
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                nm.cancel(NOTIFICATION_ID);
//                finish();
//            }
//        });
        setNotification();
    }
    private void setNotification() {

//        NotificationManager fm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        Notification notification = new Notification();
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notification.icon = R.drawable.ic_launcher;
//        notification.tickerText = "hello...";
//
//        Intent notificationIntent = new Intent(this, AlarmActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(this, "title", "hello", contentIntent);
//
//        fm.notify(1, notification);

    }
}
