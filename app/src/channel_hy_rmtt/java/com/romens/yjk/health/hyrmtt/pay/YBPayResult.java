package com.romens.yjk.health.hyrmtt.pay;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;

/**
 * @author Zhou Lisi
 * @create 16/2/24
 * @description
 */
public class YBPayResult extends BaseActionBarActivityWithAnalytics {

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

        actionBar.setTitle("医保支付结果");
        actionBar.setBackButtonImage(R.drawable.ic_close_white_24dp);

        TextView emptyTextView = new TextView(this);
        emptyTextView.setTextColor(0xff808080);
        emptyTextView.setTextSize(20);
        emptyTextView.setLineSpacing(AndroidUtilities.dp(4), 1.0f);
        emptyTextView.setGravity(Gravity.CENTER);
        content.addView(emptyTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 16, 16, 16, 16));

        Intent intent = getIntent();
        String state = intent.getStringExtra("status");
        StringBuilder log = new StringBuilder();
        log.append("state:" + state);
        log.append("\n");
        emptyTextView.setText(log);
    }
}
