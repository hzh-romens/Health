package com.romens.yjk.health.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.HeaderCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.GoodsListEntity;
import com.romens.yjk.health.model.OrderListEntity;
import com.romens.yjk.health.pay.PayMode;
import com.romens.yjk.health.pay.PayPrepareBaseActivity;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.OrderGoodsCell;
import com.romens.yjk.health.ui.cells.OrderPropertyCell;
import com.romens.yjk.health.ui.cells.OrderStoreCell;
import com.romens.yjk.health.ui.components.ToastCell;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/9/24.
 * 订单详情
 */
public class OrderDetailActivity extends DarkActionBarActivity {
    public static final String ARGUMENT_KEY_ORDER_NO = "KEY_ORDER_NO";

    private LinearLayout linearLayout;
    private ListView listView;
    private OrderDetailAdapter adapter;
    private OrderListEntity orderListEntity;
    private List<GoodsListEntity> goodsListEntities;


    private String userGuid;

    private String orderId;

    private String currOrderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userGuid = UserGuidConfig.USER_GUID;
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = actionBarEvent();
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        linearLayout = new LinearLayout(this);
//        UIHelper.setupSwipeRefreshLayoutProgress(swipeRefreshLayout);
        container.addView(linearLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                requestOrderDetailList(userGuid, orderId);
//            }
//        });

        FrameLayout listContainer = new FrameLayout(this);
        linearLayout.addView(listContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        setContentView(container);
        initData();
        adapter = new OrderDetailAdapter(this);

//        listView.setAdapter(adapter);
    }

    private ActionBar actionBarEvent() {
        ActionBar actionBar = new ActionBar(this);
        actionBar.setTitle("订单详情");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
        return actionBar;
    }


    public void initData() {
//        setRow();
        Intent intent = getIntent();
        if (intent.hasExtra(ARGUMENT_KEY_ORDER_NO)) {
            currOrderNo = intent.getStringExtra(ARGUMENT_KEY_ORDER_NO);
        } else {
            orderId = intent.getStringExtra("orderId");
        }
        requestOrderDetailList(userGuid, orderId);
    }

    private int rowCount;
    private int orderNoRow;
    private int dataRow;
    private int lineRow;

    private int shippingSectionRow;
    private int shippingNameRow;
    private int shippingPhoneRow;
    private int shippingAddressRow;
    private int line2Row;

    private int shopSectionRow;
    private int shopNameRow;
    private int goodsBeginRow;
    private int goodsEndRow;

    private int orderPaySection;
    private int totalPriceRow;
    private int couponPriceRow;
    private int payPriceRow;
    private int orderPayModeRow;


    private int payOrderActionRow;
    private int cancelOrderActionRow;

    private void setRow() {
        rowCount = 0;
        orderNoRow = rowCount++;
        dataRow = rowCount++;
        lineRow = rowCount++;

        shippingSectionRow = rowCount++;
        shippingNameRow = rowCount++;
        shippingPhoneRow = rowCount++;
        shippingAddressRow = rowCount++;
        line2Row = rowCount++;

        shopSectionRow = rowCount++;
        shopNameRow = rowCount++;
        goodsBeginRow = rowCount;
        rowCount += goodsListEntities.size();
        goodsEndRow = rowCount - 1;


        orderPaySection = rowCount++;
        totalPriceRow = rowCount++;
        couponPriceRow = rowCount++;
        payPriceRow = rowCount++;
        orderPayModeRow = rowCount++;

        if (orderListEntity != null && orderListEntity.getOrderStatusStr().equals("未付款")) {
            payOrderActionRow = rowCount++;
            cancelOrderActionRow = rowCount++;
        } else {
            payOrderActionRow = -1;
            cancelOrderActionRow = -1;
        }
    }

    class OrderDetailAdapter extends BaseAdapter {

        private Context context;
        private OrderListEntity orderListEntity;

        public void setOrderListEntity(OrderListEntity orderListEntity) {
            this.orderListEntity = orderListEntity;
            notifyDataSetChanged();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        public OrderDetailAdapter(Context context) {
            this.context = context;
            this.orderListEntity = new OrderListEntity();
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == lineRow || position == line2Row) {
                return 1;
            } else if (position == shippingSectionRow || position == shopSectionRow || position == orderPaySection) {
                return 2;
            } else if (position == shippingNameRow || position == shippingPhoneRow || position == shippingAddressRow) {
                return 0;
            } else if (position >= goodsBeginRow && position <= goodsEndRow) {
                int itemIndex = position - goodsBeginRow;
                return 3;
            } else if (position == payOrderActionRow || position == cancelOrderActionRow) {
                return 4;
            } else if (position == shopNameRow) {
                return 5;
            }
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                if (convertView == null) {
                    convertView = new OrderPropertyCell(context);
                }
                OrderPropertyCell cell = (OrderPropertyCell) convertView;
                cell.setSmall(true);
                cell.setMultilineValue(false);
                cell.setTextColor();
                cell.setValueTextColor();
                if (position == orderNoRow) {
                    cell.setTextColor(0xff212121);
                    cell.setValueTextColor(context.getResources().getColor(R.color.order_statu_color));
                    cell.setTextAndValue("订单编号:" + orderListEntity.getOrderNo(), orderListEntity.getOrderStatusStr(), true);
                } else if (position == dataRow) {
                    cell.setTextAndValue(orderListEntity.getCreateTime() + "  下单", "", true);
                } else if (position == totalPriceRow) {
                    //cell.setKeyAndValue("总计",ShoppingHelper.formatPrice(  orderListEntity.getOrderPrice(),"￥",false));
                    cell.setTextAndValue("商品金额", ShoppingHelper.formatPrice(orderListEntity.getOrderPrice(), "￥", false), false);
                } else if (position == couponPriceRow) {
                    //cell.setKeyAndValue("优惠金额",ShoppingHelper.formatPrice( orderListEntity.getCouponPrice(), "-￥", false));
                    cell.setTextAndValue("优惠金额", ShoppingHelper.formatPrice(orderListEntity.getCouponPrice(), "-￥", false), false);
                } else if (position == payPriceRow) {
                    //cell.setKeyAndValue("支付金额", ShoppingHelper.formatPrice(  orderListEntity.getPayPrice(),"￥",true));
                    cell.setTextAndValue("订单合计", ShoppingHelper.formatPrice(orderListEntity.getPayPrice(), "￥", true), true);
                } else if (position == orderPayModeRow) {
//                    String result = "";
//                    if (orderListEntity.getDeliverType().equals("1")) {
//                        result = "药店派送";
//                    } else if (orderListEntity.getDeliverType().equals("2")) {
//                        result = "到店自取";
//                    }
//                    cell.setKeyAndValue("支付方式", result, true);
                    //cell.setKeyAndValue("支付方式", orderListEntity.getPayType());

                    cell.setTextAndValue("支付方式", PayMode.getPayModeDesc(orderListEntity.getPayType()), true);
                } else if (position == shippingNameRow) {
                    //cell.setKeyAndValue("收货人姓名", orderListEntity.getReceiver());
                    cell.setTextAndValue("收货人姓名", orderListEntity.getReceiver(), true);
                } else if (position == shippingPhoneRow) {
                    //cell.setKeyAndValue("联系方式", orderListEntity.getTelephone());
                    cell.setTextAndValue("联系方式", orderListEntity.getTelephone(), true);
                } else if (position == shippingAddressRow) {
                    cell.setMultilineValue(true);
                    //cell.setKeyAndValue("地址", orderListEntity.getAddress());
                    cell.setTextAndValue("收货地址", orderListEntity.getAddress(), true);
                }
            } else if (type == 1) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(context);
                }
            } else if (type == 2) {
                if (convertView == null) {
                    convertView = new HeaderCell(context);
                }
                HeaderCell cell = (HeaderCell) convertView;
                cell.setTextColor(ResourcesConfig.bodyText3);
                if (position == shippingSectionRow) {
                    cell.setText("收货信息");
                } else if (position == shopSectionRow) {
                    cell.setText("商品信息");
                } else if (position == orderPaySection) {
                    cell.setText("合计");
                }
            } else if (type == 3) {//药品
                if (convertView == null) {
                    convertView = new OrderGoodsCell(context);
                }
                OrderGoodsCell cell = (OrderGoodsCell) convertView;
                int itemIndex = position - goodsBeginRow;
                GoodsListEntity item = goodsListEntities.get(itemIndex);
                String iconPath = item.getGoodsUrl();
                String name = item.getName();
                String desc = String.format("规格:%s", item.getSpec());
                BigDecimal userPrice = new BigDecimal(item.getGoodsPrice());
                int count = Integer.parseInt(item.getBuyCount());
                cell.setValue(iconPath, name, desc, userPrice, count, true);
            } else if (type == 4) {
                if (convertView == null) {
                    convertView = new ActionCell(context);
                }
                ActionCell cell = (ActionCell) convertView;
                if (position == payOrderActionRow) {
                    cell.setPrimaryAction();
                    cell.setValue("支付此订单");
                } else if (position == cancelOrderActionRow) {
                    cell.setNormalAction();
                    cell.setValue("取消此订单");
                }
            } else if (type == 5) {//药店

                if (convertView == null) {
                    convertView = new OrderStoreCell(context);
                }
                OrderStoreCell cell = (OrderStoreCell) convertView;
                GoodsListEntity item = goodsListEntities.get(0);
                cell.setValue(item.getShopName(), true);
            }
            return convertView;
        }
    }

    //取消订单的dialog
    public void showCancelDialog(final String userGuid, final String orderId) {
        new AlertDialog.Builder(this).setTitle("确定删除订单吗？")
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

    //请求取消订单
    private void requestCancelOrderList(final String userGuid, String orderId) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("ORDERID", orderId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "CancelOrder", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol).build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(OrderDetailActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(OrderDetailActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange);
                        finish();
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "取消失败", Toast.LENGTH_SHORT).show();
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

    private void toPay(OrderListEntity entity) {
        //发起支付
        String orderNo = entity.getOrderNo();
        String orderDate = entity.getCreateTime();
        String payType = entity.getPayType();
        BigDecimal payAmount = entity.getOrderPrice();

        Bundle arguments = new Bundle();
        arguments.putString(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_NO, orderNo);
        arguments.putString(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_DATE, orderDate);
        arguments.putDouble(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_AMOUNT, payAmount.doubleValue());
        arguments.putDouble(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_PAY_AMOUNT, payAmount.doubleValue());
        boolean isOpen = UIOpenHelper.openPayPrepareActivity(OrderDetailActivity.this, payType, arguments);
        if (isOpen) {
            finish();
        }
    }

    private void setListViewHeight(ExpandableListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void requestOrderDetailList(String userGuid, String orderId) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        if (!TextUtils.isEmpty(currOrderNo)) {
            args.put("ORDERNO", currOrderNo);
        } else {
            args.put("ORDERID", orderId);
        }

        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "getMyOrderDetail", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(OrderDetailActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol<JsonNode>) message.protocol;
                            setOrderData(responseProtocol.getResponse());
//                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            ToastCell.toast(OrderDetailActivity.this, "查询订单失败!");
//                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    @Override
    public void onDestroy() {
        ConnectManager.getInstance().destroyInitiator(OrderDetailActivity.class);
        super.onDestroy();
    }

    public void setOrderData(JsonNode response) {
        if (response == null) {
            return;
        }
        goodsListEntities = new ArrayList<>();
        orderListEntity = new OrderListEntity();
        orderListEntity.setOrderId(response.get("ORDER_ID").asText());
        orderListEntity.setOrderNo(response.get("ORDERNO").asText());
        orderListEntity.setCreateTime(response.get("CREATETIME").asText());
        orderListEntity.setOrderPrice(response.get("ORDERPRICE").asDouble());
        orderListEntity.setCouponPrice(response.get("COUPONPRICE").asDouble());
        orderListEntity.setPayPrice(response.get("PAYPRICE").asDouble());
        orderListEntity.setReceiver(response.get("RECEIVER").asText());
        orderListEntity.setAddress(response.get("ADDRESS").asText());
        orderListEntity.setDeliverType(response.get("DELIVERYTYPE").asText());
        orderListEntity.setOrderStatus(response.get("orderStatus").asText());
        orderListEntity.setOrderStatusStr(response.get("ORDERSTATUSSTR").asText());
        orderListEntity.setTelephone(response.get("TELEPHONE").asText());
        orderListEntity.setPayType(response.get("PAYTYPE").asText());
        JsonNode array = response.get("GOODSLIST");
        for (int i = 0; i < array.size(); i++) {
            JsonNode subObjcet = array.get(i);
            GoodsListEntity goodsEntity = new GoodsListEntity();
            goodsEntity.setGoodsGuid(subObjcet.get("GOODSGUID").asText());
            goodsEntity.setBuyCount(subObjcet.get("BUYCOUNT").asText());
            goodsEntity.setGoodsPrice(subObjcet.get("GOODSPRICE").asText());
            goodsEntity.setName(subObjcet.get("NAME").asText());
            goodsEntity.setCode(subObjcet.get("CODE").asText());
            goodsEntity.setGoodsUrl(subObjcet.get("GOODURL").asText());
            //GOODSBIGURL
            goodsEntity.setDetailDescitption(subObjcet.get("DETAILDESCRIPTION").asText());
            goodsEntity.setSpec(subObjcet.get("SPEC").asText());
            goodsEntity.setGoodsSortGuid(subObjcet.get("GOODSSORTGUID").asText());
            goodsEntity.setShopId(subObjcet.get("SHOPID").asText());
            goodsEntity.setShopName(subObjcet.get("SHOPNAME").asText());
            goodsListEntities.add(goodsEntity);
        }
        setRow();

        adapter.setOrderListEntity(orderListEntity);
        listView.setAdapter(adapter);
    }

    public LinearLayout getBtnLayout() {
        LinearLayout layout = new LinearLayout(this) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56), MeasureSpec.EXACTLY));
            }
        };

        TextView confirmBtn = new TextView(this);
        confirmBtn.setBackgroundResource(R.drawable.btn_primary);
        confirmBtn.setTextColor(0xffffffff);
        confirmBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        confirmBtn.setLines(1);
        confirmBtn.setText("去支付");
        confirmBtn.setMaxLines(1);
        confirmBtn.setSingleLine(true);
        confirmBtn.setEllipsize(TextUtils.TruncateAt.END);
        confirmBtn.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams confirmParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 40, 16, 8, 4, 8);
        confirmParams.weight = 1;
        layout.addView(confirmBtn, confirmParams);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPay(orderListEntity);
            }
        });

        TextView cancelBtn = new TextView(this);
        cancelBtn.setBackgroundResource(R.drawable.order_cancel_btn_bg);
        cancelBtn.setTextColor(0xff121212);
        cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        cancelBtn.setLines(1);
        cancelBtn.setText("取消订单");
        cancelBtn.setMaxLines(1);
        cancelBtn.setSingleLine(true);
        cancelBtn.setEllipsize(TextUtils.TruncateAt.END);
        cancelBtn.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams cancelParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 40, 4, 8, 16, 8);
        cancelParams.weight = 1;
        layout.addView(cancelBtn, cancelParams);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCancelOrderList(userGuid, orderId);
            }
        });
        return layout;
    }
}
