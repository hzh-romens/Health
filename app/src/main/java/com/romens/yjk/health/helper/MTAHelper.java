package com.romens.yjk.health.helper;

import android.content.Context;

import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.config.FacadeConfig;
import com.tencent.stat.StatService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by siery on 15/12/25.
 */
public class MTAHelper {

    /**
     * 测试终端网络速度
     */
    public static void testHostSpeed() {
        Map<String, Integer> map = new HashMap<>();
        map.put(FacadeConfig.HOST, 80);
        StatService.testSpeed(MyApplication.applicationContext, map);
    }


    public static void pushError(Context context, Throwable error) {
        StatService.reportException(context, error);
    }

    public static void pushError(Context context, String error) {
        StatService.reportError(context, error);
    }
}
