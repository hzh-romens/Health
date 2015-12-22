package com.romens.yjk.health.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.base.BaseActionBarActivity;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.fragment.HomeHealthFragment;

/**
 * Created by siery on 15/12/22.
 */
public class MedicineGroupActivity extends BaseActionBarActivity {
    private HomeHealthFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.app_name);
        }
        setContentView(R.layout.activity_fragment, R.id.action_bar);
        ActionBar actionBar = getMyActionBar();
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });
        actionBar.setTitle(title);
        fragment = new HomeHealthFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
    }
}
