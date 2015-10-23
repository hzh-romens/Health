package com.romens.yjk.health.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.BaseActivity;
import com.romens.yjk.health.ui.cells.KeyAndImgCell;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/16.
 */
public class HelpActivity extends BaseActivity {

    private ListView listView;
    private SwipeRefreshLayout refreshLayout;
    private HelpAdapter adapter;

    private List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);
        actionBarEvent(actionBar);
        initData();

        refreshLayout = new SwipeRefreshLayout(this);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        container.addView(refreshLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(this);
        refreshLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        adapter = new HelpAdapter(this, data);
        listView.setAdapter(adapter);
    }

    private void initData() {
        data = new ArrayList<>();
        data.add(getResources().getString(R.string.question1));
        data.add(getResources().getString(R.string.question2));
        data.add(getResources().getString(R.string.question3));
    }

    private void actionBarEvent(ActionBar actionBar) {
        actionBar.setTitle("帮助");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                super.onItemClick(i);
                if (i == -1) {
                    finish();
                }
            }
        });
    }

    class HelpAdapter extends BaseAdapter {

        private Context context;
        private List<String> data;

        public HelpAdapter(Context context, List<String> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size() + 3;
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == data.size() + 1) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(context);
                }
            } else {
                if (convertView == null) {
                    convertView = new KeyAndImgCell(context);
                }
                KeyAndImgCell cell = (KeyAndImgCell) convertView;
                if (position <= data.size()) {
                    cell.setInfo(data.get(position), false, false);
                } else if (position == data.size() + 2) {
                    cell.setInfo("没有您的问题？", true, true);
                    cell.setDivider(true, AndroidUtilities.dp(16), AndroidUtilities.dp(16));
                } else if (position == data.size() + 3) {
                    cell.setInfo("联系客服电话", false, true);
                }

            }

            return convertView;
        }
    }
}
