package com.romens.yjk.health;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.text.TextUtils;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.mobvoi.android.common.MobvoiApiManager;
import com.mobvoi.android.common.NoAvailableServiceException;
import com.pgyersdk.crash.PgyCrashManager;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.PgyConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.im.IMHXSDKHelper;

/**
 * Created by zhoulisi on 15/1/15.
 */
public class MyApplication extends ApplicationLoader {
    private static boolean applicationInited = false;

    @Override
    public void onCreate() {
        super.onCreate();
        PgyCrashManager.register(this, PgyConfig.APP_ID);
        try {
            MobvoiApiManager.getInstance().adaptService(applicationContext);
        } catch (NoAvailableServiceException e) {
            FileLog.e("init tic api error", e);
        }
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

    public static void initEMChat() {
        /**
         * this function will initialize the HuanXin SDK
         *
         * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
         *
         * 环信初始化SDK帮助函数
         * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
         *
         * for example:
         * 例子：
         *
         * public class DemoHXSDKHelper extends HXSDKHelper
         *
         * HXHelper = new DemoHXSDKHelper();
         * if(HXHelper.onInit(context)){
         *     // do HuanXin related work
         * }
         */
        final String hxAppId = UserConfig.getHXAppId();
        if (!TextUtils.isEmpty(hxAppId)) {
            EMChat.getInstance().setAppkey(hxAppId);
            IMHXSDKHelper.getInstance().onInit();
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
            FileLog.e("tmessages", "screen state = " + isScreenOn);
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }

        UserConfig.loadConfig();
        if (UserConfig.isClientLogined()) {
            FacadeToken.getInstance().init();
        }
        FileLog.e("yunnuo_im", "app initied");
    }
}
