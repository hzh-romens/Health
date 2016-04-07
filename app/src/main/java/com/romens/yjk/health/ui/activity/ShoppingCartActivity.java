package com.romens.yjk.health.ui.activity;

import android.os.Bundle;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.fragment.ShoppingCartFragment;

/**
 * @author Zhou Lisi
 * @create 16/2/27
 * @description
 */
public class ShoppingCartActivity extends BaseActionBarActivityWithAnalytics {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment, R.id.action_bar);
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("购物车");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });
        Bundle arguments = getIntent().getExtras();
        ShoppingCartFragment fragment = new ShoppingCartFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit();
    }
}
