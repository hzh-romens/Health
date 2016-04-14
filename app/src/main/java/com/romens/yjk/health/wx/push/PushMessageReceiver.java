package com.romens.yjk.health.wx.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.PushMessageEntity;
import com.romens.yjk.health.service.RemindService;
import com.romens.yjk.health.service.UploadPushTokenService;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by siery on 15/12/8.
 */
public abstract class PushMessageReceiver extends XGPushBaseReceiver {

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context,
                                          XGPushShowedResult xgPushShowedResult) {
        if (context == null || xgPushShowedResult == null) {
            return;
        }
        Long messageId = xgPushShowedResult.getMsgId();
        String title = xgPushShowedResult.getTitle();
        String content = xgPushShowedResult.getContent();
        String customContent = xgPushShowedResult.getCustomContent();
        PushMessageEntity messageEntity = new PushMessageEntity(title, content, customContent, messageId);
        DBInterface.instance().savePushMessage(messageEntity);
    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }

    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> tags = sharedPreferences.getStringSet("push_tags", new HashSet<String>());

            if (!tags.contains(tagName)) {
                tags.add(tagName);
                sharedPreferences.edit().putStringSet("push_tags", tags).commit();
            }
        }
    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> tags = sharedPreferences.getStringSet("push_tags", new HashSet<String>());

            if (tags.contains(tagName)) {
                tags.remove(tagName);
                sharedPreferences.edit().putStringSet("push_tags", tags).commit();
            }
        }
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {

    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
    @Override
    public void onNotifactionClickedResult(Context context,
                                           XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }

        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            onNotificationClicked(context, message);
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            onNotificationCleared(context, message);
        } else {
            onNotificationOtherAction(context, message);
        }
    }

    protected void onNotificationClicked(Context context, XGPushClickedResult message) {
    }

    protected void onNotificationCleared(Context context, XGPushClickedResult message) {
    }

    protected void onNotificationOtherAction(Context context, XGPushClickedResult message) {
    }

    @Override
    public void onRegisterResult(Context context, int errorCode,
                                 XGPushRegisterResult message) {
        // TODO Auto-generated method stub
        if (context == null || message == null) {
            return;
        }
        String token = null;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            // 在这里拿token
            token = message.getToken();
        }
        PushManager.setPushInfo(errorCode, token);

        PushManager.doUpload(context, token);
    }

    public static void handleNotify(XGNotifaction xGNotifaction) {
        Notification notification = xGNotifaction.getNotifaction();
        ComponentName componentName = new ComponentName(MyApplication.applicationContext, "com.romens.yjk.health.ui.MyOrderActivity");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        notification.contentIntent = PendingIntent.getActivity(MyApplication.applicationContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) MyApplication.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(xGNotifaction.getNotifyId(), notification);
    }

}

