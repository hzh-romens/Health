package com.romens.yjk.health.ui.activity;

import android.content.ComponentName;
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
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.components.logger.Log;

/**
 * @author Zhou Lisi
 * @create 16/2/23
 * @description 开发者模式
 */
public class DevelopModeActivity extends BaseActionBarActivityWithAnalytics {
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);
        ListView listView = new ListView(this);
        container.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setSelector(R.drawable.list_selector);
        adapter = new ListAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == testYBZFRow) {
                    onTestYBZFRow();
                }
            }
        });
        updateAdapter();
    }

    private void onTestYBZFRow() {
        ComponentName componentName = new ComponentName("com.yitong.hrb.people.android",
                "com.yitong.hrb.people.android.activity.GifViewActivity");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("flowno", "");
        bundle.putString("orderno", "");
        bundle.putString("EndDate", "");
        bundle.putString("MerType", "");
        bundle.putString("acctFlag", "");
        bundle.putString("payAmount", "");
        bundle.putString("agtname", "");
        bundle.putString("cardNo", "");
        bundle.putString("O2TRSN", "");
        bundle.putString("transferFlowNo", "");
        bundle.putString("BusiPeriod", "");
        intent.putExtra("bundle", bundle);
        intent.setComponent(componentName);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DevelopMode", "requestCode=" + requestCode + ";resultCode=" + resultCode);
    }

    private void updateAdapter() {
        rowCount = 0;
        testYBZFRow = rowCount++;
        adapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int testYBZFRow;

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextSettingsCell(DevelopModeActivity.this);
            }
            TextSettingsCell cell = (TextSettingsCell) convertView;
            if (position == testYBZFRow) {
                cell.setText("医保支付测试", true);
            }
            return convertView;
        }
    }
}
