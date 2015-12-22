package com.romens.yjk.health.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.romens.yjk.health.db.entity.DiscoveryCollection;

/**
 * Created by siery on 15/8/14.
 */
public class ADFunctionEntity {
    public final String id;
    public final CharSequence name;
    public final int iconResId;
    private String actionValue;

    public ADFunctionEntity(String _id, CharSequence _name, int _iconResId) {
        this.id = _id;
        this.name = _name;
        this.iconResId = _iconResId;
    }

    public void setActionValue(String value) {
        actionValue = value;
    }

    public void onAction(Context context) {
        if (!TextUtils.isEmpty(actionValue)) {
            DiscoveryCollection.onFocusItemAction(context,id,actionValue);
        }

//        if (!TextUtils.isEmpty(actionValue)) {
//            if (actionValue.startsWith("http")) {
//
//            } else {
//                Intent intent = new Intent(actionValue);
//                context.startActivity(intent);
//            }
//        }
    }
}
