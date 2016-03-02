package com.romens.yjk.health.pay;

import android.os.Bundle;

/**
 * @author Zhou Lisi
 * @create 16/3/2
 * @description
 */
public class PayParamsForAlipay extends PayParams {
    @Override
    public Bundle toBundle() {
        //{"partner":"2088701740074813",
        // "seller_id":"2088701740074813",
        // "out_trade_no":"RMTT48131456924508",
        // "subject":"测试",
        // "total_fee":0.01,
        // "notify_url":"http://115.28.244.190/index.php/Alipay",
        // "service":"mobile.securitypay.pay",
        // "payment_type":"1",
        // "_input_charset":"utf-8",
        // "it_b_pay":"30",
        // "sign":"IPJiz3Te4bLSaxDZzJb0DUlM8Xsiq37NOtlQEPT/iErD498AZJJX3zAR8VEZ3rfVKURFG4vcM5Pn/M2mYPolg4KeN6kVCoslBn0jELwd0veAzlxZC7W2zKS6feUvpsP8nmRe3abQdZQtZ5JKGTF0sLlqX+ZnMpt4GnhkgTBFbNQ=",
        // "sign_type":"RSA"}

        Bundle bundle = new Bundle();
        bundle.putString("PAYPARAMS", get("PAYPARAMS"));
        return bundle;
    }
}
