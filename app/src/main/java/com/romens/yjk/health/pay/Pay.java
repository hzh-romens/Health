package com.romens.yjk.health.pay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.OrderDetailEntity;
import com.romens.yjk.health.ui.activity.pay.AlipayPayActivity;
import com.romens.yjk.health.ui.activity.pay.PayPrepareActivity;

import java.math.BigDecimal;
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
                JsonNode item = data.get(i);
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

    public String getPayType(String key) {
        int id = getPayTypeId(key);
        if (id >= 0) {
            return getPayType(id);
        }
        return "";
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
            } else if (id == PAY_TYPE_ONLINE) {
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

    /**
     * 20160418 zhoulisi 新增医保支付限制其他支付
     */
    public static class PayRequestBuilder {
        private final String orderNo;
        private final String orderDate;
        private final BigDecimal orderAmount;
        private final BigDecimal payAmount;

        private final String payType;
        private final boolean supportOtherPay;

        public PayRequestBuilder(JsonNode response) {
            orderNo = response.get("ORDERCODE").asText();
            orderDate = response.get("CREATEDATE").asText();
            payType = response.get("PAYTYPE").asText();
            orderAmount = new BigDecimal(response.get("PAYMOUNT").asDouble());
            payAmount = new BigDecimal(response.get("PAYPRICE").asDouble());

            if (response.has("SUPPORT_OTHER_PAY")) {
                supportOtherPay = TextUtils.equals("1", response.get("SUPPORT_OTHER_PAY").asText());
            } else {
                supportOtherPay = false;
            }
        }

        public PayRequestBuilder(OrderDetailEntity entity) {
            orderNo = entity.orderNo;
            orderDate = entity.createTime;
            orderAmount = entity.orderPrice;
            payAmount = entity.payPrice;
            payType = entity.payType;
            supportOtherPay = entity.supportOtherPay;
        }

        public boolean build(Context context) {
            boolean isOpen = true;
            int id = Pay.getInstance().getPayTypeId(payType);
            if (id == Pay.PAY_TYPE_OFFLINE) {
                UIOpenHelper.openOrderDetailForOrderNoActivity(context, orderNo);
            } else {
                Bundle arguments = new Bundle();
                arguments.putString(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_NO, orderNo);
                arguments.putString(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_DATE, orderDate);
                arguments.putDouble(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_AMOUNT, orderAmount.doubleValue());
                arguments.putDouble(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_PAY_AMOUNT, payAmount.doubleValue());
                if (id == Pay.PAY_TYPE_YB_ONLINE) {
                    //20160418 zhoulisi 新增限制医保是否支持其他支付 默认不支持
                    Bundle payExtras = new Bundle();
                    payExtras.putBoolean("SUPPORT_OTHER_PAY", supportOtherPay);
                    arguments.putBundle(PayPrepareBaseActivity.ARGUMENTS_KEY_PAY_EXTRAS, payExtras);
                }
                isOpen = UIOpenHelper.openPayPrepareActivity(context, payType, arguments);
            }
            return isOpen;
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
