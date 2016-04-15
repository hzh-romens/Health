package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import com.romens.android.ui.cells.EmptyCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.common.GoodsFlag;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.GoodsListEntity;
import com.romens.yjk.health.model.OrderDetailEntity;
import com.romens.yjk.health.pay.Pay;
import com.romens.yjk.health.pay.PayPrepareBaseActivity;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.H3HeaderCell;
import com.romens.yjk.health.ui.cells.OrderGoodsCell;
import com.romens.yjk.health.ui.cells.OrderPayAmountCell;
import com.romens.yjk.health.ui.cells.OrderPropertyCell;
import com.romens.yjk.health.ui.cells.OrderStoreCell;
import com.romens.yjk.health.ui.cells.TipCell;
import com.romens.yjk.health.ui.components.ToastCell;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 2016-04-13 10:26
 * @description
 */
public class OrderDetailFragment extends OrderBaseFragment implements AppNotificationCenter.NotificationCenterDelegate {
    public static final String ARGUMENT_KEY_ORDER_NO = "KEY_ORDER_NO";

    private ProgressBar progressBar;
    private ListView listView;
    private OrderDetailAdapter adapter;
    private OrderDetailEntity orderDetailEntity;


    private String userGuid;

    private String orderId;

    private String currOrderNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onOrderStateChange);
        userGuid = UserSession.getInstance().getUser();
        Bundle arguments = getArguments();
        if (arguments.containsKey(ARGUMENT_KEY_ORDER_NO)) {
            currOrderNo = arguments.getString(ARGUMENT_KEY_ORDER_NO);
        } else {
            orderId = arguments.getString("orderId");
        }
        adapter = new OrderDetailAdapter(getActivity());
    }

    @Override
    public void onDestroy() {
        ConnectManager.getInstance().destroyInitiator(OrderDetailFragment.class);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onOrderStateChange);
        super.onDestroy();
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout content = new FrameLayout(context);
        listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        content.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        progressBar = new ProgressBar(context);
        content.addView(progressBar, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == payOrderActionRow) {
                    toPay(orderDetailEntity);
                } else if (position == cancelOrderActionRow) {
                    tryCancelOrder(orderDetailEntity.orderId);
                } else if (position == completeOrderActionRow) {
                    tryCompleteOrder(orderDetailEntity.orderNo, orderDetailEntity.orderId);
                } else if (position == buyOrderActionRow) {
                    putOrderGoodsToShoppingCart(orderDetailEntity.orderNo);
                } else if (position == retryOrderActionRow) {
                    putOrderGoodsToShoppingCart(orderDetailEntity.orderNo);
                } else if (position == commitOrderActionRow) {
                    doCommitOrder(orderDetailEntity.orderId);
                } else if (position >= goodsBeginRow && position <= goodsEndRow) {
                    int itemIndex = position - goodsBeginRow;
                    GoodsListEntity item = orderDetailEntity.goodsListEntities.get(itemIndex);
                    String goodsGuid = item.getGoodsGuid();
                    int goodsType = item.getGoodsType();
                    UIOpenHelper.openMedicineActivity(getActivity(), goodsGuid, goodsType);
                } else if (position == cancelOrderTipRow) {
                    UIOpenHelper.openHelperActivity(getActivity());
                }
            }
        });
        return content;
    }

    private void showProgress(boolean progress) {
        progressBar.setVisibility(progress ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        listView.setAdapter(adapter);
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        requestOrderDetailList(userGuid, orderId);
    }

    private int rowCount;
    private int orderNoRow;
    private int dataRow;
    private int lineRow;

    private int shippingTypeRow;
    private int shippingNameRow;
    private int shippingPhoneRow;
    private int shippingAddressRow;
    private int line2Row;

    private int shopSectionRow;
    private int shopNameRow;
    private int goodsBeginRow;
    private int goodsEndRow;

    private int orderPaySection;
    private int orderPayModeRow;
    private int goodsAmountRow;
    private int shippingAmountRow;
    private int couponAmountRow;
    private int payAmountRow;

    private int payResultSection;
    private int payModeRow;
    private int payResultBeingRow;
    private int payResultEndRow;

    private int payOrderActionRow;
    private int cancelOrderActionRow;
    private int completeOrderActionRow;
    private int commitOrderActionRow;
    private int retryOrderActionRow;
    private int buyOrderActionRow;


    private int cancelOrderTipRow;

    private int bottomEmptyRow;

    private void updateAdapter() {
        rowCount = 0;
        orderNoRow = rowCount++;
        dataRow = rowCount++;
        lineRow = rowCount++;

        shippingTypeRow = rowCount++;
        shippingNameRow = rowCount++;
        shippingPhoneRow = rowCount++;
        shippingAddressRow = rowCount++;
        line2Row = rowCount++;

        shopSectionRow = rowCount++;
        shopNameRow = rowCount++;
        goodsBeginRow = rowCount;
        rowCount += orderDetailEntity.goodsListEntities.size();
        goodsEndRow = rowCount - 1;


        orderPaySection = rowCount++;
        orderPayModeRow = rowCount++;
        goodsAmountRow = rowCount++;
        shippingAmountRow = rowCount++;
        couponAmountRow = rowCount++;
        payAmountRow = rowCount++;

        if (orderDetailEntity.payResult.size() > 0) {
            payResultSection = rowCount++;
            payModeRow = rowCount++;
            payResultBeingRow = rowCount;
            rowCount += orderDetailEntity.payResult.size();
            payResultEndRow = rowCount - 1;

        } else {
            payResultSection = -1;
            payModeRow = -1;
            payResultBeingRow = -1;
            payResultEndRow = -1;
        }

        String orderStatus = orderDetailEntity.orderStatusStr;
        if (TextUtils.equals("未付款", orderStatus)) {
            payOrderActionRow = rowCount++;
            cancelOrderActionRow = rowCount++;
            completeOrderActionRow = -1;
            commitOrderActionRow = -1;
            retryOrderActionRow = -1;
            buyOrderActionRow = -1;
        } else if (TextUtils.equals("交易取消", orderStatus)||TextUtils.equals("交易关闭", orderStatus)) {
            payOrderActionRow = -1;
            cancelOrderActionRow = -1;
            completeOrderActionRow = -1;
            commitOrderActionRow = -1;
            retryOrderActionRow = rowCount++;
            buyOrderActionRow = -1;
        } else if (TextUtils.equals("交易完成", orderStatus)) {
            payOrderActionRow = -1;
            cancelOrderActionRow = -1;
            completeOrderActionRow = -1;
            commitOrderActionRow = rowCount++;
            retryOrderActionRow = -1;
            buyOrderActionRow = rowCount++;
        } else if (TextUtils.equals("已评价", orderStatus)) {
            payOrderActionRow = -1;
            cancelOrderActionRow = -1;
            completeOrderActionRow = -1;
            commitOrderActionRow = -1;
            retryOrderActionRow = -1;
            buyOrderActionRow = -1;
        } else {
            completeOrderActionRow = rowCount++;
            payOrderActionRow = -1;
            cancelOrderActionRow = -1;
            commitOrderActionRow = -1;
            retryOrderActionRow = -1;
            buyOrderActionRow = -1;
        }
        cancelOrderTipRow = ShoppingHelper.needCancelAlert(orderStatus) ? rowCount++ : -1;

        bottomEmptyRow = rowCount++;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.onOrderStateChange) {
            if (objects != null && objects.length > 0) {
                String orderId = objects[0].toString();
                if (TextUtils.equals(orderId, orderDetailEntity.orderId)) {
                    requestOrderDetailList(userGuid, orderDetailEntity.orderId);
                }
            }
        }
    }

    class OrderDetailAdapter extends BaseAdapter {

        private Context context;


        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            if (position == payOrderActionRow
                    || position == cancelOrderActionRow
                    || position == completeOrderActionRow
                    || position == commitOrderActionRow
                    || position == retryOrderActionRow
                    || position == buyOrderActionRow) {
                return true;
            } else if (position >= goodsBeginRow && position <= goodsEndRow) {
                return true;
            } else if (position == cancelOrderTipRow) {
                return true;
            }
            return false;
        }

        public OrderDetailAdapter(Context context) {
            this.context = context;
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
            } else if (position == shopSectionRow || position == orderPaySection || position == payResultSection) {
                return 2;
            } else if (position >= goodsBeginRow && position <= goodsEndRow) {
                return 3;
            } else if (position == payOrderActionRow
                    || position == cancelOrderActionRow
                    || position == completeOrderActionRow
                    || position == commitOrderActionRow
                    || position == retryOrderActionRow
                    || position == buyOrderActionRow) {
                return 4;
            } else if (position == shopNameRow) {
                return 5;
            } else if (position == bottomEmptyRow) {
                return 6;
            } else if (position == payAmountRow) {
                return 7;
            } else if (position == cancelOrderTipRow) {
                return 8;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 9;
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
                    int color = ShoppingHelper.getOrderStatusColor(orderDetailEntity.orderStatusStr);
                    cell.setValueTextColor(color);
                    cell.setTextAndValue("订单编号:" + orderDetailEntity.orderNo, orderDetailEntity.orderStatusStr, true);
                } else if (position == dataRow) {
                    cell.setTextAndValue(orderDetailEntity.createTime + "  下单", "", true);
                } else if (position == goodsAmountRow) {
                    //cell.setKeyAndValue("总计",ShoppingHelper.formatPrice(  orderListEntity.getOrderPrice(),"￥",false));
                    cell.setTextAndValue("商品金额", ShoppingHelper.formatPrice(orderDetailEntity.orderPrice, "￥", false), false);
                } else if (position == shippingAmountRow) {
                    cell.setTextAndValue("配送费", ShoppingHelper.formatPrice(orderDetailEntity.shippingAmount, "+￥", false), false);
                } else if (position == couponAmountRow) {
                    //cell.setKeyAndValue("优惠金额",ShoppingHelper.formatPrice( orderListEntity.getCouponPrice(), "-￥", false));
                    cell.setTextAndValue("优惠金额", ShoppingHelper.formatPrice(orderDetailEntity.couponPrice, "-￥", false), true);
                } else if (position == orderPayModeRow) {
//                    String result = "";
//                    if (orderListEntity.getDeliverType().equals("1")) {
//                        result = "药店派送";
//                    } else if (orderListEntity.getDeliverType().equals("2")) {
//                        result = "到店自取";
//                    }
//                    cell.setKeyAndValue("支付方式", result, true);
                    //cell.setKeyAndValue("支付方式", orderListEntity.getPayType());

                    cell.setTextAndValue("支付方式", Pay.getInstance().getPayType(orderDetailEntity.payType), true);
                } else if (position == shippingNameRow) {
                    //cell.setKeyAndValue("收货人姓名", orderListEntity.getReceiver());
                    cell.setTextAndValue("收货人姓名", orderDetailEntity.receiver, true);
                } else if (position == shippingPhoneRow) {
                    //cell.setKeyAndValue("联系方式", orderListEntity.getTelephone());
                    cell.setTextAndValue("联系方式", orderDetailEntity.telephone, true);
                } else if (position == shippingAddressRow) {
                    cell.setMultilineValue(true);
                    //cell.setKeyAndValue("地址", orderListEntity.getAddress());
                    cell.setTextAndValue("收货地址", orderDetailEntity.address, true);
                } else if (position == shippingTypeRow) {
                    cell.setTextAndValue("配送方式", orderDetailEntity.deliverType, true);
                } else if (position == payModeRow) {
                    cell.setTextAndValue("付款方式", orderDetailEntity.getPayModeDesc(), true);
                } else if (position >= payResultBeingRow && position <= payResultEndRow) {
                    cell.setMultilineValue(true);
                    int index = position - payResultBeingRow;
                    Pair<CharSequence, CharSequence> payResultItem = orderDetailEntity.payResult.get(index);
                    cell.setTextAndValue(payResultItem.first, payResultItem.second, true);
                }
            } else if (type == 1) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(context);
                }
            } else if (type == 2) {
                if (convertView == null) {
                    convertView = new H3HeaderCell(context);
                }
                H3HeaderCell cell = (H3HeaderCell) convertView;
                cell.setTextColor(ResourcesConfig.primaryColor);
                if (position == shopSectionRow) {
                    cell.setText("商品信息");
                } else if (position == orderPaySection) {
                    cell.setText("订单合计");
                } else if (position == payResultSection) {
                    cell.setText("付款信息");
                }
            } else if (type == 3) {//药品
                if (convertView == null) {
                    convertView = new OrderGoodsCell(context);
                }
                OrderGoodsCell cell = (OrderGoodsCell) convertView;
                int itemIndex = position - goodsBeginRow;
                GoodsListEntity item = orderDetailEntity.goodsListEntities.get(itemIndex);
                String iconPath = item.getGoodsUrl();
                CharSequence name = ShoppingHelper.createShoppingCartGoodsName(item.getName(), item.getGoodsType() == GoodsFlag.MEDICARE);
                String desc = String.format("规格:%s", item.getSpec());
                BigDecimal userPrice = new BigDecimal(item.getGoodsPrice());
                int count = Integer.parseInt(item.getBuyCount());
                cell.setValue(iconPath, name, desc, userPrice, false, count, true);
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
                } else if (position == completeOrderActionRow) {
                    cell.setPrimaryAction();
                    cell.setValue("确认收货");
                } else if (position == retryOrderActionRow) {
                    cell.setNormalAction();
                    cell.setValue("重新购买");
                } else if (position == buyOrderActionRow) {
                    cell.setNormalAction();
                    cell.setValue("再来一单");
                } else if (position == commitOrderActionRow) {
                    cell.setNormalAction();
                    cell.setValue("评价此订单");
                }
            } else if (type == 5) {//药店

                if (convertView == null) {
                    convertView = new OrderStoreCell(context);
                }
                OrderStoreCell cell = (OrderStoreCell) convertView;
                GoodsListEntity item = orderDetailEntity.goodsListEntities.get(0);
                cell.setValue(item.getShopName(), true);
            } else if (type == 6) {
                if (convertView == null) {
                    convertView = new EmptyCell(context);
                }
                EmptyCell cell = (EmptyCell) convertView;
                cell.setHeight(AndroidUtilities.dp(32));
            } else if (type == 7) {

                if (convertView == null) {
                    convertView = new OrderPayAmountCell(context);
                }
                OrderPayAmountCell cell = (OrderPayAmountCell) convertView;
                if (position == payAmountRow) {
                    //cell.setKeyAndValue("支付金额", ShoppingHelper.formatPrice(  orderListEntity.getPayPrice(),"￥",true));
                    cell.setTextAndValue("实付款", ShoppingHelper.formatPrice(orderDetailEntity.payPrice, "￥", true), true);
                }
            } else if (type == 8) {
                if (convertView == null) {
                    convertView = new TipCell(context);
                }
                TipCell cell = (TipCell) convertView;
                if (position == cancelOrderTipRow) {
                    cell.setValue(getString(R.string.order_cancel_tip_for_payed));
                }
            }
            return convertView;
        }
    }

    private void toPay(OrderDetailEntity entity) {
        Bundle arguments = new Bundle();
        arguments.putString(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_NO, entity.orderNo);
        arguments.putString(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_DATE, entity.createTime);
        arguments.putDouble(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_AMOUNT, entity.payPrice.doubleValue());
        arguments.putDouble(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_PAY_AMOUNT, entity.payPrice.doubleValue());
        boolean isOpen = UIOpenHelper.openPayPrepareActivity(getActivity(), entity.payType, arguments);
        if (isOpen) {
            getActivity().finish();
        }
    }

    private void requestOrderDetailList(String userGuid, String orderId) {
        showProgress(true);
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        if (!TextUtils.isEmpty(currOrderNo)) {
            args.put("ORDERNO", currOrderNo);
        } else {
            args.put("ORDERID", orderId);
        }

        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "getMyOrderDetail", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(OrderDetailFragment.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        showProgress(false);
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol<JsonNode>) message.protocol;
                            orderDetailEntity = new OrderDetailEntity(responseProtocol.getResponse());
                            updateAdapter();
//                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            ToastCell.toast(getActivity(), "查询订单失败!");
//                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(getActivity(), connect);
    }
}

