package com.romens.yjk.health.ui;

import android.os.Bundle;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.fragment.OrderDetailFragment;

/**
 * Created by anlc on 2015/9/24.
 * 订单详情
 */
public class OrderDetailActivity extends DarkActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment, R.id.action_bar);
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("订单详情");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });

        OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
        Bundle extras = getIntent().getExtras();
        orderDetailFragment.setArguments(extras);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, orderDetailFragment).commit();

    }

    @Override
    protected String getActivityName() {
        return "订单详情";
    }
}