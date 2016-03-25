package com.romens.yjk.health.ui.adapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.log.FileLog;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.model.GoodsListEntity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.OrderDetailActivity;
import com.romens.yjk.health.ui.OrderEvaluateActivity;
import com.romens.yjk.health.ui.OrderEvaluateDetailActivity;
import com.romens.yjk.health.ui.cells.KeyAndViewCell;
import com.romens.yjk.health.ui.fragment.BaseFragment;
import com.romens.yjk.health.ui.fragment.OrderFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/10/22.
 */
public class OrderAdapter extends BaseExpandableListAdapter {

    protected List<List<AllOrderEntity>> typeEntitiesList;
    protected List<String> typeList;
    protected Context adapterContext;
    protected String userGuid = UserGuidConfig.USER_GUID;
    private ProgressDialog progressDialog;

    public OrderAdapter(Context adapterContext, List<AllOrderEntity> orderEntities) {
        this.adapterContext = adapterContext;
        classifyEntity(orderEntities);
    }

    public void setOrderEntities(List<AllOrderEntity> orderEntities) {
        if (typeEntitiesList != null) {
            typeEntitiesList.clear();
        }
        if (typeList != null) {
            typeList.clear();
        }
        classifyEntity(orderEntities);
    }

    private void classifyEntity(List<AllOrderEntity> orderEntities) {
        typeList = new ArrayList<>();
        for (int i = 0; i < orderEntities.size(); i++) {
            boolean flag = true;
            String drugStroe = orderEntities.get(i).getOrderNo();
            for (int j = 0; j < typeList.size(); j++) {
                if (drugStroe.equals(typeList.get(j))) {
                    flag = false;
                }
            }
            if (flag) {
                typeList.add(drugStroe);
            }
        }
        typeEntitiesList = new ArrayList<>();
        for (String drugStroe : typeList) {
            List<AllOrderEntity> tempList = new ArrayList<>();
            for (int i = 0; i < orderEntities.size(); i++) {
                if (drugStroe.equals(orderEntities.get(i).getOrderNo())) {
                    tempList.add(orderEntities.get(i));
                }
            }
            typeEntitiesList.add(tempList);
        }
    }

    @Override
    public int getGroupCount() {
        return typeEntitiesList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Log.e("tag", "-groupPosition-->" + groupPosition);
        Log.e("tag", "-typeEntitiesList.size()-->" + typeEntitiesList.size());
        Log.e("tag", "-size()-->" + typeEntitiesList.get(groupPosition).size());
        return typeEntitiesList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return typeEntitiesList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return typeEntitiesList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new CustomGroupView(adapterContext);
        }
        CustomGroupView groupView = (CustomGroupView) convertView;
        groupView.setInfor(groupPosition);
        return convertView;
    }

    class CustomGroupView extends LinearLayout {
        KeyAndViewCell cell;
        ShadowSectionCell lineCell;

        public CustomGroupView(Context context) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);
            lineCell = new ShadowSectionCell(adapterContext);
            addView(lineCell);
            cell = new KeyAndViewCell(adapterContext);
            addView(cell);
        }

        public void setInfor(int groupPosition) {
            if (groupPosition == 0) {
                lineCell.setVisibility(GONE);
            } else {
                lineCell.setVisibility(VISIBLE);
            }
            cell.setKeyAndRightText("订单编号：" + typeList.get(groupPosition), typeEntitiesList.get(groupPosition).get(0).getOrderStatuster(), true);
            cell.setTextViewColor(adapterContext.getResources().getColor(R.color.order_statu_color));
            cell.setKeyTextColor(adapterContext.getResources().getColor(R.color.theme_title));
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(adapterContext).inflate(R.layout.list_item_order_complete, null);
        }
        TextView titleTextView = (TextView) convertView.findViewById(R.id.order_title);
        TextView moneyTextView = (TextView) convertView.findViewById(R.id.order_money);
        TextView specTextView = (TextView) convertView.findViewById(R.id.order_date);
        BackupImageView medicineImg = (BackupImageView) convertView.findViewById(R.id.order_img);

        final AllOrderEntity entity = typeEntitiesList.get(groupPosition).get(childPosition);
        titleTextView.setText(entity.getGoodsName());
        moneyTextView.setText("￥" + entity.getOrderPrice());
        specTextView.setText(entity.getCreateDate());
        if (entity.getPicSmall() != null) {
            medicineImg.setImageUrl(entity.getPicSmall(), null, null);
        } else {
            medicineImg.setImageResource(R.drawable.no_img_upload);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(adapterContext, OrderDetailActivity.class);
                intent.putExtra("orderId", entity.getOrderId());
                adapterContext.startActivity(intent);
                notifyDataSetChanged();
            }
        });

        String orderState = typeEntitiesList.get(groupPosition).get(childPosition).getOrderStatuster();
        TextView buyAgainBtn = (TextView) convertView.findViewById(R.id.order_all_buy_again);
        TextView evaluateBtn = (TextView) convertView.findViewById(R.id.order_all_evaluate_btn);
        TextView cancelBtn = (TextView) convertView.findViewById(R.id.order_all_buy_cancel);
        if (orderState.equals("未付款")) {
            cancelBtn.setText("取消订单");
            cancelBtn.setVisibility(View.VISIBLE);
            evaluateBtn.setVisibility(View.GONE);
            buyAgainBtn.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cancelBtn.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.rightMargin = AndroidUtilities.dp(16);
            cancelBtn.setLayoutParams(layoutParams);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCancelDialog(userGuid, entity.getOrderId());
                }
            });
        } else if (orderState.equals("交易完成")) {
            buyAgainBtn.setText("再来一单");
            evaluateBtn.setText(" 评    价 ");
            cancelBtn.setVisibility(View.GONE);
            evaluateBtn.setVisibility(View.VISIBLE);
            buyAgainBtn.setVisibility(View.VISIBLE);
            evaluateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(adapterContext, OrderEvaluateActivity.class);
                    intent.putExtra("fragmentIndex", 2);
                    intent.putExtra("orderEntity", typeEntitiesList.get(groupPosition).get(childPosition));
                    adapterContext.startActivity(intent);
//                    ((FragmentActivity) adapterContext).finish();
                }
            });
            buyAgainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestOrderBuyAgain(UserGuidConfig.USER_GUID, entity.getOrderId());
                }
            });
        } else if (orderState.equals("已评价")) {
            evaluateBtn.setText("查看评价");
            cancelBtn.setVisibility(View.GONE);
            evaluateBtn.setVisibility(View.VISIBLE);
            buyAgainBtn.setVisibility(View.GONE);
            evaluateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(adapterContext, OrderEvaluateDetailActivity.class);
                    intent.putExtra("evaluateDetailEntity", entity);
                    adapterContext.startActivity(intent);
                }
            });
        } else if (orderState.equals("交易取消")) {
            evaluateBtn.setText("重新购买");
            evaluateBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.GONE);
            buyAgainBtn.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cancelBtn.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.rightMargin = AndroidUtilities.dp(8);
            cancelBtn.setLayoutParams(layoutParams);
            evaluateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    needShowProgress("正在处理...");
                    requestOrderBuyAgain(UserGuidConfig.USER_GUID, entity.getOrderId());
                }
            });
        } else {
            cancelBtn.setText("取消订单");
            evaluateBtn.setText("确认收货");
            evaluateBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            buyAgainBtn.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cancelBtn.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.rightMargin = AndroidUtilities.dp(8);
            cancelBtn.setLayoutParams(layoutParams);

            evaluateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    needShowProgress("正在处理...");
                    requestConfirmReceive(userGuid, entity.getOrderId(), groupPosition, childPosition);
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCancelDialog(userGuid, entity.getOrderId());
                }
            });
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void needShowProgress(String progressText) {
        if (progressDialog != null) {
            return;
        }
        progressDialog = new ProgressDialog(adapterContext);
        progressDialog.setMessage(progressText);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void needHideProgress() {
        if (progressDialog == null) {
            return;
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(adapterContext.getPackageName(), e);
        }
        progressDialog = null;
    }

    //根据type，请求不同状态下的订单
    public void requestOrderList(String userGuid, final int fragmentType) {
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
        FacadeClient.request(adapterContext, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(adapterContext, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange);
//                    setOrderData(responseProtocol.getResponse(), fragmentType);
                }
                if (errorMsg == null) {
                } else {
                    android.util.Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

//    public void setOrderData(List<LinkedTreeMap<String, String>> response, int fragmentType) {
//        int count = response == null ? -1 : response.size();
//        if (count < 0) {
//            return;
//        } else if (count == 0) {
//            Fragment fragment = OrderFragment.getThisFragment((FragmentActivity) adapterContext, fragmentType + "");
//            OrderFragment orderFragment = (OrderFragment) fragment;
//            orderFragment.clearListEntities();
//            orderFragment.refershContentView();
//        }
//        List<AllOrderEntity> orderEntities = new ArrayList<>();
//        for (LinkedTreeMap<String, String> item : response) {
//            AllOrderEntity entity = AllOrderEntity.mapToEntity(item);
//            orderEntities.add(entity);
//        }
//        setOrderEntities(orderEntities);
//        notifyDataSetChanged();
//    }

    //请求取消订单
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
//                        requestOrderList(userGuid, MyOrderActivity.ORDER_TYPE_BEING);
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange);
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

    //取消订单的dialog
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

    //请求再来一单
    public void requestOrderBuyAgain(String userGuid, String orderId) {
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
                    setOrderBuyAgainData(responseEntity.getResponse());
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    //再来一单,->保存数据
    public void setOrderBuyAgainData(String jsonData) {
        if (jsonData == null) {
            return;
        }
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

    //再来一单,->请求加入购物车
    public void requestToBuy(String PRICE, String GUID) {
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
                    } else {
                        Toast.makeText(adapterContext, "成功加入购物车", Toast.LENGTH_SHORT).show();
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, 1);
                    }
                } else {
                    Log.e("InsertIntoCar", errorMsg.toString() + "====" + errorMsg.msg);
                }
            }
        });
    }

    //访问确认收获
    public void requestConfirmReceive(final String userGuid, String orderId, final int groupPosition, final int childPosition) {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("yes")) {
                        Toast.makeText(adapterContext, "确认收货", Toast.LENGTH_SHORT).show();
//                        requestOrderList(userGuid, MyOrderActivity.ORDER_TYPE_BEING);
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange);
                        Intent intent = new Intent(adapterContext, OrderEvaluateActivity.class);
                        intent.putExtra("orderEntity", typeEntitiesList.get(groupPosition).get(childPosition));
                        intent.putExtra("fragmentIndex", 1);
//                        ((FragmentActivity) adapterContext).finish();
                        adapterContext.startActivity(intent);
                    } else {
                        Toast.makeText(adapterContext, "确认收货错误", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg == null) {
                } else {
                    Toast.makeText(adapterContext, "出现未知错误", Toast.LENGTH_SHORT).show();
                    Log.e("ConfirmReceive", "ERROR---->" + errorMsg.msg);
                }
                needHideProgress();
            }
        });
    }
}