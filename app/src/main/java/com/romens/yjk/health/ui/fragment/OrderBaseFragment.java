package com.romens.yjk.health.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.OrderCommitActivity;
import com.romens.yjk.health.ui.activity.ShoppingCartActivity;
import com.romens.yjk.health.ui.components.ToastCell;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 2016-04-13 10:21
 * @description
 */
public abstract class OrderBaseFragment extends AppFragment {

    protected void tryCancelOrder(final String orderId) {
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
                        requestCancelOrderList(orderId);
                    }
                }).create().show();
    }

    private void requestCancelOrderList(final String orderId) {
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
                                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange,orderId);
                                onCancelOrderCompleted();
                                return;
                            }
                        }
                        ToastCell.toast(getContext(), "取消失败");
                    }
                }).build();
        ConnectManager.getInstance().request(getContext(), content);
    }

    protected void onCancelOrderCompleted() {
    }


    protected void tryCompleteOrder(final String orderNo, final String orderID) {
        String message = String.format("订单 %s 是否确定收货?", orderNo);
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
                        requestConfirmReceive(orderID);
                    }
                }).create().show();
    }

    private void requestConfirmReceive(final String orderID) {
        needShowProgress("正在处理...");
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", UserSession.getInstance().getUser());
        args.put("ORDERID", orderID);
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
                                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange,orderID);
                                Intent intent = new Intent(getActivity(), OrderCommitActivity.class);
                                intent.putExtra(OrderCommitActivity.ARGUMENT_KEY_ORDER_ENTITY, orderID);
                                startActivity(intent);
                            }
                        } else {
                            ToastCell.toast(getActivity(), "确认收货失败!");
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(getContext(), content);
    }

    /**
     * 重新购买
     * @param orderNo 订单编号
     */
    protected void putOrderGoodsToShoppingCart(String orderNo) {
        needShowProgress("正在处理...");
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("ORDERCODE", orderNo);
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

    /**
     * 评价此单
     * @param orderId
     */
    protected void doCommitOrder(String orderId){
        Intent intent = new Intent(getActivity(), OrderCommitActivity.class);
        intent.putExtra(OrderCommitActivity.ARGUMENT_KEY_ORDER_ENTITY, orderId);
        startActivity(intent);
    }
}
