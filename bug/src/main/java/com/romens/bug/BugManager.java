package com.romens.bug;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author Zhou Lisi
 * @create 2016-03-24 23:33
 * @description
 */
public class BugManager {
    private static volatile boolean isInit = false;

    public static final void init(Context context, BugConfig config) {
        if (config != null) {
            isInit = true;
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
            strategy.setAppChannel(config.getAppChannel());  //设置渠道
            strategy.setAppVersion(config.getAppVersion());      //App的版本
            strategy.setAppPackageName(config.getPackageName());  //App的包名
            CrashReport.initCrashReport(context, config.getAppId(), false, strategy);
        }
    }

    public static void createBugUser(String id) {
        CrashReport.setUserId(id);
    }

    public static void createBugUser(Context context, String key, String value) {
        int size = CrashReport.getUserDatasSize(context);
        if (size <= 9) {
            CrashReport.putUserData(context, key, value);
        }
    }

    public static void postCrash(Throwable throwable) {
        CrashReport.postCatchedException(throwable);
    }

    public static void postCrash(Context context, Throwable throwable) {
        postCrash(context, 1, throwable);
    }

    public static void postCrash(Context context, int tag, Throwable throwable) {
        CrashReport.setUserSceneTag(context, tag);
        CrashReport.postCatchedException(throwable);
    }
}
