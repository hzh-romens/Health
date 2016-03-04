package com.romens.yjk.health.pay;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public abstract class PayParams {

    protected final Map<String, String> params = new HashMap<>();

    public void put(String key, String value) {
        params.put(key, value);
    }

    public String get(String key) {
        return params.get(key);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry :
                params.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }
}
