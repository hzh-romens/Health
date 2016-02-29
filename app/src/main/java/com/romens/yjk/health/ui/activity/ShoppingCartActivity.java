package com.romens.yjk.health.ui.activity;

import android.os.Bundle;

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

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, new ShoppingCartFragment())
                .commit();
    }
}
