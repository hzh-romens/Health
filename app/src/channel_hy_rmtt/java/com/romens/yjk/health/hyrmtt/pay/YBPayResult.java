package com.romens.yjk.health.hyrmtt.pay;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;

import java.util.Set;

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

        if (!isFromPayPrepare) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            Set<String> keys = bundle.keySet();
            bundle = intent.getBundleExtra("bundle");
            keys = bundle.keySet();
            String state = intent.getStringExtra("status");
            StringBuilder log = new StringBuilder();
            log.append("state:" + state);
            log.append("\n");
            emptyTextView.setText(log);
            //Bundle[{cardNo=11204688X,
            // certNo=230102198506053415,
            // payAmount=0.01,
            // totalAmount=0.01,
            // status=1,
            // balance=785.69,
            // custname=赵宇,
            // lastPayAmount=0.00,
            // transferFlowNo=C06714567145472016022910}]
        } else {
            Bundle bundle = payParams.getBundle("PAY");
            sendPayRequest(bundle);
        }
    }

    @Override
    protected void tryCheckPayState() {

    }

    @Override
    protected boolean sendPayRequest(Bundle bundle) {
        ComponentName componentName = new ComponentName("com.yitong.hrb.people.android",
                "com.yitong.hrb.people.android.activity.GifViewActivity");
        Intent intent = new Intent();
        intent.putExtra("bundle", bundle);
        intent.setComponent(componentName);
        startActivity(intent);
        return true;
    }
}
