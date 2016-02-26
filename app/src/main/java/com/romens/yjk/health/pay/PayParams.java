package com.romens.yjk.health.pay;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public abstract class PayParams {
    public static final String KEY_BILL_NO = "BILL_NO";
    public static final String KEY_BILL_AMOUNT = "BILL_AMOUNT";

    private final Map<String, String> params = new HashMap<>();

    public void put(String key, String value) {
        params.put(key, value);
    }

    public String get(String key) {
        return params.get(key);
    }
}