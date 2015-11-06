package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
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
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.model.GoodsListEntity;
import com.romens.yjk.health.model.OrderListEntity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.OrderEvaluateActivity;
import com.romens.yjk.health.ui.cells.KeyAndValueCell;
import com.romens.yjk.health.ui.utils.TransformDateUitls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/9/24.
 * 订单页面中可扩展的listview的Adapter
 */
public class OrderExpandableAlreadyCompleteAdapter extends BaseExpandableAdapter {

    private List<GoodsListEntity> goodsListEntities;

    public OrderExpandableAlreadyCompleteAdapter(Context adapterContext, List<AllOrderEntity> orderEntities) {
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
//        TextView countTextView = (TextView) view.findViewById(R.key.order_count);

        Button buyAgainBtn = (Button) view.findViewById(R.id.order_all_buy_again);
        Button evaluateBtn = (Button) view.findViewById(R.id.order_all_evaluate_btn);
        final AllOrderEntity entity = typeEntitiesList.get(groupPosition).get(childPosition);
        titleTextView.setText(entity.getGoodsName());
//        countTextView.setText("x" + entity.getMerCount());
        moneyTextView.setText("￥" + entity.getOrderPrice());
        TransformDateUitls.getDate(entity.getCreateDate());
        dateTextView.setText(entity.getCreateDate());
        evaluateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(adapterContext, OrderEvaluateActivity.class);
                intent.putExtra("fragmentIndex", 2);
                intent.putExtra("orderEntity", typeEntitiesList.get(groupPosition).get(childPosition));
                adapterContext.startActivity(intent);
                ((FragmentActivity) adapterContext).finish();
            }
        });
        buyAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(adapterContext, "click-->再来一单", Toast.LENGTH_SHORT).show();
                requestOrderDetailList(UserGuidConfig.USER_GUID, entity.getOrderId());
            }
        });
        return view;
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
        FacadeClient.request(adapterContext, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(adapterContext, msg.msg, Toast.LENGTH_SHORT).show();
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
        OrderListEntity orderListEntity = new OrderListEntity();
        try {
            JSONObject object = new JSONObject(jsonData);
            JSONArray array = object.getJSONArray("GOODSLIST");
            for (int i = 0; i < array.length(); i++) {
                JSONObject subObjcet = array.getJSONObject(i);
                requestToBuy(subObjcet.getString("GOODSPRICE"), subObjcet.getString("GOODSGUID"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void requestToBuy(String PRICE, String GUID) {
        //加入购物车,用户暂定为2222
        int lastTime = DBInterface.instance().getDiscoveryDataLastTime();
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("GOODSGUID", GUID);
        args.put("USERGUID", UserGuidConfig.USER_GUID);
        args.put("BUYCOUNT", "1");
        args.put("PRICE", PRICE);

        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "InsertIntoCar", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(adapterContext, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("InsertIntoCar", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    if ("ERROE".equals(response)) {
                        Toast.makeText(adapterContext, "加入购物车异常", Toast.LENGTH_SHORT).show();
                        Log.e("tag", "---newOrder--error->");
                    } else {
                        Toast.makeText(adapterContext, "成功加入购物车", Toast.LENGTH_SHORT).show();
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, 1);
                        Log.e("tag", "---newOrder--ok->");
                    }
                } else {
                    Log.e("InsertIntoCar", errorMsg.toString() + "====" + errorMsg.msg);
                }
            }
        });
    }
}