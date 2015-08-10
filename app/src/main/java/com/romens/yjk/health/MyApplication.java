package com.romens.yjk.health;

import android.content.res.Configuration;

import com.pgyersdk.crash.PgyCrashManager;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.yjk.health.config.PgyConfig;

/**
 * Created by zhoulisi on 15/1/15.
 */
public class MyApplication extends ApplicationLoader {

    @Override
    public void onCreate() {
        super.onCreate();
        //注册蒲公英 Crash接口
        //蒲公英注册或上传应用获取的AppId
        PgyCrashManager.register(this, PgyConfig.APP_ID);
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
}
