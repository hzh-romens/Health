package com.romens.yjk.health.njxszk.wxapi;

import android.widget.Toast;

import com.yunuo.pay.WXPayActivity;

/**
 * Created by siery on 16/1/25.
 */
public class WXPayEntryActivity extends WXPayActivity {
    @Override
    protected void onWXPayProgress(boolean progress) {

    }

    @Override
    protected void onWXPayFail(int code, String error) {
        Toast.makeText(WXPayEntryActivity.this, error, Toast.LENGTH_LONG).show();
    }
}
