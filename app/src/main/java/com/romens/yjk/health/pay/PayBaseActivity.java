package com.romens.yjk.health.pay;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public abstract class PayBaseActivity extends BaseActionBarActivityWithAnalytics {

    public interface PayMode {
        String YB_HEB = "YB_HEB";
    }

    protected boolean sendPayRequest(String payMode, PayParams payParams) {
        if (TextUtils.equals(PayMode.YB_HEB, payMode)) {
            return sendPayRequestForYBHEB(payParams);
        }
        return false;
    }

    private boolean sendPayRequestForYBHEB(PayParams payParams) {
        ComponentName componentName = new ComponentName("com.yitong.hrb.people.android",
                "com.yitong.hrb.people.android.activity.GifViewActivity");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("flowno", payParams.get(PayParamsForYBHEB.KEY_FLOW_NO));
        bundle.putString("orderno", payParams.get(PayParamsForYBHEB.KEY_BILL_NO));
        bundle.putString("EndDate", payParams.get(PayParamsForYBHEB.KEY_END_DATE));
        bundle.putString("MerType", "0");
        bundle.putString("acctFlag", "1");
        bundle.putString("payAmount", payParams.get(PayParamsForYBHEB.KEY_BILL_AMOUNT));
        bundle.putString("agtname", payParams.get(PayParamsForYBHEB.KEY_OPERATOR));
        bundle.putString("cardNo", payParams.get(PayParamsForYBHEB.KEY_MEDICARE_NO));
        bundle.putString("O2TRSN", payParams.get(PayParamsForYBHEB.KEY_O2TRSN));
        bundle.putString("transferFlowNo", payParams.get(PayParamsForYBHEB.KEY_TRANSFER_FLOW_NO));
        bundle.putString("BusiPeriod", "20150000");
        bundle.putString("hrbbType", "1");
        intent.putExtra("bundle", bundle);
        intent.setComponent(componentName);
        startActivity(intent);
        return true;
    }
}
