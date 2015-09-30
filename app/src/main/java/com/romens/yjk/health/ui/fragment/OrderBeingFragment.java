package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.adapter.OrderExpandableAdapter;
import com.romens.yjk.health.ui.adapter.OrderExpandableAlreadyCompleteAdapter;
import com.romens.yjk.health.ui.adapter.OrderExpandableBeingAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/9/22.
 * 我的订单中的订单处理中页面
 */
public class OrderBeingFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<AllOrderEntity> mOrderEntities;

    private OrderExpandableBeingAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestOrderList();
        initData();
        adapter = new OrderExpandableBeingAdapter(getActivity(), mOrderEntities);
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
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout content = new FrameLayout(context);
        swipeRefreshLayout = new SwipeRefreshLayout(context);
        content.addView(swipeRefreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AllOrderAsynTask().execute();
            }
        });
        ExpandableListView  expandableListView = new ExpandableListView(context);
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null);
        int count=expandableListView.getCount();
        for (int i = 0; i < count; i++) {
            expandableListView.expandGroup(i);
        }
        swipeRefreshLayout.addView(expandableListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return content;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }

    //刷新时，去获取新数据
    class AllOrderAsynTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void requestOrderList() {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", "3333");
        args.put("ORDER_STATUS", "2");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "getMyOrders", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(getActivity(), message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(getActivity(), msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    setOrderData(responseProtocol.getResponse());
                }
                if (errorMsg == null) {
                } else {
                    android.util.Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }
    public void setOrderData(List<LinkedTreeMap<String, String>> response) {
        int count = response == null ? 0 : response.size();
        if (count <= 0) {
            return;
        }
        mOrderEntities = new ArrayList<>();
        for (LinkedTreeMap<String, String> item : response) {
            AllOrderEntity entity = AllOrderEntity.mapToEntity(item);
            mOrderEntities.add(entity);
        }
        adapter.notifyDataSetChanged();
    }
}
