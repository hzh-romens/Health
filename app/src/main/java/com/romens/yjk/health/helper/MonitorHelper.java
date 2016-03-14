package com.romens.yjk.health.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

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
    public static final void init(Context context) {
        PgyCrashManager.register(context);
    }

    public static void postError(Context context, Exception e) {
        PgyCrashManager.reportCaughtException(context, e);
    }

    public static void unregisterUpdate() {
        PgyUpdateManager.unregister();
    }

    public static void checkUpdate(final Activity context) {
        checkUpdate(context, false);
    }

    public static void checkUpdate(final Activity context, final boolean showNoUpdate) {
        PgyUpdateManager.register(context, new UpdateManagerListener() {
            @Override
            public void onUpdateAvailable(final String result) {
                final AppBean appBean = getAppBeanFromString(result);
                if (appBean != null) {
                    int newVersion;
                    try {
                        newVersion = Integer.parseInt(appBean.getVersionCode());
                    } catch (NumberFormatException e) {
                        newVersion = 0;
                    }
                    int currVersion = MyApplication.getAppVersion();
                    if (newVersion > currVersion) {
                        String title = "新版本 " + appBean.getVersionName();
                        String content = appBean.getReleaseNote();
                        new AlertDialog.Builder(context)
                                .setTitle(title)
                                .setMessage(content)
                                .setPositiveButton("更新",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                startDownloadTask(context, appBean.getDownloadURL());
                                            }
                                        }).create().show();
                        return;
                    }
                }
            }

            @Override
            public void onNoUpdateAvailable() {
                if (showNoUpdate) {
                    Toast.makeText(context, "已经是最新版!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
