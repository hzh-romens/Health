package com.romens.yjk.health.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.HistoryDao;
import com.romens.yjk.health.db.entity.HistoryEntity;
import com.romens.yjk.health.ui.adapter.HistoryAdapter;
import com.romens.yjk.health.ui.components.CustomDialog;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.List;

/**
 * Created by anlc on 2015/10/13.
 * Modify by hzh on 2015/10/18
 */
public class HistoryActivity extends BaseActivity implements View.OnClickListener {
    private ExpandableListView listView;
    private SwipeRefreshLayout refreshLayout;
    private HistoryAdapter adapter;
    private List<HistoryEntity> goodsListEntities;
    private TextView tv_clear, title;
    private ImageView back;
    private CustomDialog.Builder ibuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_header);
        tv_clear = (TextView) findViewById(R.id.clear);
        title = (TextView) findViewById(R.id.title);
        back = (ImageView) findViewById(R.id.back);
        title.setText("历史浏览");
        tv_clear.setOnClickListener(this);
        back.setOnClickListener(this);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        listView = (ExpandableListView) findViewById(R.id.listView);
        listView.setGroupIndicator(null);
        adapter = new HistoryAdapter(this);
        listView.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
        initData();
    }

    private List<HistoryEntity> historyEntities;

    private void initData() {
        historyEntities = DBInterface.instance().loadAllHistory();
        refreshListView();
    }

    private void refreshListView() {
        adapter.setOrderEntities(historyEntities);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                int count = listView.getCount();
                for (int i = 0; i < count; i++) {
                    listView.expandGroup(i);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear:
                ibuilder = new CustomDialog.Builder(HistoryActivity.this);
                ibuilder.setTitle(R.string.prompt);
                ibuilder.setMessage("您确定要清空历史浏览记录？");
                ibuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HistoryDao historyDao = DBInterface.instance().openReadableDb().getHistoryDao();
                        historyDao.deleteAll();
                        initData();
                    }
                });
                ibuilder.setNegativeButton("取消", null);
                ibuilder.create().show();
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
