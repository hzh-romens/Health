package com.romens.yjk.health.ui;

import android.os.Bundle;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.fragment.ShoppingCartFragment;

/**
 * Created by HZH on 2016/2/26.
 */
public class ShopCarActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
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

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment, new ShoppingCartFragment())
                .commit();
    }
}
