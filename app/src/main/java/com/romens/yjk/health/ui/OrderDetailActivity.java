package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.HeaderCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.model.GoodsListEntity;
import com.romens.yjk.health.model.OrderListEntity;
import com.romens.yjk.health.ui.adapter.OrderExpandableDetailAdapter;
import com.romens.yjk.health.ui.cells.KeyAndValueCell;
import com.romens.yjk.health.ui.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/9/24.
 * 订单详情
 */
public class OrderDetailActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private OrderDetailAdapter adapter;
    private OrderListEntity orderListEntity;
    private List<GoodsListEntity> goodsListEntities;
    private OrderExpandableDetailAdapter subExpandableadapter;

    private String userGuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userGuid = UserGuidConfig.USER_GUID;
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = actionBarEvent();
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        swipeRefreshLayout = new SwipeRefreshLayout(this);
        UIHelper.setupSwipeRefreshLayoutProgress(swipeRefreshLayout);
        container.addView(swipeRefreshLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestOrderDetailList(userGuid, orderId);
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
        adapter = new OrderDetailAdapter(this);
        subExpandableadapter = new OrderExpandableDetailAdapter(this);
//        listView.setAdapter(adapter);
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

    private String orderId;

    public void initData() {
        setRow();
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        requestOrderDetailList(userGuid, orderId);
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
        private OrderListEntity orderListEntity;

        public void setOrderListEntity(OrderListEntity orderListEntity) {
            this.orderListEntity = orderListEntity;
        }

        public OrderDetailAdapter(Context context) {
            this.context = context;
            this.orderListEntity = new OrderListEntity();
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
            if (position == dataRow || position == totalPriceRow || position == payWayRow) {
                return 0;
            } else if (position == consigneeTitleRow || position == orderNumRow) {
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
                KeyAndValueCell cell = new KeyAndValueCell(context);
                if (position == dataRow) {
                    cell.setKeyAndValue(orderListEntity.getCreateTime() + "  下单", orderListEntity.getOrderStatusStr(), true);
                    cell.setValueTextColor(context.getResources().getColor(R.color.order_statu_color));
                } else if (position == totalPriceRow) {
                    cell.setKeyAndValue("总计", "￥" + orderListEntity.getOrderPrice(), false);
                    cell.setValueTextColor(context.getResources().getColor(R.color.order_money_color));
                } else if (position == payWayRow) {
//                    String result = "";
//                    if (orderListEntity.getDeliverType().equals("1")) {
//                        result = "药店派送";
//                    } else if (orderListEntity.getDeliverType().equals("2")) {
//                        result = "到店自取";
//                    }
//                    cell.setKeyAndValue("支付方式", result, true);
                    cell.setKeyAndValue("支付方式", orderListEntity.getDeliverType(), true);
                } else if (position == consigneeNameRow) {
                    cell.setKeyAndValue("收获人姓名", orderListEntity.getReceiver(), false);
                } else if (position == consigneePhoneRow) {
                    cell.setKeyAndValue("联系方式", orderListEntity.getTelephone(), false);
                } else if (position == consignessAddressRow) {
                    cell.setKeyAndValue("地址", orderListEntity.getAddress(), false);
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
                if (position == orderNumRow) {
                    cell.setText("订单编号：" + orderListEntity.getOrderNo());
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
                        return true;
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

    private void requestOrderDetailList(String userGuid, String orderId) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("ORDERID", orderId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "getMyOrderDetail", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(OrderDetailActivity.this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(OrderDetailActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
//                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    ResponseProtocol<String> responseEntity = (ResponseProtocol<String>) msg.protocol;

                    setOrderData(responseEntity.getResponse());
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    public void setOrderData(String jsonData) {
        if (jsonData == null) {
            return;
        }
        goodsListEntities = new ArrayList<>();
        orderListEntity = new OrderListEntity();
        try {
            JSONObject object = new JSONObject(jsonData);
            orderListEntity.setOrderId(object.getString("ORDER_ID"));
            orderListEntity.setOrderNo(object.getString("ORDERNO"));
            orderListEntity.setCreateTime(object.getString("CREATETIME"));
            orderListEntity.setOrderPrice(object.getString("ORDERPRICE"));
            orderListEntity.setReceiver(object.getString("RECEIVER"));
            orderListEntity.setAddress(object.getString("ADDRESS"));
            orderListEntity.setDeliverType(object.getString("DELIVERYTYPE"));
            orderListEntity.setOrderStatus(object.getString("ORDER_STATUS"));
            orderListEntity.setOrderStatusStr(object.getString("ORDERSTATUSSTR"));
            JSONArray array = object.getJSONArray("GOODSLIST");
            for (int i = 0; i < array.length(); i++) {
                JSONObject subObjcet = array.getJSONObject(i);
                GoodsListEntity goodsEntity = new GoodsListEntity();
                goodsEntity.setGoodsGuid(subObjcet.getString("GOODSGUID"));
                goodsEntity.setBuyCount(subObjcet.getString("BUYCOUNT"));
                goodsEntity.setGoodsPrice(subObjcet.getString("GOODSPRICE"));
                goodsEntity.setName(subObjcet.getString("NAME"));
                goodsEntity.setCode(subObjcet.getString("CODE"));
                goodsEntity.setGoodsUrl(subObjcet.getString("GOODURL"));
                //GOODSBIGURL
                goodsEntity.setDetailDescitption(subObjcet.getString("DETAILDESCRIPTION"));
                goodsEntity.setSpec(subObjcet.getString("SPEC"));
                goodsEntity.setGoodsSortGuid(subObjcet.getString("GOODSSORTGUID"));
                goodsEntity.setShopId(subObjcet.getString("SHOPID"));
                goodsEntity.setShopName(subObjcet.getString("SHOPNAME"));
                goodsListEntities.add(goodsEntity);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        subExpandableadapter.setOrderEntities(goodsListEntities);
        subExpandableadapter.notifyDataSetChanged();

        adapter.setOrderListEntity(orderListEntity);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }
}
