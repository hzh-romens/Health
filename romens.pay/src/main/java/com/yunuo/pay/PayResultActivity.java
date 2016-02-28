package com.yunuo.pay;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.yunuo.pay.adapter.PayResultAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HZH on 2016/2/19.
 */
public class PayResultActivity extends AppCompatActivity {
    private ListView listView;
    private Toolbar toolbar;
    private PayResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        initView();
        setSupportActionBar(toolbar);
        UpdateSucceessData();
        setData();
    }

    private void setData() {
        adapter = new PayResultAdapter(this);
        adapter.bindData(succeessData, types);
        listView.setAdapter(adapter);
    }

    private List<String> succeessData;
    private List<String> errorData;

    private SparseArray types;

    private void UpdateSucceessData() {
        types = new SparseArray();
        for (int i = 0; i < 7; i++) {
            types.append(i, i + 1);
        }
        succeessData = new ArrayList<>();
        succeessData.add("succeess");
        succeessData.add("name");
        succeessData.add("way");
        succeessData.add("orderCode");
        succeessData.add("time");
        succeessData.add("price");
    }

    private void UpdateErrorData() {
        errorData = new ArrayList<>();
        errorData.add("fail");
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("交易详情");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        listView = (ListView) findViewById(R.id.listview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == 16908332) {
            finish();
        } else if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
