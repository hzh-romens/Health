package com.romens.yjk.health.ui.activity.pay;

import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.romens.android.io.json.JacksonMapper;
import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.pay.PayState;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.yunuo.pay.wx.WXPay;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Zhou Lisi
 * @create 16/3/2
 * @description
 */
public abstract class WXPayBaseActivity extends PayActivity implements WXPay.Delegate {
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
        String appId = getAppId();
        wxPay = new WXPay(this, appId, this);
    }

    protected abstract String getAppId();

    @Override
    protected void onPayRequest(Bundle payParams) {
        Bundle bundle = payParams.getBundle("PAY");
        Bundle extBundle = bundle.getBundle("ext");
        Set<String> extKey = extBundle.keySet();
        Map<String, String> ext = new HashMap<>();
        for (String key : extKey) {
            ext.put(key, extBundle.getString(key));
        }
        String appId = getAppId();
        WXPay.WXPayBuilder builder = new WXPay.WXPayBuilder(appId)
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
        } else {
            onPayProcessing();
        }
    }

    @Override
    protected void onPayResponse(Intent intent) {
        wxPay.handleIntent(intent);
    }


    @Override
    protected void needFinish() {
        finish();
    }

    @Override
    public void onPaySuccess(Bundle extData) {
        changePayState(PayState.PROCESSING);
        PayResp payResp = new PayResp(extData);
        Map<String, String> args = new HashMap<>();
        args.put("ORDERCODE", orderNo);
        ObjectNode payResult = JacksonMapper.getInstance().createObjectNode();
        payResult.put("prepayId", payResp.prepayId);
        args.put("PAYRESULT", payResult.toString());
        postPayResponseToServerAndCheckPayResult(args);
    }

    @Override
    public void onPayFail(int errorCode, String error) {
        changePayState(PayState.FAIL);
    }

    @Override
    public void onPayCancel(Bundle extData) {
        changePayState(PayState.FAIL);
    }

    @Override
    public void onPayProcessing() {
        changePayState(PayState.PROCESSING);
    }
}
