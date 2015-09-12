package com.romens.yjk.health.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.romens.android.ApplicationLoader;

/**
 * Created by siery on 15/7/7.
 */
public class SystemConfig {
    private final String fileName = "system_config.ini";
    private final static Object sync = new Object();
    private SharedPreferences sharedPreferences;

    private static volatile SystemConfig Instance = null;

    public static SystemConfig getInstance() {
        SystemConfig localInstance = Instance;
        if (localInstance == null) {
            synchronized (SystemConfig.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new SystemConfig();
                }
            }
        }
        return localInstance;
    }

    public SystemConfig() {
        sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public boolean enableSendMessageUserEnterKey() {
        return sharedPreferences.getBoolean("enableSendMessageUserEnterKey", false);
    }

    public void setEnableSendMessageUserEnterKey(boolean enable) {
        synchronized (sync) {
            sharedPreferences.edit().putBoolean("enableSendMessageUserEnterKey", enable).commit();
        }
    }
}
