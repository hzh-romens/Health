package com.romens.yjk.health.ui.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.romens.yjk.health.ui.AlarmActivity;

/**
 * Created by anlc on 2015/8/25.
 */
public class RemindReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, AlarmActivity.class);
        context.startActivity(intent);
    }
}
