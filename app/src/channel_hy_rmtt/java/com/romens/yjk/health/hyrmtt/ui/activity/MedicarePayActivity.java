package com.romens.yjk.health.hyrmtt.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.yjk.health.R;
import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.pay.PayMode;
import com.romens.yjk.health.pay.PayModeEnum;
import com.romens.yjk.health.pay.PayParamsForYBHEB;
import com.romens.yjk.health.ui.activity.medicare.MedicarePayBaseActivity;

import java.util.Iterator;

/**
 * @author Zhou Lisi
 * @create 16/3/2
 * @description 医保支付方式选择
 */
public class MedicarePayActivity extends MedicarePayBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onInitPayMode(SparseArray<PayMode> payModes) {
        payModes.put(0, new PayMode.Builder(0)
                .withIconResId(R.drawable.medicare_pay_haerbin)
                .withName("哈尔滨银行(医保支付)")
                .withDesc("支持使用哈尔滨银行账户进行医保支付")
                .withMode(PayModeEnum.YB_HEB).build());

        payModes.put(1, new PayMode.Builder(1)
                .withIconResId(R.drawable.ic_appwx_logo)
                .withName("微信支付(现金支付)")
                .withDesc("推荐微信用户使用")
                .withMode(PayModeEnum.WX).build());

        payModes.put(2, new PayMode.Builder(2)
                .withIconResId(R.drawable.ic_pay_alipay)
                .withName("支付宝支付(现金支付)")
                .withDesc("推荐支付宝用户使用")
                .withMode(PayModeEnum.ALIPAY).build());
        selectedPayModeKey = 0;
    }

    @Override
    protected void formatPayParamsResponse(JsonNode jsonNode) {
        String payMode = jsonNode.get("PayMode").asText();
        JsonNode payParamsNode = jsonNode.get("PayParams");

        PayParamsForYBHEB payParams = new PayParamsForYBHEB();
        Iterator<String> fieldNames = payParamsNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            payParams.put(fieldName, payParamsNode.get(fieldName).asText());
        }
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.romens.yjk.health.hyrmtt",
                "com.romens.yjk.health.hyrmtt.pay.YBPayResult");
        intent.setComponent(componentName);
        Bundle bundle = new Bundle();
        bundle.putBoolean(PayActivity.ARGUMENTS_KEY_FROM_PAY_PREPARE, true);
        Bundle pay = new Bundle();
        pay.putString("ORDER_NO", orderNo);
        pay.putDouble("ORDER_PAY_AMOUNT", orderPayAmount.doubleValue());
        pay.putBundle("PAY", payParams.toBundle());
        bundle.putBundle(PayActivity.ARGUMENTS_KEY_PAY_PARAMS, pay);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

}
