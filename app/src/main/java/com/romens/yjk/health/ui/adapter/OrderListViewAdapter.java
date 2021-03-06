package com.romens.yjk.health.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.ui.OrderEvaluateActivity;
import com.romens.yjk.health.ui.cells.OrderCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2016/3/24.
 */
public class OrderListViewAdapter extends RecyclerView.Adapter {

    private final List<OrderEntity> orderEntities=new ArrayList<>();
    protected Context adapterContext;

    public interface Delegate extends OrderCell.Delegate{
        void onItemSelect(OrderEntity orderEntity);
    }

    private Delegate adapterDelegate;

    public OrderListViewAdapter(Context adapterContext,Delegate delegate) {
        this.adapterContext = adapterContext;
        this.adapterDelegate=delegate;
    }

    public void bindData(List<OrderEntity> entities) {
        orderEntities.clear();
        if(entities!=null&&entities.size()>0){
            orderEntities.addAll(entities);
        }
        notifyDataSetChanged();
    }

    public OrderEntity getItem(int position) {
        return orderEntities.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OrderCell cell=new OrderCell(parent.getContext());
        cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,LayoutHelper.WRAP_CONTENT));
        return new Holder(cell);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        OrderCell cell=(OrderCell)holder.itemView;
        OrderEntity orderEntity=getItem(position);
        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapterDelegate!=null){
                    adapterDelegate.onItemSelect(getItem(position));
                }
            }
        });
        cell.setValue(orderEntity,adapterDelegate);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return orderEntities.size();
    }

    static class Holder extends RecyclerView.ViewHolder{

        public Holder(View itemView) {
            super(itemView);
        }
    }

//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView =new OrderCell(adapterContext);
//        }
//
//
//
////        OrderTitleCell groupView = (OrderTitleCell) convertView.findViewById(R.id.title_view);
////        groupView.setInfor(orderEntity.orderNo, orderEntity.orderStatusStr, position == 0 ? true : false);
////
////        TextView titleTextView = (TextView) convertView.findViewById(R.id.order_title);
////        TextView moneyTextView = (TextView) convertView.findViewById(R.id.order_money);
////        TextView specTextView = (TextView) convertView.findViewById(R.id.order_date);
////        BackupImageView medicineImg = (BackupImageView) convertView.findViewById(R.id.order_img);
////
////        titleTextView.setText(entity.getGoodsName());
////        moneyTextView.setText("￥" + entity.getOrderPrice());
////        specTextView.setText(entity.getCreateDate());
////        if (entity.getPicSmall() != null) {
////            medicineImg.setImageUrl(entity.getPicSmall(), null, null);
////        } else {
////            medicineImg.setImageResource(R.drawable.no_img_upload);
////        }
////
////        convertView.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Intent intent = new Intent(adapterContext, OrderDetailActivity.class);
////                intent.putExtra("orderId", getItem(position).orderId);
////                adapterContext.startActivity(intent);
////                notifyDataSetChanged();
////            }
////        });
////        TextView buyAgainBtn = (TextView) convertView.findViewById(R.id.order_all_buy_again);
////        TextView evaluateBtn = (TextView) convertView.findViewById(R.id.order_all_evaluate_btn);
////        TextView cancelBtn = (TextView) convertView.findViewById(R.id.order_all_buy_cancel);
////        if (TextUtils.equals("未付款",orderEntity.orderStatusStr)) {
////            evaluateBtn.setText("取消订单");
////            cancelBtn.setVisibility(View.GONE);
////            evaluateBtn.setVisibility(View.VISIBLE);
////            buyAgainBtn.setVisibility(View.GONE);
//////            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cancelBtn.getLayoutParams();
//////            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//////            layoutParams.rightMargin = AndroidUtilities.dp(16);
//////            cancelBtn.setLayoutParams(layoutParams);
////            evaluateBtn.setBackgroundResource(R.drawable.order_cancel_btn_bg);
////            evaluateBtn.setTextColor(adapterContext.getResources().getColor(R.color.theme_sub_title));
////            evaluateBtn.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    showCancelDialog(userGuid, entity.getOrderId());
////                }
////            });
////        } else if (TextUtils.equals("交易完成",orderEntity.orderStatusStr)) {
////            buyAgainBtn.setText("再来一单");
////            evaluateBtn.setText(" 评    价 ");
////            cancelBtn.setVisibility(View.GONE);
////            evaluateBtn.setVisibility(View.VISIBLE);
////            buyAgainBtn.setVisibility(View.VISIBLE);
////            evaluateBtn.setBackgroundResource(R.drawable.order_confirm_btn_bg);
////            evaluateBtn.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    Intent intent = new Intent(adapterContext, OrderEvaluateActivity.class);
////                    intent.putExtra("fragmentIndex", 2);
////                    intent.putExtra("orderEntity", typeEntitiesList.get(position));
////                    adapterContext.startActivity(intent);
////                }
////            });
////            buyAgainBtn.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    requestOrderBuyAgain(UserGuidConfig.USER_GUID, entity.getOrderId());
////                }
////            });
////        } else if (TextUtils.equals("已评价",orderEntity.orderStatusStr)) {
////            evaluateBtn.setText("查看评价");
////            cancelBtn.setVisibility(View.GONE);
////            evaluateBtn.setVisibility(View.VISIBLE);
////            buyAgainBtn.setVisibility(View.GONE);
////            evaluateBtn.setBackgroundResource(R.drawable.order_confirm_btn_bg);
////            evaluateBtn.setTextColor(adapterContext.getResources().getColor(R.color.order_btn_bg));
////            evaluateBtn.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    Intent intent = new Intent(adapterContext, OrderEvaluateDetailActivity.class);
////                    intent.putExtra("evaluateDetailEntity", entity);
////                    adapterContext.startActivity(intent);
////                }
////            });
////        } else if (TextUtils.equals("交易取消",orderEntity.orderStatusStr)) {
////            evaluateBtn.setText("重新购买");
////            evaluateBtn.setVisibility(View.VISIBLE);
////            cancelBtn.setVisibility(View.GONE);
////            buyAgainBtn.setVisibility(View.GONE);
////            evaluateBtn.setBackgroundResource(R.drawable.order_confirm_btn_bg);
////            evaluateBtn.setTextColor(adapterContext.getResources().getColor(R.color.order_btn_bg));
////            evaluateBtn.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    needShowProgress("正在处理...");
////                    requestOrderBuyAgain(UserGuidConfig.USER_GUID, entity.getOrderId());
////                }
////            });
////        } else {
////            cancelBtn.setText("取消订单");
////            evaluateBtn.setText("确认收货");
////            evaluateBtn.setVisibility(View.VISIBLE);
////            cancelBtn.setVisibility(View.VISIBLE);
////            buyAgainBtn.setVisibility(View.GONE);
////            evaluateBtn.setBackgroundResource(R.drawable.order_confirm_btn_bg);
////            evaluateBtn.setTextColor(adapterContext.getResources().getColor(R.color.order_btn_bg));
////            evaluateBtn.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    needShowProgress("正在处理...");
////                    requestConfirmReceive(userGuid, entity.getOrderId(), position);
////                }
////            });
////            cancelBtn.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    showCancelDialog(userGuid, entity.getOrderId());
////                }
////            });
////        }
//        return convertView;
//    }

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
//            orderFragment.refreshContentView();
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
        //needShowProgress("正在处理...");
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
                //needHideProgress();
                Toast.makeText(adapterContext, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                //needHideProgress();
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

            }
        });
    }

    //取消订单的dialog
    public void showCancelDialog(final String userGuid, final String orderId) {
        new AlertDialog.Builder(adapterContext).setTitle("确定取消订单吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        //换成新的，参照OrderDetailActivity 410行
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(adapterContext, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                //needHideProgress();
                Toast.makeText(adapterContext, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                //needHideProgress();
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
    public void requestConfirmReceive(final String userGuid, String orderId, final int position) {
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
                //needHideProgress();
                Toast.makeText(adapterContext, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                //needHideProgress();
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
                        //intent.putExtra("orderEntity", typeEntitiesList.get(position));
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
            }
        });
    }
}
