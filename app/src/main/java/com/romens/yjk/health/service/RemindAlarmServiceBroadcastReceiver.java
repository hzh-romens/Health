package com.romens.yjk.health.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Zhou Lisi
 * @create 16/4/14
 * @description
 */
public class RemindAlarmServiceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, RemindService.class);
        context.startService(serviceIntent);
    }

}
