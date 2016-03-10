package com.romens.yjk.health.ui.activity.pay;

import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.pay.PayState;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.yunuo.pay.wx.WXPay;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Zhou Lisi
 * @create 16/3/2
 * @description
 */
public class WXPayActivity extends PayActivity implements WXPay.Delegate {
    protected static final int PAY_ERROR_CODE_ARGS_LOSS = 100;
    protected static final int PAY_ERROR_CODE_CONFIG_LOSS = 101;

    private WXPay wxPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWXApi();
        onCreateCompleted();
    }

    @Override
    protected String getPayModeText() {
        return "微信支付";
    }

    private void initWXApi() {
        wxPay = new WXPay(this,this);
    }

    @Override
    protected void onPayRequest(Bundle payParams) {
        Bundle bundle=payParams.getBundle("PAY");
        Bundle extBundle=bundle.getBundle("ext");
        Set<String> extKey=extBundle.keySet();
        Map<String,String> ext=new HashMap<>();
        for (String key :extKey) {
            ext.put(key,extBundle.getString(key));
        }

        WXPay.WXPayBuilder builder = new WXPay.WXPayBuilder(wxPay.getAppId())
                .withPartnerId(bundle.getString("partnerid"))
                .withPrepayId(bundle.getString("prepayid"))
                .withPackageValue("Sign=WXPay")
                .withNonceStr(bundle.getString("noncestr"))
                .withTimeStamp(bundle.getString("time"))
                .withSign(bundle.getString("sign"))
                .withExt(ext);
        boolean isSend = wxPay.sendPayRequest(builder);
        if (!isSend) {
            onPayFail(PAY_ERROR_CODE_ARGS_LOSS, "微信支付参数缺失");
        }
    }

    @Override
    protected void onPayResponse(Intent intent) {
        wxPay.handleIntent(intent);
    }


    @Override
    protected void onPostPayResponseToServerCallback(JsonNode response, String error) {

    }

    @Override
    protected void needFinish() {
        finish();
    }

    @Override
    public void onPaySuccess(String extData) {
        changePayState(PayState.PROCESSING);
        Map<String,String> args=new HashMap<>();
        postPayResponseToServerAndCheckPayResult(args);
    }

    @Override
    public void onPayFail(int errorCode, String error) {
        changePayState(PayState.FAIL);
    }

    @Override
    public void onPayCancel(String extData) {
        changePayState(PayState.FAIL);
    }

    @Override
    public void onPayProcessing() {
        changePayState(PayState.PROCESSING);
    }
}
