package com.romens.yjk.health.ui.activity.medicare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.pay.PayAppManager;
import com.romens.yjk.health.pay.PayMode;
import com.romens.yjk.health.pay.PayPrepareBaseActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public abstract class MedicarePayBaseActivity extends PayPrepareBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("选择医保支付方式");
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
        medicarePayModes.clear();
        onInitPayMode(medicarePayModes);
        updateAdapter();
    }

    @Override
    protected void needFinish() {
        finish();
    }

    protected abstract void trySendPayPrepareRequest();
    /**
     * 初始化支付方式
     * @param payModes 支付方式
     */
    protected abstract void onInitPayMode(SparseArray<PayMode> payModes);
}
