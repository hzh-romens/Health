package com.romens.yjk.health.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.HistoryDao;
import com.romens.yjk.health.db.entity.HistoryEntity;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;
import com.romens.yjk.health.ui.adapter.HistoryAdapter;
import com.romens.yjk.health.ui.utils.DialogUtils;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.List;

/**
 * Created by anlc on 2015/10/13.
 * Modify by hzh on 2015/10/18
 */
public class HistoryActivity extends BaseActionBarActivityWithAnalytics implements View.OnClickListener {
    private ExpandableListView listView;
    private SwipeRefreshLayout refreshLayout;
    private HistoryAdapter adapter;
    private List<HistoryEntity> goodsListEntities;
    private TextView tv_clear, title;
    private ImageView back;

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
                showDialogs(this, "提示", "是否清空历史记录?");
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    public void showDialogs(Context context, String title, String info) {
        DialogUtils dialogUtils = new DialogUtils();
        dialogUtils.show_infor_two(info, (Activity) context, title, new DialogUtils.ConfirmListenerCallBack() {
            @Override
            public void ConfirmListener() {
                HistoryDao historyDao = DBInterface.instance().openReadableDb().getHistoryDao();
                historyDao.deleteAll();
                initData();
            }
        }, new DialogUtils.CancelListenerCallBack() {
            @Override
            public void CancelListener() {

            }
        });
    }
}
