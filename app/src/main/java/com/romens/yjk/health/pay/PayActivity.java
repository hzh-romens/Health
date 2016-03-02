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
    }

    protected void onCreateAfter() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null&&extras.containsKey(ARGUMENTS_KEY_FROM_PAY_PREPARE)){
            isFromPayPrepare = extras.getBoolean(ARGUMENTS_KEY_FROM_PAY_PREPARE, false);
        }else{
            isFromPayPrepare=false;
        }
        if (isFromPayPrepare) {
            payParams = extras.getBundle(ARGUMENTS_KEY_PAY_PARAMS);
            onPayRequest(payParams);
        } else {
            onPayResponse(intent);
        }
    }

    protected abstract void onPayRequest(Bundle payParams);

    protected abstract void onPayResponse(Intent intent);

    protected abstract void onCheckPayState();

    @Override
    public void onResume() {
        super.onResume();
        if (isFromPayPrepare) {
            onCheckPayState();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        onPayResponse(intent);
    }
}
