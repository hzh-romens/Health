package com.romens.yjk.health.wx.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.PushMessageEntity;
import com.romens.yjk.health.service.UploadPushTokenService;
import com.romens.yjk.health.ui.controls.ADBaseControl;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGNotifaction;
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

    public static void handleNotify(XGNotifaction xGNotifaction) {
        Notification notification = xGNotifaction.getNotifaction();
        Intent intent = createNotifyIntent(xGNotifaction.getCustomContent());
        if (intent != null) {
            notification.contentIntent = PendingIntent.getActivity(MyApplication.applicationContext, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        NotificationManager notificationManager = (NotificationManager) MyApplication.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(xGNotifaction.getNotifyId(), notification);

    }

    private static Intent createNotifyIntent(String extras) {
        JsonNode jsonNode = PushMessageEntity.formatExtras(extras);
        if (jsonNode != null) {
            String action = jsonNode.get("ACTION").asText();
            if (TextUtils.equals(ADBaseControl.ACTION_MEDICARE, action)) {
                Intent intent = ADBaseControl.createActionMedicareIntent(MyApplication.applicationContext);
                return intent;
            }
        }
        return null;
    }

    public static void onPushMessageAction(Context context, PushMessageEntity entity) {
        if (entity.unRead()) {
            entity.setRead();
            DBInterface.instance().savePushMessage(entity);
            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onPushMessageStateChanged);
        }
        JsonNode jsonNode = entity.formatExtras();
        if (jsonNode != null) {
            String action = jsonNode.get("ACTION").asText();
            if (TextUtils.equals(ADBaseControl.ACTION_MEDICARE, action)) {
                ADBaseControl.onActionMedicare(context);
            }
        }
    }
}
