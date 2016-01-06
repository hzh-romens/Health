package com.romens.yjk.health.helper;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;

/**
 * Created by siery on 16/1/5.
 */
public class MonitorHelper {
    public static final void init(Context context){
        PgyCrashManager.register(context);
    }

    public static void postError(Context context,Exception e){
        PgyCrashManager.reportCaughtException(context, e);
    }

    public static void checkUpdate(final Activity context){
        PgyUpdateManager.register(context,new UpdateManagerListener() {

                    @Override
                    public void onUpdateAvailable(final String result) {

                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        String currVersion=String.valueOf(MyApplication.getAppVersion());
                        if(!TextUtils.equals(appBean.getVersionCode(),currVersion)){
                            Uri uri = Uri.parse(appBean.getDownloadURL());
                            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                            String ticker=context.getString(R.string.app_name)+"有更新";
                            String title="新版本 "+appBean.getVersionName();
                            String content=appBean.getReleaseNote();
                            showNotification(MyApplication.applicationContext,100,ticker,title,content,pendingIntent);
                        }
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                    }
                });
    }

    public static void showNotification(Context context, int id, String tickerText, String title, String content, PendingIntent pendingIntent) {
        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setTicker(tickerText)
                .setSound(notificationUri)
                .setVibrate(new long[]{0, 180, 80, 120})
                .setAutoCancel(true);

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String tag = context.getPackageName();
        notificationManager.notify(tag, id, notification);
    }
}
