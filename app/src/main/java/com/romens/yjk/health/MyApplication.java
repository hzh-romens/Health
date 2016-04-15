package com.romens.yjk.health;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.PowerManager;

import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.android.network.request.ConnectManager;
import com.romens.bug.BugConfig;
import com.romens.bug.BugManager;
import com.romens.images.CloudImagesManager;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.wx.push.PushManager;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;

import java.util.List;

/**
 * Created by zhoulisi on 15/1/15.
 */
public class MyApplication extends ApplicationLoader {
    private static boolean applicationInited = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // 在主进程设置信鸽相关的内容
        if (isMainProcess()) {
            // 为保证弹出通知前一定调用本方法，需要在application的onCreate注册
            // 收到通知时，会调用本回调函数。
            // 相当于这个回调会拦截在信鸽的弹出通知之前被截取
            // 一般上针对需要获取通知内容、标题，设置通知点击的跳转逻辑等等
            XGPushManager
                    .setNotifactionCallback(new XGPushNotifactionCallback() {

                        @Override
                        public void handleNotify(XGNotifaction xGNotifaction) {
                            PushManager.handleNotify(xGNotifaction);
                        }
                    });
        }

        //初始化网路库
        ConnectManager.setEnableLog(BuildConfig.ENABLE_DEVELOP_MODE);
        //初始化日志
        FileLog.setEnableLog(BuildConfig.ENABLE_DEVELOP_MODE);
        FileLog.setEnableSystemLog(BuildConfig.ENABLE_DEVELOP_MODE);
        //初始化图片库
        CloudImagesManager.init(applicationContext);

        //2016-03-25 zhoulisi Crash渠道由蒲公英切换为腾讯Bugly
        //MonitorHelper.init(this);
        BugConfig bugConfig = new BugConfig.Builder(this, BuildConfig.BUG_APP_ID)
                .withPackageName(BuildConfig.BUG_PACKAGE)
                .withAppVersion(BuildConfig.BUG_VERSION)
                .withAppChannel(BuildConfig.BUG_CHANNEL)
                .build();
        BugManager.init(getApplicationContext(), bugConfig);

//        //初始化环信SDK
//        IMHXSDKHelper.getInstance().onInit(MyApplication.applicationContext);
        UserConfig.getInstance().loadConfig();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            AndroidUtilities.checkDisplaySize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postInitApplication(boolean isAutoLogin) {
        if (applicationInited) {
            return;
        }

        applicationInited = true;

        try {
            PowerManager pm = (PowerManager) applicationContext.getSystemService(Context.POWER_SERVICE);
            isScreenOn = pm.isScreenOn();
            FileLog.e("romens", "screen state = " + isScreenOn);
        } catch (Exception e) {
            FileLog.e("romens", e);
        }

        UserConfig.getInstance().loadConfig();
//        if (UserConfig.isClientLogined()) {
//            FacadeToken.getInstance().init();
//        }
        FileLog.e("romens", "app initied");
    }

    public boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
