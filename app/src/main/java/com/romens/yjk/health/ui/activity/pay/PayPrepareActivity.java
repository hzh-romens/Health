package com.romens.yjk.health.ui.activity.pay;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.pay.PayAppManager;
import com.romens.yjk.health.pay.PayMode;
import com.romens.yjk.health.pay.PayModeEnum;
import com.romens.yjk.health.pay.PayParamsForYBHEB;
import com.romens.yjk.health.pay.PayPrepareBaseActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/3/2
 * @description
 */
public class PayPrepareActivity extends PayPrepareBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("选择支付方式");
        actionBar.setBackButtonImage(R.drawable.ic_close_white_24dp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == payActionRow) {
                    trySendPayPrepareRequest();
                } else if (position >= payModeStartRow && position <= payModeEndRow) {
                    selectedPayModeKey = position - payModeStartRow;
                    updateAdapter();
                }
            }
        });
        onInitPayMode(medicarePayModes);
        updateAdapter();
    }

    @Override
    protected void needFinish() {
        finish();
    }

    private void trySendPayPrepareRequest() {
        Map<String, String> args = new HashMap<>();
        args.put("BILLNO", orderNo);
        //args.put("MEDICARECARD", medicareCardNo);

        String payMode = medicarePayModes.get(selectedPayModeKey).getPayModeDesc();
        args.put("PAYMODE", payMode);
        doPayPrepareRequest(args);
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

