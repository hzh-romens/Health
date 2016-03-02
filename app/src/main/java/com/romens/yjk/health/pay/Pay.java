package com.romens.yjk.health.pay;

import android.util.SparseArray;

/**
 * @author Zhou Lisi
 * @create 16/3/2
 * @description
 */
public class Pay {
    public static final int PAY_TYPE_ONLINE = 0;
    public static final int PAY_TYPE_OFFLINE = 1;
    public static final int PAY_TYPE_YB_ONLINE = 2;

    private static final SparseArray<String> payTypeKeys = new SparseArray<>();
    private static final SparseArray<String> payTypes = new SparseArray<>();

    public static final int DELIVERY_TYPE_STORE = 0;
    public static final int DELIVERY_TYPE_HOME = 1;

    private static final SparseArray<String> deliveryTypeKeys = new SparseArray<>();
    private static final SparseArray<String> deliveryTypes = new SparseArray<>();

    static {
        payTypeKeys.put(PAY_TYPE_ONLINE, "PAY_ONLINE");
        payTypeKeys.put(PAY_TYPE_OFFLINE, "PAY_OFFLINE");
        payTypeKeys.put(PAY_TYPE_YB_ONLINE, "YB_PAY_ONLINE");

        payTypes.put(PAY_TYPE_ONLINE, "在线支付");
        payTypes.put(PAY_TYPE_OFFLINE, "货到付款");
        payTypes.put(PAY_TYPE_YB_ONLINE, "医保在线支付");


        deliveryTypeKeys.put(DELIVERY_TYPE_STORE, "DELIVERY_STORE");
        deliveryTypeKeys.put(DELIVERY_TYPE_HOME, "DELIVERY_HOME");

        deliveryTypes.put(DELIVERY_TYPE_STORE, "到店自提");
        deliveryTypes.put(DELIVERY_TYPE_HOME, "送货上门");
    }

    public static String getPayTypeKey(int id) {
        return payTypeKeys.get(id);
    }

    public static String getPayType(int id) {
        return payTypes.get(id);
    }

    public static int getPayTypeId(String key) {
        int index = payTypeKeys.indexOfValue(key);
        return payTypeKeys.keyAt(index);
    }

    public static String getDeliveryTypeKey(int id) {
        return deliveryTypeKeys.get(id);
    }

    public static String getDeliveryType(int id) {
        return deliveryTypes.get(id);
    }

    public static int getDeliveryTypeId(String key) {
        int index = deliveryTypeKeys.indexOfValue(key);
        return deliveryTypeKeys.keyAt(index);
    }
}
