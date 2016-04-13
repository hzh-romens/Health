package com.romens.yjk.health.ui;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.MemberType;
import com.romens.yjk.health.ui.base.LightActionBarActivity;
import com.romens.yjk.health.ui.adapter.NewMemberAdapter;

/**
 * Created by HZH on 2016/3/18.
 */
public class AddMemberActivity extends LightActionBarActivity {
    private RecyclerView listView;
    private NewMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        actionBar.setTitle("绑定会员卡");
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        listView = new RecyclerView(this);
        content.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        setContentView(content, actionBar);
        actionBar.setBackgroundColor(getResources().getColor(R.color.theme_primary));
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_white_24dp);
        listView.setLayoutManager(new LinearLayoutManager(this));
        setType();
        adapter = new NewMemberAdapter(this);
        adapter.bindType(types);
        listView.setAdapter(adapter);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
    }

    private SparseIntArray types = new SparseIntArray();

    private void setType() {
        types.append(0, MemberType.TIP);
        types.append(1, MemberType.EMPTY);
        types.append(2, MemberType.PHONE);
        types.append(3, MemberType.PSW);
        types.append(4, MemberType.ADVICE);
        types.append(5, MemberType.BUTTON);
    }

    @Override
    protected String getActivityName() {
        return "绑定会员卡";
    }
}
