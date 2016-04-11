package com.romens.yjk.health.hyrmtt.pay;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.romens.android.io.json.JacksonMapper;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.pay.PayState;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/24
 * @description
 */
public class YBPayResult extends PayActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateCompleted();
    }

    @Override
    protected String getPayModeText() {
        return "社保卡支付(哈尔滨银行)";
    }

    @Override
    protected void onPayRequest(Bundle payParams) {
        Bundle bundle = payParams.getBundle("PAY");
        ComponentName componentName = new ComponentName("com.yitong.hrb.people.android",
                "com.yitong.hrb.people.android.activity.GifViewActivity");
        Intent intent = new Intent();
        intent.putExtra("bundle", bundle);
        intent.setComponent(componentName);
        startActivity(intent);
    }

    @Override
    protected void onPayResponse(Intent intent) {
        if (intent.hasExtra("bundle")) {
            //Bundle[{cardNo=11204688X, certNo=230102198506053415, payAmount=0.01,
            // totalAmount=0.01, status=1, balance=904.83, custname=赵宇, lastPayAmount=0.00,
            // transferFlowNo=C06714601693452016040910}]
            changePayState(PayState.PROCESSING);
            Bundle bundle = intent.getBundleExtra("bundle");
            String status = bundle.getString("status");
            if (TextUtils.equals("1", status)) {
                Map<String, String> args = new HashMap<>();
                args.put("ORDERCODE", orderNo);
                ObjectNode payResult = JacksonMapper.getInstance().createObjectNode();

                final String medicineCarUserName = bundle.getString("custname");
                final String medicineCardNo = bundle.getString("cardNo");
                final String medicineCertNo = bundle.getString("certNo");
                final String balance = bundle.getString("balance");

                payResult.put("transferFlowNo", bundle.getString("transferFlowNo"));
                payResult.put("lastPayAmount", bundle.getString("lastPayAmount"));
                payResult.put("totalAmount", bundle.getString("totalAmount"));
                payResult.put("balance", balance);
                payResult.put("cardNo", medicineCardNo);
                payResult.put("certNo", medicineCertNo);
                payResult.put("custname", medicineCarUserName);
                args.put("PAYRESULT", payResult.toString());

                payResultInfo.clear();
                payResultInfo.add(new PayResultInfo("姓名", medicineCarUserName));
                payResultInfo.add(new PayResultInfo("社保卡号码", ShoppingHelper.mixedString(medicineCardNo)));
                payResultInfo.add(new PayResultInfo("身份证号码", ShoppingHelper.mixedString(medicineCertNo)));

                CharSequence balanceText;
                try {
                    BigDecimal balanceDecimal = new BigDecimal(balance);
                    balanceText = ShoppingHelper.formatPrice(balanceDecimal, false);
                } catch (Exception e) {
                    balanceText = balance;
                }
                payResultInfo.add(new PayResultInfo("社保卡余额", balanceText));

                postPayResponseToServerAndCheckPayResult(args);
            } else {
                changePayState(PayState.FAIL);
            }
        } else {
            changePayState(PayState.FAIL);
        }
    }

    @Override
    protected void needFinish() {
        finish();
    }
}
