package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.HelpQuestionConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/16.
 */
public class HelpActivity extends BaseActivity {

    private ListView listView;
    private HelpAdapter adapter;

    private List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);
        actionBar.setTitle("帮助");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                super.onItemClick(i);
                if (i == -1) {
                    finish();
                }
            }
        });
        initData();

        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        container.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        adapter = new HelpAdapter(this, data);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HelpActivity.this, HelpAnswerActivity.class);
                intent.putExtra("question", position + 1);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        data = new ArrayList<>();
        String key = "";
        for (int i = 1; i < 4; i++) {
            key = "question" + i;
            data.add(HelpQuestionConfig.getData(key));
        }
    }

    class HelpAdapter extends BaseAdapter {

        private Context context;
        private List<String> data;

        public HelpAdapter(Context context, List<String> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public boolean isEnabled(int position) {
            return position < data.size();
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
            if (position == data.size()) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(context);
                }
            } else {
                if (convertView == null) {
                    convertView = new TextSettingsCell(context);
                }
                TextSettingsCell cell = (TextSettingsCell) convertView;
                if (position < data.size()) {
                    cell.setTextAndIcon(data.get(position), R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == data.size() + 1) {
                    cell.setTextAndIcon("没有您的问题？", R.drawable.ic_chevron_right_grey600_24dp, true);
                } else if (position == data.size() + 2) {
                    cell.setTextAndIcon("联系客服电话", R.drawable.ic_chevron_right_grey600_24dp, true);
                }
            }

            return convertView;
        }
    }
}
