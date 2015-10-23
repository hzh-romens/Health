package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.HelpQuestionConfig;
import com.romens.yjk.health.ui.cells.KeyAndImgCell;
import com.romens.yjk.health.ui.cells.TitleAndBodyCell;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/10/16.
 * 问题回答页面
 */
public class HelpAnswerActivity extends BaseActivity {

    private ListView listView;
    private SwipeRefreshLayout refreshLayout;
    private HelpAdapter adapter;

    private List<Map<String, String>> data;

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
        refreshLayout.setBackgroundResource(R.drawable.bg_light_gray);
        container.addView(refreshLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        refreshLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        adapter = new HelpAdapter(this, data);
        listView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        int type = intent.getIntExtra("question", 0);
        data = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("question", HelpQuestionConfig.getData("question" + type));
        data.add(map);
        int count = 6;
        if (type == 3) {
            count = 4;
        }
        for (int i = 1; i < count; i++) {
            map = new HashMap<>();
            map.put("question", HelpQuestionConfig.getData("question" + type + "_sub" + i));
            map.put("answer", HelpQuestionConfig.getData("question" + type + "_answer" + i));
            data.add(map);
        }
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
        private List<Map<String, String>> data;

        public HelpAdapter(Context context, List<Map<String, String>> data) {
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
            if (position == 0) {
                KeyAndImgCell cell = new KeyAndImgCell(context);
                cell.setCellBackgroudColor(Color.WHITE);
                cell.setInfo(data.get(position).get("question"), true, false);
                return cell;
            } else if (position < data.size()) {
                TitleAndBodyCell cell = new TitleAndBodyCell(context);
                cell.setTitleAndBodyInfo(data.get(position).get("question"), data.get(position).get("answer"), true);
                return cell;
            } else if (position == data.size()) {
                return new ShadowSectionCell(context);
            } else if (position > data.size()) {
                if (convertView == null) {
                    convertView = new KeyAndImgCell(context);
                }
                KeyAndImgCell cell = (KeyAndImgCell) convertView;
                cell.setCellBackgroudColor(Color.WHITE);
                if (position == data.size() + 1) {
                    cell.setInfo("没有您的问题？", true, true);
                    cell.setDivider(true, AndroidUtilities.dp(16), AndroidUtilities.dp(16));
                } else if (position == data.size() + 2) {
                    cell.setInfo("联系客服电话", false, true);
                }
            }
            return convertView;
        }
    }
}
