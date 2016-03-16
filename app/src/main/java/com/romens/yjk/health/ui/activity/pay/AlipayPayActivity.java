package com.romens.yjk.health.ui.activity.pay;

import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.romens.android.io.json.JacksonMapper;
import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.pay.PayParamsForAlipay;
import com.romens.yjk.health.pay.PayState;
import com.yunuo.pay.alipay.AlipayPay;

import java.util.HashMap;
import java.util.Map;

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
        onCreateCompleted();
    }

    @Override
    protected String getPayModeText() {
        return "支付宝支付";
    }

    @Override
    protected void onPayRequest(Bundle payParams) {
        Bundle bundle = payParams.getBundle("PAY");
        String payOrderInfo = bundle.getString(PayParamsForAlipay.KEY_PAY_PARAMS);
        alipayPay.sendPayRequest(AlipayPayActivity.this, payOrderInfo);
    }

    @Override
    protected void onPayResponse(Intent intent) {
        //支付宝不重写这个方法，支付宝同步返回机制
    }


    @Override
    protected void needFinish() {
        finish();
    }

    @Override
    public void onPaySuccess(Bundle extData) {
        changePayState(PayState.PROCESSING);
        Map<String, String> args = new HashMap<>();
        args.put("ORDERCODE", orderNo);
        ObjectNode payResult = JacksonMapper.getInstance().createObjectNode();
        for (String key : extData.keySet()) {
            payResult.put(key, extData.getString(key));
        }
        args.put("PAYRESULT", payResult.toString());
        postPayResponseToServerAndCheckPayResult(args);
    }

    @Override
    public void onPayFail(Bundle extData) {
        changePayState(PayState.FAIL);
    }

    @Override
    public void onPayProcessing(Bundle extData) {
        changePayState(PayState.PROCESSING);
    }
}
