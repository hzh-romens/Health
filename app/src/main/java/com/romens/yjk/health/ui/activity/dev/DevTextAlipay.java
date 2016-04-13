package com.romens.yjk.health.ui.activity.dev;

import android.os.Bundle;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.pay.PayParamsForAlipay;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.components.ToastCell;
import com.romens.yjk.health.ui.components.logger.Log;
import com.yunuo.pay.alipay.AlipayPay;

import java.util.Iterator;

/**
 * @author Zhou Lisi
 * @create 16/3/2
 * @description
 */
public class DevTextAlipay extends DarkActionBarActivity {

    private AlipayPay alipayPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alipayPay = new AlipayPay(new AlipayPay.Delegate() {
            @Override
            public void onPaySuccess(Bundle extData) {
                Log.e("ALIPAY", extData.toString());
            }

            @Override
            public void onPayFail(Bundle extData) {
                Log.e("ALIPAY", extData.toString());
            }

            @Override
            public void onPayProcessing(Bundle extData) {
                Log.e("ALIPAY", extData.toString());
            }
        });

        FacadeProtocol protocol = new FacadeProtocol("http://115.28.244.190/index.php/", "test", "GetBillPayRequestParams", null);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(DevTextAlipay.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> protocol = (ResponseProtocol) message.protocol;
                            JsonNode response = protocol.getResponse();
                            PayParamsForAlipay payParams = createPayParams(response);
                            Bundle bundle = payParams.toBundle();
                            String payInfo = bundle.getString(PayParamsForAlipay.KEY_PAY_PARAMS);
                            alipayPay.sendPayRequest(DevTextAlipay.this, payInfo);
                            //{"partner":"2088701740074813",
                            // "seller_id":"2088701740074813",
                            // "out_trade_no":"RMTT48131456924508",
                            // "subject":"测试","total_fee":0.01,
                            // "notify_url":"http://115.28.244.190/index.php/Alipay",
                            // "service":"mobile.securitypay.pay",
                            // "payment_type":"1",
                            // "_input_charset":"utf-8",
                            // "it_b_pay":"30",
                            // "sign":"IPJiz3Te4bLSaxDZzJb0DUlM8Xsiq37NOtlQEPT/iErD498AZJJX3zAR8VEZ3rfVKURFG4vcM5Pn/M2mYPolg4KeN6kVCoslBn0jELwd0veAzlxZC7W2zKS6feUvpsP8nmRe3abQdZQtZ5JKGTF0sLlqX+ZnMpt4GnhkgTBFbNQ=",
                            // "sign_type":"RSA"}
                        } else {
                            ToastCell.toast(DevTextAlipay.this, "提交订单失败!");
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(DevTextAlipay.this, connect);
    }

    private PayParamsForAlipay createPayParams(JsonNode response) {
        Iterator<String> iterator = response.fieldNames();
        PayParamsForAlipay payParams = new PayParamsForAlipay();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = response.get(key).asText();
            payParams.put(key, value);
        }
        return payParams;
    }

    @Override
    protected String getActivityName() {
        return "支付宝测试入口";
    }
}
