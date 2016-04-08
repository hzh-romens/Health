package com.romens.yjk.health.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.LoadingCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.common.OrderStatus;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.OrderDao;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.OrderCommitActivity;
import com.romens.yjk.health.ui.OrderDetailActivity;
import com.romens.yjk.health.ui.OrderEvaluateActivity;
import com.romens.yjk.health.ui.activity.ShoppingCartActivity;
import com.romens.yjk.health.ui.adapter.OrderListViewAdapter;
import com.romens.yjk.health.ui.cells.ImageAndTextCell;
import com.romens.yjk.health.ui.components.ToastCell;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/10/22.
 */
public class OrderFragment extends AppFragment implements AppNotificationCenter.NotificationCenterDelegate {
    public static final String ARGUMENTS_KEY_ORDER_STATUS = "key_order_status";
    private SwipeRefreshLayout swipeRefreshLayout;
    //    private ExpandableListView expandableListView;
//    private OrderAdapter adapter;
    private RecyclerView listView;
    private OrderListViewAdapter listViewAdapter;
    private LoadingCell loadingCell;

    private ImageAndTextCell attachView;
    private int orderStatus = OrderStatus.ALL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onOrderStateChange);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onOrderDataUpdated);

        this.orderStatus = getArguments().getInt(ARGUMENTS_KEY_ORDER_STATUS, OrderStatus.ALL);
        listViewAdapter = new OrderListViewAdapter(getActivity(), new OrderListViewAdapter.Delegate() {
            @Override
            public void onItemSelect(OrderEntity orderEntity) {
                openOrderDetail(orderEntity);
            }

            @Override
            public void onCompleted(OrderEntity entity) {
                tryCompleteOrder(entity);
            }

            @Override
            public void onBuyAgain(OrderEntity entity) {
                putOrderGoodsToShoppingCart(entity);
            }

            @Override
            public void onCancel(OrderEntity entity) {
                tryCancelOrder(entity);
            }

            @Override
            public void onCommit(OrderEntity entity) {
                Intent intent = new Intent(getActivity(), OrderCommitActivity.class);
                intent.putExtra(OrderCommitActivity.ARGUMENT_KEY_ORDER_ENTITY, entity.orderId);
                startActivity(intent);
            }

            @Override
            public void onLookCommit(OrderEntity entity) {

            }

            @Override
            public void onRetry(OrderEntity entity) {
                putOrderGoodsToShoppingCart(entity);
            }
        });
    }

    private void putOrderGoodsToShoppingCart(OrderEntity entity) {
        needShowProgress("正在处理...");
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("ORDERCODE", entity.orderNo);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "InsertIntoCarFromOrder", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect content = new RMConnect.Builder(MyOrderActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        needHideProgress();
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (!response.has("ERROR")) {
                                ArrayList<String> shoppingCart = new ArrayList<>();
                                int size = response.size();
                                for (int i = 0; i < size; i++) {
                                    shoppingCart.add(response.get(i).asText());
                                }
                                Intent intent = new Intent(getActivity(), ShoppingCartActivity.class);
                                Bundle arguments = new Bundle();
                                arguments.putStringArrayList(ShoppingCartFragment.ARGUMENTS_KEY_SELECT_GOODS, shoppingCart);
                                intent.putExtras(arguments);
                                startActivity(intent);
                                return;
                            }
                        }
                        ToastCell.toast(getContext(), "失败,请重试!");
                    }
                }).build();
        ConnectManager.getInstance().request(getContext(), content);
    }

    private void openOrderDetail(OrderEntity entity) {
        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
        intent.putExtra("orderId", entity.orderId);
        startActivity(intent);
    }

    private void tryCancelOrder(final OrderEntity entity) {
        new AlertDialog.Builder(getActivity()).setTitle("确定取消订单吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestCancelOrderList(entity.orderId);
                    }
                }).create().show();
    }

    private void requestCancelOrderList(String orderId) {
        needShowProgress("正在处理...");
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", UserSession.getInstance().getUser());
        args.put("ORDERID", orderId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "CancelOrder", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect content = new RMConnect.Builder(MyOrderActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        needHideProgress();
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            String requestCode = response.get("success").asText();
                            if (requestCode.equals("yes")) {
                                ToastCell.toast(getContext(), "取消成功");
                                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange);
                                return;
                            }
                        }
                        ToastCell.toast(getContext(), "取消失败");
                    }
                }).build();
        ConnectManager.getInstance().request(getContext(), content);
    }

    private void tryCompleteOrder(final OrderEntity entity) {
        String message = String.format("订单 %s 是否确定收货?", entity.orderNo);
        new AlertDialog.Builder(getActivity()).setTitle("提示")
                .setMessage(message)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestConfirmReceive(entity);
                    }
                }).create().show();
    }

    public void requestConfirmReceive(final OrderEntity entity) {
        needShowProgress("正在处理...");
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", UserSession.getInstance().getUser());
        args.put("ORDERID", entity.orderId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "ConfirmReceive", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect content = new RMConnect.Builder(MyOrderActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        needHideProgress();
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            String requestCode = response.get("success").asText();
                            if (requestCode.equals("yes")) {
                                ToastCell.toast(getActivity(), "确认收货成功!");
                                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange);
                                Intent intent = new Intent(getActivity(), OrderCommitActivity.class);
                                intent.putExtra(OrderCommitActivity.ARGUMENT_KEY_ORDER_ENTITY, entity.orderId);
                                startActivity(intent);
                            }
                        } else {
                            ToastCell.toast(getActivity(), "确认收货失败!");
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(getContext(), content);
    }

//    public void requestOrderBuyAgain(OrderEntity entity) {
//        Map<String, String> args = new FacadeArgs.MapBuilder().build();
//        args.put("ORDERID", entity.orderId);
//        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "getMyOrderDetail", args);
//        protocol.withToken(FacadeToken.getInstance().getAuthToken());
//
//        Connect content = new RMConnect.Builder(MyOrderActivity.class)
//                .withProtocol(protocol)
//                .withParser(new JSONNodeParser())
//                .withDelegate(new Connect.AckDelegate() {
//                    @Override
//                    public void onResult(Message message, Message errorMessage) {
//                        needHideProgress();
//                        if (errorMessage != null) {
//                            ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
//                            ResponseProtocol<String> responseEntity = (ResponseProtocol<String>) msg.protocol;
//                            setOrderBuyAgainData(responseEntity.getResponse());
//                        } else {
//                            ToastCell.toast(getActivity(), "确认收货失败!");
//                        }
//                    }
//                }).build();
//        ConnectManager.getInstance().request(getContext(), content);
//    }
//
//    //再来一单,->保存数据
//    public void setOrderBuyAgainData(String jsonData) {
//        if (jsonData == null) {
//            return;
//        }
//        try {
//            JSONObject object = new JSONObject(jsonData);
//            JSONArray array = object.getJSONArray("GOODSLIST");
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject subObjcet = array.getJSONObject(i);
//                requestToBuy(subObjcet.getString("GOODSPRICE"), subObjcet.getString("GOODSGUID"));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    //再来一单,->请求加入购物车
//    public void requestToBuy(String PRICE, String GUID) {
//        int lastTime = DBInterface.instance().getDiscoveryDataLastTime();
//        Map<String, String> args = new FacadeArgs.MapBuilder().build();
//        args.put("GOODSGUID", GUID);
//        args.put("USERGUID", UserGuidConfig.USER_GUID);
//        args.put("BUYCOUNT", "1");
//        args.put("PRICE", PRICE);
//
//        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "InsertIntoCar", args);
//        protocol.withToken(FacadeToken.getInstance().getAuthToken());
//        Message message = new Message.MessageBuilder()
//                .withProtocol(protocol)
//                .build();
//        FacadeClient.request(adapterContext, message, new FacadeClient.FacadeCallback() {
//            @Override
//            public void onTokenTimeout(Message msg) {
//                Log.e("InsertIntoCar", "ERROR");
//            }
//
//            @Override
//            public void onResult(Message msg, Message errorMsg) {
//                if (errorMsg == null) {
//                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
//                    String response = responseProtocol.getResponse();
//                    if ("ERROE".equals(response)) {
//                        Toast.makeText(adapterContext, "加入购物车异常", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(adapterContext, "成功加入购物车", Toast.LENGTH_SHORT).show();
//                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, 1);
//                    }
//                } else {
//                    Log.e("InsertIntoCar", errorMsg.toString() + "====" + errorMsg.msg);
//                }
//            }
//        });
//    }


    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout content = new FrameLayout(context);
        loadingCell = new LoadingCell(getActivity());
        content.addView(loadingCell);
        swipeRefreshLayout = new SwipeRefreshLayout(context);
        UIHelper.setupSwipeRefreshLayoutProgress(swipeRefreshLayout);
        content.addView(swipeRefreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.needUpdateOrderData);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
//        expandableListView = new ExpandableListView(context);
//        swipeRefreshLayout.addView(expandableListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
//        expandableListView.setAdapter(adapter);
//        expandableListView.setVerticalScrollBarEnabled(false);
//        expandableListView.setGroupIndicator(null);
//        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long key) {
//                parent.expandGroup(groupPosition);
//                return true;
//            }
//        });
        listView = new RecyclerView(context);
        listView.setLayoutManager(new LinearLayoutManager(context));
        swipeRefreshLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setVerticalScrollBarEnabled(false);
        addCellView(content);
//        refreshContentView();
        showProgressLayout();
        return content;
    }

    private void showProgressLayout() {
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
        listView.setAdapter(listViewAdapter);
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onOrderStateChange);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onOrderDataUpdated);
        super.onDestroy();
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        //requestOrderList(orderStatus);
        loadLocalOrderData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void loadLocalOrderData() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                List<OrderEntity> orderEntities=DBInterface.instance().loadOrderData(orderStatus);
                listViewAdapter.bindData(orderEntities);
                refreshContentView();
            }
        });
    }

    private void requestOrderList(int fragmentType) {
        Map<String, String> args = new HashMap<>();
        args.put("USERGUID", UserSession.getInstance().getUser());
        args.put("ORDERSTATUS", fragmentType + "");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "getMyOrders", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(MyOrderActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        refreshContentView();
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            setOrderData(responseProtocol.getResponse());
                        } else {
                            setOrderData(null);
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(getActivity(), connect);
    }

    public void setOrderData(JsonNode response) {
        List<OrderEntity> entities = new ArrayList<>();
        int count = response == null ? 0 : response.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                entities.add(new OrderEntity(response.get(i)));
            }
            Collections.sort(entities, new Comparator<OrderEntity>() {
                @Override
                public int compare(OrderEntity lhs, OrderEntity rhs) {
                    if (lhs.created == 0 || rhs.created == 0) {
                        return rhs.createDate.compareTo(lhs.createDate);
                    } else {
                        return rhs.created < lhs.created ? -1 : (rhs.created == lhs.created ? 0 : 1);
                    }

                }
            });
        }
        listViewAdapter.bindData(entities);
        refreshContentView();
        swipeRefreshLayout.setRefreshing(false);

    }

    public void refreshContentView() {
        loadingCell.setVisibility(View.GONE);
        if (listViewAdapter.getItemCount() > 0) {
            attachView.setVisibility(View.GONE);
        } else {
            attachView.setVisibility(View.VISIBLE);
        }
    }

    public void requestDataRefreshView() {
        requestOrderList(orderStatus);
    }

    public int getOrderStatus() {
        return orderStatus;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onOrderStateChange);
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.onOrderStateChange) {
            //requestOrderList(orderStatus);
        } else if (i == AppNotificationCenter.onOrderDataUpdated) {
            loadLocalOrderData();
        }
    }
}
