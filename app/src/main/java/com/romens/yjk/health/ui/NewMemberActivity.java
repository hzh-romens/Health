package com.romens.yjk.health.ui;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.ui.activity.LightActionBarActivity;
import com.romens.yjk.health.ui.adapter.NewMemberAdapter;

/**
 * Created by HZH on 2016/3/18.
 */
public class NewMemberActivity extends LightActionBarActivity {
    private RecyclerView listView;
    private NewMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        listView = new RecyclerView(this);
        content.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        setContentView(content, actionBar);
        listView.setLayoutManager(new LinearLayoutManager(this));
        setType();
    }
    private SparseIntArray types=new SparseIntArray();

    private void setType() {
        for (int i = 0; i <6; i++) {

        }
    }
}
