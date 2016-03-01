package com.romens.yjk.health.pay;

import android.content.Intent;
import android.os.Bundle;

import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;

/**
 * @author Zhou Lisi
 * @create 16/3/1
 * @description
 */
public abstract class PayActivity extends BaseActionBarActivityWithAnalytics {
    public static final String ARGUMENTS_KEY_FROM_PAY_PREPARE = "yjk_key_from_pay_prepare";
    public static final String ARGUMENTS_KEY_PAY_PARAMS = "yjk_key_pay_params";

    protected boolean isFromPayPrepare = false;
    protected Bundle payParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        isFromPayPrepare = extras.getBoolean(ARGUMENTS_KEY_FROM_PAY_PREPARE, false);
        if (isFromPayPrepare) {
            payParams = extras.getBundle(ARGUMENTS_KEY_PAY_PARAMS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFromPayPrepare) {
            tryCheckPayState();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    protected abstract void tryCheckPayState();

    protected abstract boolean sendPayRequest(Bundle bundle);
}
