package com.romens.yjk.health.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.im.ui.IMChatActivity;

/**
 * Created by siery on 15/9/11.
 */
public class IMHelper {
    public static void openChatActivity(Context context, String chatId, int chatType) {
        if (!TextUtils.equals(chatId, UserConfig.getClientUserId())) {
            Bundle arguments = new Bundle();
            arguments.putInt(IMChatActivity.ARGUMENT_CHAT_TYPE, chatType);
            arguments.putString(IMChatActivity.ARGUMENT_CHAT_ID, chatId);
            Intent intent = new Intent(context, IMChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtras(arguments);
            context.startActivity(intent);
        }
    }
}
