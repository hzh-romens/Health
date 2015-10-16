package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ExpandableListView;
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
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.model.GoodsListEntity;
import com.romens.yjk.health.model.OrderListEntity;
import com.romens.yjk.health.ui.adapter.HistoryAdapter;
import com.romens.yjk.health.ui.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/10/13.
 */
public class HistoryActivity extends BaseActivity {

    private ExpandableListView listView;
    private SwipeRefreshLayout refreshLayout;
    private HistoryAdapter adapter;

    private List<GoodsListEntity> goodsListEntities;

    private String userGuid = "3333";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = actionBarEvent();
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);

        refreshLayout = new SwipeRefreshLayout(this);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        container.addView(refreshLayout);

        listView = new ExpandableListView(this);
        listView.setGroupIndicator(null);
        adapter = new HistoryAdapter(this);
        listView.setAdapter(adapter);
        refreshLayout.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        initData();
    }

    private ActionBar actionBarEvent() {
        ActionBar actionBar = new ActionBar(this);
        actionBar.setTitle("历史游览");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(0, R.drawable.check_blue);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                    if (goodsListEntities != null && goodsListEntities.size() > 0) {
                        for (GoodsListEntity entity : adapter.getSelectEntities()) {
                            goodsListEntities.remove(entity);
                        }
                    }
                    refreshListView();
                }
            }
        });
        return actionBar;
    }

    private void initData() {
//        requestOrderDetailList(userGuid, "");
        goodsListEntities = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            GoodsListEntity goodsEntity = new GoodsListEntity();
            goodsEntity.setGoodsGuid("2A3B8A63-8386-4398-8F8B-712B162F552B");
            goodsEntity.setBuyCount("1");
            goodsEntity.setGoodsPrice("20.0");
            goodsEntity.setName("盐酸环丙沙星片");
            goodsEntity.setCode("0101101");
            goodsEntity.setGoodsUrl("http://files.yunuo365.com/image/conew_0101101_small.jpg");
            //GOODSBIGURL
            goodsEntity.setDetailDescitption("aaaa");
            goodsEntity.setSpec("0.25g*12片");
            goodsEntity.setGoodsSortGuid("XH110060101");
            goodsEntity.setShopId("9C5B01CB-D296-4C57-9683-97888888888");
            goodsEntity.setShopName("金象大药房安定门店");
            goodsListEntities.add(goodsEntity);
        }
        for (int i = 0; i < 5; i++) {
            GoodsListEntity goodsEntity = new GoodsListEntity();
            goodsEntity.setGoodsGuid("2A3B8A63-8386-4398-8F8B-712B162F552B");
            goodsEntity.setBuyCount("1");
            goodsEntity.setGoodsPrice("20.0");
            goodsEntity.setName("盐酸环丙沙星片");
            goodsEntity.setCode("0101101");
            goodsEntity.setGoodsUrl("http://files.yunuo365.com/image/conew_0101101_small.jpg");
            //GOODSBIGURL
            goodsEntity.setDetailDescitption("aaaa");
            goodsEntity.setSpec("0.25g*12片");
            goodsEntity.setGoodsSortGuid("XH110060101");
            goodsEntity.setShopId("9C5B01CB-D296-4C57-9683-97888888888");
            goodsEntity.setShopName("平价大药房安定门店");
            goodsListEntities.add(goodsEntity);
        }
        refreshListView();
    }

    private void refreshListView() {
        adapter.setOrderEntities(goodsListEntities);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        int count = listView.getCount();
        for (int i = 0; i < count; i++) {
            listView.expandGroup(i);
        }
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
        FacadeClient.request(HistoryActivity.this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(HistoryActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
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
        try {
            JSONObject object = new JSONObject(jsonData);
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

        adapter.setOrderEntities(goodsListEntities);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }
}
