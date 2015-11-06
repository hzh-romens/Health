package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.OrderEvaluateDetailActivity;
import com.romens.yjk.health.ui.adapter.BaseExpandableAdapter;
import com.romens.yjk.health.ui.adapter.OrderExpandableAdapter;
import com.romens.yjk.health.ui.adapter.OrderExpandableAlreadyCompleteAdapter;
import com.romens.yjk.health.ui.adapter.OrderExpandableBeingAdapter;
import com.romens.yjk.health.ui.cells.ImageAndTextCell;
import com.romens.yjk.health.ui.components.logger.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/10/22.
 */
public class OrderFragment extends BaseFragment implements AppNotificationCenter.NotificationCenterDelegate {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableListView expandableListView;
    private BaseExpandableAdapter adapter;

    private List<AllOrderEntity> mOrderEntities;
    private String userGuid;

    private ImageAndTextCell attachView;
    private int fragmentType;

    public OrderFragment(int fragmentType) {
        this.fragmentType = fragmentType;
        userGuid = UserGuidConfig.USER_GUID;
        fragmentTypeBase = fragmentType + "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrderEntities = new ArrayList<>();
        switch (fragmentType) {
            case MyOrderActivity.ORDER_TYPE_COMPLETE:
                adapter = new OrderExpandableAlreadyCompleteAdapter(getActivity(), mOrderEntities);
                break;
            case MyOrderActivity.ORDER_TYPE_EVALUATE:
                adapter = new OrderExpandableAdapter(getActivity(), mOrderEntities);
//                requestOrderList(userGuid);
                break;
            case MyOrderActivity.ORDER_TYPE_BEING:
                adapter = new OrderExpandableBeingAdapter(getActivity(), mOrderEntities);
//                requestOrderList(userGuid);
//                AppNotificationCenter.getInstance().addObserver(getActivity(), AppNotificationCenter.orderCompleteAdd);
                break;
        }
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout content = new FrameLayout(context);
        swipeRefreshLayout = new SwipeRefreshLayout(context);
        content.addView(swipeRefreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mOrderEntities = new ArrayList<>();
                requestOrderList(userGuid, fragmentType);
            }
        });
        requestOrderList(userGuid, fragmentType);
        expandableListView = new ExpandableListView(context);
        swipeRefreshLayout.addView(expandableListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(getActivity(), OrderEvaluateDetailActivity.class);
                AllOrderEntity entity = (AllOrderEntity) adapter.getChild(groupPosition, childPosition);
                intent.putExtra("evaluateDetailEntity", entity);
                startActivity(intent);
                return true;
            }
        });
//        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long key) {
//                parent.expandGroup(groupPosition);
//                Log.e("tag", "-------->" + groupPosition);
//                return true;
//            }
//        });

        addCellView(content);
        refershContentView();
        return content;
    }

    private void addCellView(FrameLayout container) {
        attachView = new ImageAndTextCell(getActivity());
        attachView.setImageAndText(R.drawable.no_order_img, "您还没有相关订单");
        LinearLayout.LayoutParams layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        attachView.setPadding(AndroidUtilities.dp(0), AndroidUtilities.dp(100), AndroidUtilities.dp(0), AndroidUtilities.dp(0));
        attachView.setLayoutParams(layoutParams);
        container.addView(attachView);
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.orderCompleteAdd);
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.orderCompleteAdd);
        super.onDestroy();
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("tag", "isVisibleToUser---->" + isVisibleToUser);
    }

    private void requestOrderList(String userGuid, int fragmentType) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("ORDERSTATUS", fragmentType + "");
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
        refreshView();
    }

    //刷新View
    private void refreshView() {
        refershContentView();
        swipeRefreshLayout.setRefreshing(false);
        adapter.setOrderEntities(mOrderEntities);
        adapter.notifyDataSetChanged();

        int count = expandableListView.getCount();
        for (int i = 0; i < count; i++) {
            expandableListView.expandGroup(i);
        }
    }

    public void refershContentView() {
        if (mOrderEntities != null && mOrderEntities.size() > 0) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            attachView.setVisibility(View.GONE);
        } else {
            swipeRefreshLayout.setVisibility(View.GONE);
            attachView.setVisibility(View.VISIBLE);
        }
    }

    public void requestDataRefreshView() {
        requestOrderList(userGuid, fragmentType);
    }

    public int getFragmentType() {
        return fragmentType;
    }

    public void setmOrderEntities(List<AllOrderEntity> mOrderEntities) {
        this.mOrderEntities = mOrderEntities;
    }

    public void clearListEntities() {
        mOrderEntities.clear();
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.orderCompleteAdd) {
            Fragment fragment = getThisFragment((FragmentActivity) objects[0], MyOrderActivity.ORDER_TYPE_COMPLETE + "");
            if (fragment != null) {
                ((OrderFragment) fragment).requestOrderList(userGuid, MyOrderActivity.ORDER_TYPE_COMPLETE);
            }
        }
    }

    public static Fragment getThisFragment(FragmentActivity context, String type) {
        FragmentManager manager = context.getSupportFragmentManager();
        List<Fragment> fragments = manager.getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            BaseFragment baseFragment = (BaseFragment) fragments.get(i);
            if (baseFragment.getFragmentTypeBase() != null && baseFragment.getFragmentTypeBase().equals(type)) {
                return fragments.get(i);
            }
        }
        return null;
    }
}
