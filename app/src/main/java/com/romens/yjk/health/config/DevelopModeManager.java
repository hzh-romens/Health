package com.romens.yjk.health.config;

import android.content.Context;
import android.content.Intent;

import com.romens.yjk.health.BuildConfig;
import com.romens.yjk.health.ui.activity.DevelopModeActivity;

/**
 * @author Zhou Lisi
 * @create 16/2/23
 * @description
 */
public class DevelopModeManager {

    public static boolean enable() {
        return BuildConfig.ENABLE_DEVELOP_MODE;
    }

    public static void openDebug(Context context) {
        if (enable()) {
            Intent intent = new Intent(context, DevelopModeActivity.class);
            context.startActivity(intent);
        }
    }
}
