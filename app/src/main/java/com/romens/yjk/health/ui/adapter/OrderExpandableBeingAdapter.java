package com.romens.yjk.health.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.OrderEvaluateActivity;
import com.romens.yjk.health.ui.fragment.OrderFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/9/24.
 * 订单页面中可扩展的listview的Adapter
 */
public class OrderExpandableBeingAdapter extends BaseExpandableAdapter {

    public OrderExpandableBeingAdapter(Context adapterContext, List<AllOrderEntity> orderEntities) {
        super(adapterContext, orderEntities);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        int type = getGroupType(groupPosition);
//        if (type == 0) {
        if (convertView == null) {
            convertView = new TextSettingsCell(adapterContext);
        }
        TextSettingsCell cell = (TextSettingsCell) convertView;
        cell.setTextAndValue("订单编号：" + typeList.get(groupPosition), typeEntitiesList.get(groupPosition).get(0).getOrderStatuster(), true);
        cell.setValueTextColor(adapterContext.getResources().getColor(R.color.theme_sub_title));
//        } else if (type == 1) {
//            if (convertView == null) {
//                convertView = new ShadowSectionCell(adapterContext);
//            }
//        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(adapterContext).inflate(R.layout.list_item_order_complete, null);
        TextView titleTextView = (TextView) view.findViewById(R.id.order_title);
        TextView moneyTextView = (TextView) view.findViewById(R.id.order_money);
        TextView dateTextView = (TextView) view.findViewById(R.id.order_date);
//        TextView countTextView = (TextView) view.findViewById(R.id.order_count);

        Button buyAgainBtn = (Button) view.findViewById(R.id.order_all_buy_again);
        Button evaluateBtn = (Button) view.findViewById(R.id.order_all_evaluate_btn);
        Button cancelBtn = (Button) view.findViewById(R.id.order_all_buy_cancel);
        cancelBtn.setVisibility(View.VISIBLE);

        final AllOrderEntity entity = typeEntitiesList.get(groupPosition).get(childPosition);
        titleTextView.setText(entity.getGoodsName());
//        countTextView.setText("x" + entity.getMerCount());
        moneyTextView.setText("￥" + entity.getOrderPrice());
        dateTextView.setText(entity.getCreateDate());
        evaluateBtn.setBackgroundResource(R.drawable.order_complete);
        evaluateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(adapterContext, "click-->确认订单", Toast.LENGTH_SHORT).show();
                needShowProgress("正在处理...");
                requestConfirmReceive(userGuid, entity.getOrderId(), groupPosition, childPosition);
            }
        });
        buyAgainBtn.setBackgroundResource(R.drawable.order_follow);
//        buyAgainBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(adapterContext, "click-->订单跟踪", Toast.LENGTH_SHORT).show();
//            }
//        });
        buyAgainBtn.setVisibility(View.GONE);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog(userGuid, entity.getOrderId());
            }
        });
        return view;
    }

    private void requestCancelOrderList(final String userGuid, String orderId) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("ORDERID", orderId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "CancelOrder", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol).build();
        FacadeClient.request(adapterContext, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(adapterContext, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String requestCode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        requestCode = jsonObject.getString("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("yes")) {
                        Toast.makeText(adapterContext, "取消成功", Toast.LENGTH_SHORT).show();
                        requestOrderList(userGuid, MyOrderActivity.ORDER_TYPE_BEING);
                    } else {
                        Toast.makeText(adapterContext, "取消失败", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg == null) {
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                }
                needHideProgress();
            }
        });
    }

    public void showCancelDialog(final String userGuid, final String orderId) {
        new AlertDialog.Builder(adapterContext).setTitle("确定删除订单吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        needShowProgress("正在处理...");
                        requestCancelOrderList(userGuid, orderId);
                    }
                }).create().show();
    }

    //访问确认收获
    private void requestConfirmReceive(final String userGuid, String orderId, final int groupPosition, final int childPosition) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("ORDERID", orderId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "ConfirmReceive", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
//                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//                }))
                .build();
        FacadeClient.request(adapterContext, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(adapterContext, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String requestCode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        requestCode = jsonObject.getString("success");
                        Log.e("tag", "--requestCode--->" + requestCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("yes")) {
                        Toast.makeText(adapterContext, "确认收获", Toast.LENGTH_SHORT).show();
                        requestOrderList(userGuid, MyOrderActivity.ORDER_TYPE_BEING);

                        Intent intent = new Intent(adapterContext, OrderEvaluateActivity.class);
                        intent.putExtra("orderEntity", typeEntitiesList.get(groupPosition).get(childPosition));
                        adapterContext.startActivity(intent);
//                        AppNotificationCenter.getInstance().addObserver(adapterContext, AppNotificationCenter.orderCompleteAdd);
//                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.orderCompleteAdd);
                    } else {
                        Toast.makeText(adapterContext, "确认收获错误", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg == null) {
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                    Toast.makeText(adapterContext, "出现未知错误", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "ERROR---->" + errorMsg.msg);
                }
                needHideProgress();
            }
        });
    }
}