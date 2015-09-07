package com.romens.yjk.health.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.romens.yjk.health.MyApplication;

import java.util.Calendar;

/**
 * Created by siery on 15/9/7.
 */
public class HomeConfig {
    private static final String PREFERENCE_NAME = "home_config";
    public static final String KEY_DATA = "data";

    public static boolean checkCacheValid() {
        long currTimestamp = Calendar.getInstance().getTimeInMillis();
        long cacheTimestamp = getCacheTimestamp();
        if ((currTimestamp - cacheTimestamp) > 600000) {
            return false;
        }
        return true;
    }

    public static long getCacheTimestamp() {
        SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        long timestamp = preferences.getLong("timestamp", 0);
        return timestamp;
    }

    public static String loadConfig() {
        SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String config = preferences.getString(KEY_DATA, "");
        return config;
    }

    public static boolean saveConfig(String config) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("timestamp", timestamp);
        editor.putString(KEY_DATA, config);
        return editor.commit();
    }
}
