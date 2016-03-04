package com.romens.yjk.health.ui.activity.pay;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.pay.Pay;
import com.romens.yjk.health.pay.PayActivity;
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
                    sendPayPrepareRequest();
                } else if (position >= payModeStartRow && position <= payModeEndRow) {
                    selectedPayModeKey = position - payModeStartRow;
                    updateAdapter();
                }
            }
        });
        medicarePayModes.clear();
        onInitPayMode(medicarePayModes);
        updateAdapter();
    }

    @Override
    protected void needFinish() {
        finish();
    }
}

