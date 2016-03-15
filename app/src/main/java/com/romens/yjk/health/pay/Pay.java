package com.romens.yjk.health.pay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.yjk.health.ui.activity.pay.AlipayPayActivity;
import com.romens.yjk.health.ui.activity.pay.PayPrepareActivity;

import java.util.Iterator;

/**
 * @author Zhou Lisi
 * @create 16/3/2
 * @description
 */
public class Pay {
    public static final int PAY_TYPE_ONLINE = 0;
    public static final int PAY_TYPE_OFFLINE = 1;
    public static final int PAY_TYPE_YB_ONLINE = 2;

    private final String[] payTypeKeys = new String[]{"PAY_ONLINE", "PAY_OFFLINE", "YB_PAY_ONLINE"};
    private final String[] payTypes = new String[]{"在线支付", "货到付款", "医保在线支付"};

//    public static final int DELIVERY_TYPE_STORE = 0;
//    public static final int DELIVERY_TYPE_HOME = 1;
//
//    private final String[] deliveryTypeKeys = new String[]{"DELIVERY_STORE", "DELIVERY_HOME"};
//    private final String[] deliveryTypes = new String[]{"到店自提", "送货上门"};

    private final SparseArray<DeliveryMode> supportDeliveryModes = new SparseArray<>();

    private static volatile Pay instance;

    private Pay() {
    }

    public static Pay getInstance() {
        Pay localInstance = instance;
        if (localInstance == null) {
            synchronized (Pay.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Pay();
                }
            }
        }

        return localInstance;
    }

    public void clearDelivery() {
        supportDeliveryModes.clear();
    }

    public void setupDelivery(JsonNode data) {
        supportDeliveryModes.clear();
        if (data != null) {
            int size = data.size();
            String key;
            String desc;
            for (int i = 0; i < size; i++) {
                JsonNode item=data.get(i);
                key = item.get("ANDROIDGUID").asText();
                if (TextUtils.equals("DELIVERY_STORE", key)) {
                    desc = "推荐到店自提,有机会得到积分加倍累计";
                } else if (TextUtils.equals("DELIVERY_HOME", key)) {
                    desc = "优先由最近的门店派送,支持在线支付或者现金、微信或支付宝等当面付";
                } else {
                    desc = null;
                }
                supportDeliveryModes.append(i, new DeliveryMode(i, item.get("GUID").asText(), key, item.get("NAME").asText(), desc));
            }
        }
    }

    public int getSupportDeliveryModesCount() {
        return supportDeliveryModes.size();
    }

    public DeliveryMode getSupportDeliveryModeForIndex(int index) {
        return supportDeliveryModes.valueAt(index);
    }

    public DeliveryMode getSupportDeliveryMode(int id) {
        return supportDeliveryModes.get(id);
    }

    public String getPayTypeKey(int id) {
        return payTypeKeys[id];
    }

    public String getPayType(int id) {
        return payTypes[id];
    }

    public int getPayTypeId(String key) {
        int index = -1;
        for (int i = 0; i < payTypeKeys.length; i++) {
            if (TextUtils.equals(key, payTypeKeys[i])) {
                index = i;
                break;
            }
        }
        return index;
    }

//    public String getDeliveryTypeKey(int id) {
//        return deliveryTypeKeys[id];
//    }
//
//    public String getDeliveryType(int id) {
//        return deliveryTypes[id];
//    }
//
//    public int getDeliveryTypeId(String key) {
//        int index = -1;
//        for (int i = 0; i < deliveryTypeKeys.length; i++) {
//            if (TextUtils.equals(key, deliveryTypeKeys[i])) {
//                index = i;
//                break;
//            }
//        }
//        return index;
//    }

    public Intent createPayPrepareComponentName(Context context, String payTypeKey) {
        Intent intent = null;
        if (!TextUtils.isEmpty(payTypeKey)) {
            int id = getPayTypeId(payTypeKey);

            if (id == PAY_TYPE_YB_ONLINE) {
                intent = new Intent();
                ComponentName componentName = new ComponentName(context.getPackageName(), context.getPackageName() + ".ui.activity.MedicarePayActivity");
                intent.setComponent(componentName);
            } else {
                intent = new Intent(context, PayPrepareActivity.class);
            }
        }
        return intent;
    }

    public Intent createPayComponentName(Context context, String payMode) {
        Intent intent = null;
        if (!TextUtils.isEmpty(payMode)) {

            if (TextUtils.equals(payMode, PayMode.PAY_MODE_WX)) {
                intent = new Intent();
                ComponentName componentName = new ComponentName(context.getPackageName(), context.getPackageName() + ".wxapi.WXPayEntryActivity");
                intent.setComponent(componentName);
            } else if (TextUtils.equals(payMode, PayMode.PAY_MODE_ALIPAY)) {
                intent = new Intent(context, AlipayPayActivity.class);
            } else if (TextUtils.equals(payMode, PayMode.PAY_MODE_YB_HEB)) {
                intent = new Intent();
                ComponentName componentName = new ComponentName(context.getPackageName(),
                        context.getPackageName() + ".pay.YBPayResult");
                intent.setComponent(componentName);
            }
        }
        return intent;
    }

    public Bundle createPayParams(Context context, String payMode, JsonNode payParamsNode, Bundle extBundle) {
        if (!TextUtils.isEmpty(payMode)) {
            if (TextUtils.equals(payMode, PayMode.PAY_MODE_WX)) {
                PayParamsForWX payParams = new PayParamsForWX();
                putPayParams(payParamsNode, payParams);
                return payParams.toPayBundle(extBundle);
            } else if (TextUtils.equals(payMode, PayMode.PAY_MODE_ALIPAY)) {
                PayParamsForAlipay payParams = new PayParamsForAlipay();
                putPayParams(payParamsNode, payParams);
                return payParams.toBundle();
            } else if (TextUtils.equals(payMode, PayMode.PAY_MODE_YB_HEB)) {
                PayParamsForYBHEB payParams = new PayParamsForYBHEB();
                putPayParams(payParamsNode, payParams);
                return payParams.toBundle();
            }
        }
        return null;
    }

    private void putPayParams(JsonNode payParamsNode, PayParams targetPayParams) {
        if (targetPayParams != null) {
            Iterator<String> fieldNames = payParamsNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                targetPayParams.put(fieldName, payParamsNode.get(fieldName).asText());
            }
        }
    }

    public static class DeliveryMode {
        public final int id;
        public final String guid;
        public final String key;
        public final String name;
        public final String desc;

        public DeliveryMode(int id, String guid, String key, String name, String desc) {
            this.id = id;
            this.guid = guid;
            this.key = key;
            this.name = name;
            this.desc = desc;
        }
    }
}
