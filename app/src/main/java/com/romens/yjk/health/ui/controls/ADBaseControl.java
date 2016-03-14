package com.romens.yjk.health.ui.controls;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.helper.FormatHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.activity.ADWebActivity;
import com.romens.yjk.health.ui.activity.MedicineGroupActivity;
import com.romens.yjk.health.ui.cells.ADHolder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by siery on 15/8/14.
 */
public abstract class ADBaseControl {
    public static final String ACTION_WEB_AD = "WEB_AD";
    public int sortIndex;

    public abstract void bindViewHolder(Context context, ADHolder holder);

    public abstract int getType();

    public void setSortIndex(int index) {
        sortIndex = index;
    }

    public static void onActionForADWeb(Context context, Bundle data) {
        Intent intent = new Intent(context, ADWebActivity.class);
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
            String action = arguments.getString("ACTION");
            if (TextUtils.equals("YBZQ", action)) {
                //跳转到医保专区
                ComponentName component = new ComponentName(context, MedicineGroupActivity.class.getName());
                Intent intent = new Intent();
                intent.setComponent(component);
                intent.putExtra("title", "医保专区");
                context.startActivity(intent);
            } else if (TextUtils.equals("OPENCLASS", action)) {
                String id = arguments.getString("ID");
                String name = arguments.getString("NAME");
                UIOpenHelper.openMedicineClass(context, id, name);
            } else if (TextUtils.equals("OPENGOODS", action)) {
                onActionForProduct(context, arguments);
            }
        } else {
            String action = arguments.getString("ACTION");
            if (TextUtils.equals(ACTION_WEB_AD, action)) {
                String id = arguments.getString("ID");
                String value = arguments.getString("VALUE");
                String url = formatADWebUrl(id, value);
                String name = arguments.getString("NAME");
                UIOpenHelper.openWebActivity(context, name, url);
            }
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

    public static String formatADWebUrl(String id, String value) {
        String url = value;
        if (!TextUtils.isEmpty(id)) {
            List<String> ids = FormatHelper.stringToList(id);
            int size = ids.size();
            String valueTemp;
            for (int i = 0; i < size; i++) {
                valueTemp = getPrimaryIdValue(ids.get(i));
                url = url.replace("{" + i + "}", valueTemp == null ? "" : valueTemp);
            }
        }
        if (url.startsWith("/")) {
            url = FacadeConfig.HOST + url;
        }
        return url;
    }

    public static String getPrimaryIdValue(String id) {
        if (TextUtils.equals("操作人员", id)) {
            return UserConfig.getInstance().getClientUserId();
        } else if (TextUtils.equals("TOKEN", id)) {
            String token = FacadeToken.getInstance().getAuthToken();
            try {
                token = URLEncoder.encode(token, "utf-8");
            } catch (UnsupportedEncodingException e) {

            }
            return token;
        }
        return id;
    }
}
