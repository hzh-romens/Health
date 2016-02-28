package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.fragment.ShopCarFragment;

/**
 * Created by HZH on 2016/2/26.
 */
public class ShopCarActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopcar_container);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ShopCarFragment shopCarFragment = new ShopCarFragment();
        shopCarFragment.setTitleView(true);
        transaction.add(R.id.container, shopCarFragment, "d");
        transaction.commit();
    }
}
