package com.romens.yjk.health.wx.push;

import android.content.Context;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

/**
 * Created by siery on 15/12/8.
 */
public class PushManager {
    public static boolean init(Context context) {
        XGPushConfig.enableDebug(context, false);
        return true;
    }

    public static void register(Context context) {
        XGPushManager.registerPush(context.getApplicationContext());
    }

    public static void register(Context context, XGIOperateCallback callback) {
        XGPushManager.registerPush(context.getApplicationContext(), callback);
    }

    public static String getToken(Context context) {
        return XGPushConfig.getToken(context);
    }

    public static void setTag(Context context, String tag) {
        XGPushManager.setTag(context, tag);
    }

}
