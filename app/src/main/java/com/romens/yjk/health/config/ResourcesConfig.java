package com.romens.yjk.health.config;

import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/8/18.
 */
public class ResourcesConfig {
    public static int primaryColor;

    static{
        primaryColor= MyApplication.applicationContext.getResources().getColor(R.color.theme_primary);
    }
}