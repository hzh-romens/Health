package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.romens.extend.scanner.Intents;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.ui.cells.ADHolder;

/**
 * Created by siery on 15/8/14.
 */
public abstract class ADBaseControl {
    public int sortIndex;

    public abstract void bindViewHolder(Context context, ADHolder holder);

    public abstract int getType();

    public void setSortIndex(int index) {
        sortIndex = index;
    }

    public static void onActionForADWeb(Context context,Bundle data) {
        Intent intent = new Intent("com.romens.yjk.health.AD_WEB");
        intent.putExtras(data);
        context.startActivity(intent);
    }

    public static String createADWebUrl(String... params) {
        String url = null;
        if (params.length == 3) {
            url = String.format("%s?token=%s&guid=%s", params[0], params[1], params[2]);
        }
        return url;
    }
}
