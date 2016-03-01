package com.romens.yjk.health.hyrmtt.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.romens.yjk.health.ui.activity.CommitOrderBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhou Lisi
 * @create 16/2/29
 * @description
 */
public class CommitOrderActivity extends CommitOrderBaseActivity {
    private boolean supportMedicareCardPay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        supportMedicareCardPay = intent.getBooleanExtra("SupportMedicareCardPay", false);
    }

    @Override
    protected boolean supportMedicareCardPay() {
        return supportMedicareCardPay;
    }
}
