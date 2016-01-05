package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.romens.yjk.health.helper.MedicareHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
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

    public static void onActionForADWeb(Context context, Bundle data) {
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

    /**
     * 处理图片点击行为
     *
     * @param context
     * @param arguments
     */
    public static void onActonForImageAD(Context context, Bundle arguments) {
        int type = arguments.getInt("TYPE");
        if (type == 1) {
            String value = arguments.getString("VALUE");

            if (TextUtils.equals("YBZQ", value)) {
                //跳转到医保专区
                Intent intent = new Intent(MedicareHelper.INTENT_ACTION);
                intent.putExtra("title", "医保专区");
                context.startActivity(intent);
            }
        }else{

        }
    }

    public static void onActionForGroup(Context context, Bundle arguments) {
        if (arguments != null) {
            String action = arguments.getString("ACTION");
            if (TextUtils.equals("ACTION_HISTORY", action)) {
                UIOpenHelper.openHistoryActivity(context);
                //UIOpenHelper.openWeb(context, "测试促销", "http://139.129.4.154/api.html");
            } else if (TextUtils.equals("1", action)) {

            }
        }
    }

    public static void onActionForProduct(Context context, Bundle arguments) {
        if (arguments != null && arguments.containsKey("ID")) {
            String medicineId = arguments.getString("ID", "");
            UIOpenHelper.openMedicineActivity(context, medicineId);
        }
    }
}
