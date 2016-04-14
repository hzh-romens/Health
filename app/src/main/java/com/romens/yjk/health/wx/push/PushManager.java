package com.romens.yjk.health.wx.push;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.service.UploadPushTokenService;
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

    public static void setPushInfo(int errorCode, String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("push_register_result", errorCode);
        editor.putString("push_token", token == null ? "" : token);
        editor.commit();
    }

    public static String getPushToken(Context context) {
        return XGPushConfig.getToken(context);
    }

    public static void doUpload(Context context) {
        String pushToken = getPushToken(context);
        doUpload(context, pushToken);
    }

    public static void doUpload(Context context, String token) {
        Intent serviceIntent = new Intent(context, UploadPushTokenService.class);
        serviceIntent.putExtra("PUSHTOKEN", token);
        context.startService(serviceIntent);
    }
}
