package com.romens.yjk.health.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.OrderDetailActivity;
import com.romens.yjk.health.ui.OrderEvaluateActivity;
import com.romens.yjk.health.ui.cells.KeyAndViewCell;
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
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(adapterContext).inflate(R.layout.list_item_order_complete, null);
        }
        TextView titleTextView = (TextView) view.findViewById(R.id.order_title);
        TextView moneyTextView = (TextView) view.findViewById(R.id.order_money);
        TextView dateTextView = (TextView) view.findViewById(R.id.order_date);
        BackupImageView medicineImg = (BackupImageView) view.findViewById(R.id.order_img);
//        TextView countTextView = (TextView) view.findViewById(R.key.order_count);
        RelativeLayout btnLayout = (RelativeLayout) view.findViewById(R.id.order_btn_layout);
        btnLayout.setVisibility(View.GONE);
        if (childPosition == getChildrenCount(groupPosition)-1) {
            btnLayout.setVisibility(View.VISIBLE);
        }

        TextView buyAgainBtn = (TextView) view.findViewById(R.id.order_all_buy_again);
        TextView evaluateBtn = (TextView) view.findViewById(R.id.order_all_evaluate_btn);
        TextView cancelBtn = (TextView) view.findViewById(R.id.order_all_buy_cancel);
        cancelBtn.setText("取消订单");
        evaluateBtn.setText("确认收货");
        cancelBtn.setVisibility(View.VISIBLE);

        final AllOrderEntity entity = typeEntitiesList.get(groupPosition).get(childPosition);
        if (entity.getPicSmall() != null) {
            medicineImg.setImageUrl(entity.getPicSmall(), null, null);
        }else {
            medicineImg.setImageResource(R.drawable.no_img_upload);
        }
        titleTextView.setText(entity.getGoodsName());
//        countTextView.setText("x" + entity.getMerCount());
        moneyTextView.setText("￥" + entity.getOrderPrice());
        dateTextView.setText(entity.getCreateDate());
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(adapterContext, OrderDetailActivity.class);
                intent.putExtra("orderId", entity.getOrderId());
                adapterContext.startActivity(intent);
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
                        Toast.makeText(adapterContext, "确认收货", Toast.LENGTH_SHORT).show();
                        requestOrderList(userGuid, MyOrderActivity.ORDER_TYPE_BEING);

                        Intent intent = new Intent(adapterContext, OrderEvaluateActivity.class);
                        intent.putExtra("orderEntity", typeEntitiesList.get(groupPosition).get(childPosition));
                        intent.putExtra("fragmentIndex", 1);
                        ((FragmentActivity) adapterContext).finish();
                        adapterContext.startActivity(intent);
                    } else {
                        Toast.makeText(adapterContext, "确认收货错误", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg == null) {
                } else {
                    Toast.makeText(adapterContext, "出现未知错误", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "ERROR---->" + errorMsg.msg);
                }
                needHideProgress();
            }
        });
    }
}