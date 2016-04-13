package com.romens.yjk.health.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.base.BaseActionBarActivity;
import com.romens.yjk.health.R;
import com.romens.yjk.health.common.GoodsFlag;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.fragment.HomeHealthNewFragment;

/**
 * Created by siery on 15/12/22.
 */
public class MedicineGroupActivity extends DarkActionBarActivity {
    private HomeHealthNewFragment fragment;
    private int goodsFlag = GoodsFlag.NORMAL;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG)) {
            goodsFlag = intent.getIntExtra(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG, GoodsFlag.NORMAL);
        }

        setContentView(R.layout.activity_fragment, R.id.action_bar);
        ActionBar actionBar = getMyActionBar();
        final ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(0, R.drawable.ic_menu_search);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 0) {
                    UIOpenHelper.openSearchActivity(MedicineGroupActivity.this, goodsFlag);
                }
            }
        });

        if (intent.hasExtra("title")) {
            title=intent.getStringExtra("title");
        } else {
            title=getString(R.string.app_name);
        }
        actionBar.setTitle(title);
        fragment = new HomeHealthNewFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG, goodsFlag);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
    }

    @Override
    protected String getActivityName() {
        return String.format("药品分类[%s][%s]",title,goodsFlag==GoodsFlag.MEDICARE?"医保药品":"药品");
    }
}
