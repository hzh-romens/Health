package com.romens.yjk.health.db;

import android.text.TextUtils;

/**
 * Created by siery on 15/8/20.
 */
public class DBHelper {
    public static String toDBString(String value){
        return TextUtils.isEmpty(value)?"":value;
    }
}
