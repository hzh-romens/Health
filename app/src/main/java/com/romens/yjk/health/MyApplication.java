package com.romens.yjk.health;

import android.content.Context;
import android.content.res.Configuration;
import android.os.PowerManager;

import com.pgyersdk.crash.PgyCrashManager;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.android.network.request.ConnectManager;
import com.romens.images.CloudImagesManager;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.helper.MonitorHelper;
import com.romens.yjk.health.im.IMHXSDKHelper;

/**
 * Created by zhoulisi on 15/1/15.
 */
public class MyApplication extends ApplicationLoader {
    private static boolean applicationInited = false;

    @Override
    public void onCreate() {
        super.onCreate();
        ConnectManager.setEnableLog(BuildConfig.ENABLE_DEVELOP_MODE);
        FileLog.setEnableLog(BuildConfig.ENABLE_DEVELOP_MODE);
        CloudImagesManager.init(applicationContext);
        MonitorHelper.init(this);
        //初始化环信SDK
        IMHXSDKHelper.getInstance().onInit(MyApplication.applicationContext);
        UserConfig.loadConfig();
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

        UserConfig.loadConfig();
//        if (UserConfig.isClientLogined()) {
//            FacadeToken.getInstance().init();
//        }
        FileLog.e("romens", "app initied");
    }
}
