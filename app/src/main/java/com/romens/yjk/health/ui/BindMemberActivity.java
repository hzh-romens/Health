package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/1/28.
 */
public class BindMemberActivity extends BaseActivity {
    private FrameLayout pharmacy, container;
    private CardView btnOk;
    private TextView pharmacyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindmember, R.id.action_bar);
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("绑定会员卡");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        initView();
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
    }

    private void initView() {
        pharmacy = (FrameLayout) findViewById(R.id.pharmacy);
        container = (FrameLayout) findViewById(R.id.container);
        btnOk = (CardView) findViewById(R.id.btnSure);
        pharmacyName = (TextView) findViewById(R.id.pharmacy_name);
    }
}
