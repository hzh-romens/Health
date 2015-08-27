package com.romens.yjk.health;

import android.content.res.Configuration;

import com.mobvoi.android.common.MobvoiApiManager;
import com.mobvoi.android.common.NoAvailableServiceException;
import com.pgyersdk.crash.PgyCrashManager;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.yjk.health.config.PgyConfig;

/**
 * Created by zhoulisi on 15/1/15.
 */
public class MyApplication extends ApplicationLoader {

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
}
