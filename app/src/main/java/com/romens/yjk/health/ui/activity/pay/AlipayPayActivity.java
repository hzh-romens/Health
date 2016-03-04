package com.romens.yjk.health.ui.activity.pay;

import android.content.Intent;
import android.os.Bundle;

import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.pay.PayParamsForAlipay;
import com.yunuo.pay.alipay.AlipayPay;

/**
 * @author Zhou Lisi
 * @create 16/3/2
 * @description
 */
public class AlipayPayActivity extends PayActivity implements AlipayPay.Delegate {
    private AlipayPay alipayPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alipayPay = new AlipayPay(this);
        onCreateAfter();
    }

    @Override
    protected void onPayRequest(Bundle payParams) {
        Bundle bundle=payParams.getBundle("PAY");
        String payOrderInfo=bundle.getString(PayParamsForAlipay.KEY_PAY_PARAMS);
        alipayPay.sendPayRequest(AlipayPayActivity.this, payOrderInfo);
    }

    @Override
    protected void onPayResponse(Intent intent) {

    }

    @Override
    protected void onCheckPayState() {

    }

    @Override
    protected void needFinish() {
        finish();
    }

    @Override
    public void onPaySuccess(String extData) {

    }

    @Override
    public void onPayFail(String extData) {

    }

    @Override
    public void onPayProcessing(String extData) {

    }
}
