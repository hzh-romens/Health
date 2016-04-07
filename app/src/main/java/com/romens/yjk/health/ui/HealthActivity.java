package com.romens.yjk.health.ui;

import android.os.Bundle;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;
import com.romens.yjk.health.ui.fragment.HomeHealthFragment;

/**
 * Created by anlc on 2015/12/15.
 */
public class HealthActivity extends BaseActionBarActivityWithAnalytics {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health, R.id.action_bar);
        actionBarEvent();
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("搜索分类");
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
    }
}
