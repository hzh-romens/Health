package com.romens.yjk.health.ui.activity.medicare;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.pay.PayPrepareBaseActivity;

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
        actionBar.setTitle("选择社保卡支付方式");
        actionBar.setBackButtonImage(R.drawable.ic_close_white_24dp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == payActionRow) {
                    trySendPayPrepareRequest();
                } else if (position >= payModeStartRow && position <= payModeEndRow) {
                    int index = position - payModeStartRow;
                    selectedPayModeId = defaultPayModes.get(index);
                    updateAdapter();
                }else if (position >= otherPayModeStartRow && position <= otherPayModeEndRow) {
                    int index = position - otherPayModeStartRow;
                    selectedPayModeId = otherPayModes.get(index);
                    updateAdapter();
                }
            }
        });
        //支付方式
        payModes.clear();
        onCreatePayMode(payModes);
        updateAdapter();
    }

    @Override
    protected void needFinish() {
        finish();
    }

    protected abstract void trySendPayPrepareRequest();
}
