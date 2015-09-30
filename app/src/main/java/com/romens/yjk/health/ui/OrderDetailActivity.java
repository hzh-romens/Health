package com.romens.yjk.health.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.HeaderCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.adapter.OrderExpandableAdapter;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/9/24.
 * 订单详情
 */
public class OrderDetailActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private OrderDetailAdapter adapter;
    private List<AllOrderEntity> mOrderEntities;
    private OrderExpandableAdapter subExpandableadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = actionBarEvent();
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        swipeRefreshLayout = new SwipeRefreshLayout(this);
        UIHelper.setupSwipeRefreshLayoutProgress(swipeRefreshLayout);
        container.addView(swipeRefreshLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new refreshTask().execute();
            }
        });

        FrameLayout listContainer = new FrameLayout(this);
        swipeRefreshLayout.addView(listContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        setContentView(container);

        initData();
        setRow();

        adapter = new OrderDetailAdapter(this);
        listView.setAdapter(adapter);
    }

    class refreshTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private ActionBar actionBarEvent() {
        ActionBar actionBar = new ActionBar(this);
        actionBar.setTitle("订单详情");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
        return actionBar;
    }

    public void initData() {
        mOrderEntities = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AllOrderEntity entit = new AllOrderEntity();
            entit.setDrugStroe("AA药店");
            entit.setOrderStatuster("待评价" + i);
            entit.setOrderStatus("1");
            entit.setGoodsName("感冒胶囊");
            entit.setOrderPrice("￥20");
            entit.setOrderId("2000288844448020291");
            mOrderEntities.add(entit);
        }
        for (int i = 0; i < 5; i++) {
            AllOrderEntity entit = new AllOrderEntity();
            entit.setDrugStroe("BB药店");
            entit.setOrderStatuster("待评价" + i);
            entit.setOrderStatus("1");
            entit.setGoodsName("感冒胶囊");
            entit.setOrderPrice("￥20");
            entit.setOrderId("2000288844448020291");
            mOrderEntities.add(entit);
        }
        subExpandableadapter = new OrderExpandableAdapter(this, mOrderEntities);
    }

    private int rowCount;
    private int orderNumRow;
    private int dataRow;
    private int lineRow;
    private int expandableRow;
    private int totalPriceRow;
    private int payWayRow;
    private int line2Row;
    private int consigneeTitleRow;
    private int consigneeNameRow;
    private int consigneePhoneRow;
    private int consignessAddressRow;

    private void setRow() {
        orderNumRow = rowCount++;
        dataRow = rowCount++;
        lineRow = rowCount++;
        expandableRow = rowCount++;
        totalPriceRow = rowCount++;
        payWayRow = rowCount++;
        line2Row = rowCount++;
        consigneeTitleRow = rowCount++;
        consigneeNameRow = rowCount++;
        consigneePhoneRow = rowCount++;
        consignessAddressRow = rowCount++;
    }

    class OrderDetailAdapter extends BaseAdapter {

        private Context context;

        public OrderDetailAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == orderNumRow || position == dataRow || position == totalPriceRow || position == payWayRow) {
                return 0;
            } else if (position == consigneeTitleRow) {
                return 2;
            } else if (position == consigneeNameRow || position == consigneePhoneRow || position == consignessAddressRow) {
                return 0;
            } else if (position == expandableRow) {
                return 3;
            }
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                TextSettingsCell cell = new TextSettingsCell(context);
                if (position == orderNumRow) {
                    cell.setTextAndValue("订单：000000", "2个包裹", false);
                } else if (position == dataRow) {
                    cell.setTextAndValue("2015-2-2 2:20  下单", "正在处理", true);
                } else if (position == totalPriceRow) {
                    cell.setTextAndValue("总计", "￥20", false);
                } else if (position == payWayRow) {
                    cell.setTextAndValue("支付方式", "货到付款", true);
                } else if (position == totalPriceRow) {
                    cell.setTextAndValue("总计", "￥20", false);
                } else if (position == consigneeNameRow) {
                    cell.setTextAndValue("收获人姓名", "xxx", false);
                } else if (position == consigneePhoneRow) {
                    cell.setTextAndValue("联系方式", "133333333333", false);
                } else if (position == consignessAddressRow) {
                    cell.setTextAndValue("地址", "北京朝阳区", false);
                }
                return cell;
            } else if (type == 1) {
                return new ShadowSectionCell(context);
            } else if (type == 2) {
                HeaderCell cell = new HeaderCell(context);
                cell.setTextColor(getResources().getColor(R.color.theme_title));
                if (position == consigneeTitleRow) {
                    cell.setText("收货人信息");
                }
                return cell;
            } else if (type == 3) {
                LinearLayout linearLayout = new LinearLayout(context);
                final ExpandableListView subListView = new ExpandableListView(context);
                subListView.setAdapter(subExpandableadapter);
                subListView.setGroupIndicator(null);
                subListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                int count = subListView.getCount();
                for (int i = 0; i < count; i++) {
                    subListView.expandGroup(i);
                }
                setListViewHeight(subListView);
                linearLayout.addView(subListView);
                subListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        setListViewHeight(subListView);
                        return false;
                    }
                });
                return linearLayout;
            }
            return null;
        }
    }

    private void setListViewHeight(ExpandableListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
