package com.romens.yjk.health.hyrmtt.pay;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.romens.android.AndroidUtilities;
import com.romens.android.io.json.JacksonMapper;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.pay.PayState;

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
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });

        actionBar.setTitle("订单支付");
        actionBar.setBackButtonImage(R.drawable.ic_close_white_24dp);

        TextView emptyTextView = new TextView(this);
        emptyTextView.setTextColor(0xff808080);
        emptyTextView.setTextSize(20);
        emptyTextView.setLineSpacing(AndroidUtilities.dp(4), 1.0f);
        emptyTextView.setGravity(Gravity.CENTER);
        content.addView(emptyTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 16, 16, 16, 16));
        onCreateCompleted();
    }

    @Override
    protected String getPayModeText() {
        return "医保支付(哈尔滨银行)";
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
            changePayState(PayState.PROCESSING);
            Bundle bundle = intent.getBundleExtra("bundle");
            String status = bundle.getString("status");
            if (TextUtils.equals("1", status)) {
                Map<String, String> args = new HashMap<>();
                args.put("ORDERNO", orderNo);
                ObjectNode payResult = JacksonMapper.getInstance().createObjectNode();
                payResult.put("transferFlowNo", bundle.getString("transferFlowNo"));
                payResult.put("lastPayAmount", bundle.getString("lastPayAmount"));
                payResult.put("totalAmount", bundle.getString("totalAmount"));
                payResult.put("balance", bundle.getString("balance"));
                payResult.put("cardNo", bundle.getString("cardNo"));
                payResult.put("certNo", bundle.getString("certNo"));
                payResult.put("custname", bundle.getString("custname"));
                args.put("PAYRESULT", payResult.toString());
                postPayResponseToServerAndCheckPayResult(args);
            } else {
                changePayState(PayState.FAIL);
            }
        } else {
            changePayState(PayState.FAIL);
        }
    }

    @Override
    protected void onPostPayResponseToServerCallback(JsonNode response, String error) {

    }

    @Override
    protected void needFinish() {
        finish();
    }
}
