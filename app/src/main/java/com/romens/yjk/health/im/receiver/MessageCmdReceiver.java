package com.romens.yjk.health.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.easemob.chat.EMMessage;

/**
 * Created by siery on 15/7/13.
 */
public class MessageCmdReceiver extends BroadcastReceiver {
    public static final String ARGUMENTS_KEY_CMD_ACTION = "cmd_action";
    public static final String ARGUMENTS_KEY_CMD_MESSAGE = "cmd_message";


    //Action

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra(ARGUMENTS_KEY_CMD_ACTION);
        EMMessage message = intent.getParcelableExtra(ARGUMENTS_KEY_CMD_MESSAGE);
        handleCMDAction(action, message);
    }

    private void handleCMDAction(String action, EMMessage message) {
        if (TextUtils.isEmpty(action) || message == null) {
            return;
        }
    }
}
