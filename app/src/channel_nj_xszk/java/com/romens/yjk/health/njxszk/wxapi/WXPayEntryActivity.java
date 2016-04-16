package com.romens.yjk.health.njxszk.wxapi;

import android.os.Bundle;

import com.romens.yjk.health.ui.activity.pay.WXPayBaseActivity;

/**
 * Created by siery on 16/1/25.
 */
public class WXPayEntryActivity extends WXPayBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getAppId() {
        return "wx0c8c0d4ae1fc56d8";
    }

    @Override
    protected String getActivityName() {
        return "微信支付";
    }
}
