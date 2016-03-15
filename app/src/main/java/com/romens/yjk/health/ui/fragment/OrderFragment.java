package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.romens.android.ui.cells.LoadingCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.adapter.OrderAllAdpter;
import com.romens.yjk.health.ui.adapter.BaseExpandableAdapter;
import com.romens.yjk.health.ui.adapter.OrderEvaluateAdapter;
import com.romens.yjk.health.ui.adapter.OrderAlreadyCompleteAdapter;
import com.romens.yjk.health.ui.adapter.OrderBeingAdapter;
import com.romens.yjk.health.ui.cells.ImageAndTextCell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/10/22.
 */
public class OrderFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableListView expandableListView;
    private BaseExpandableAdapter adapter;
    private LoadingCell loadingCell;

    private List<AllOrderEntity> mOrderEntities;
    private String userGuid;

    private ImageAndTextCell attachView;
    private int fragmentType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fragmentType = getArguments().getInt("fragmentType");
        userGuid = UserGuidConfig.USER_GUID;
        fragmentTypeBase = fragmentType + "";
        mOrderEntities = new ArrayList<>();
        switch (fragmentType) {
            case MyOrderActivity.ORDER_TYPE_ALL:
                adapter = new OrderAllAdpter(getActivity(), mOrderEntities);
                break;
            case MyOrderActivity.ORDER_TYPE_COMPLETE:
                adapter = new OrderAlreadyCompleteAdapter(getActivity(), mOrderEntities);
                break;
            case MyOrderActivity.ORDER_TYPE_EVALUATE:
                adapter = new OrderEvaluateAdapter(getActivity(), mOrderEntities);
                break;
            case MyOrderActivity.ORDER_TYPE_BEING:
                adapter = new OrderBeingAdapter(getActivity(), mOrderEntities);
                break;
        }
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout content = new FrameLayout(context);
        loadingCell = new LoadingCell(getActivity());
        content.addView(loadingCell);
        swipeRefreshLayout = new SwipeRefreshLayout(context);
        content.addView(swipeRefreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        requestOrderList(userGuid, fragmentType);
        expandableListView = new ExpandableListView(context);
        swipeRefreshLayout.addView(expandableListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        expandableListView.setAdapter(adapter);
        expandableListView.setVerticalScrollBarEnabled(false);
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long key) {
                parent.expandGroup(groupPosition);
                return true;
            }
        });

        addCellView(content);
//        refershContentView();
        showProgressLayout();
        return content;
    }

    private void showProgressLayout() {
        swipeRefreshLayout.setVisibility(View.GONE);
        attachView.setVisibility(View.GONE);
        loadingCell.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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
                refershContentView();
                Toast.makeText(getActivity(), msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                refershContentView();
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
        Collections.sort(mOrderEntities, new Comparator<AllOrderEntity>() {
            @Override
            public int compare(AllOrderEntity lhs, AllOrderEntity rhs) {
                return rhs.getCreateDate().compareTo(lhs.getCreateDate());
            }
        });
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
        loadingCell.setVisibility(View.GONE);
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
