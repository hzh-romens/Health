package com.romens.yjk.health.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.fasterxml.jackson.databind.JsonNode;
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
import com.romens.android.ui.cells.DividerCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.H3HeaderCell;
import com.romens.yjk.health.ui.cells.PayInfoCell;
import com.romens.yjk.health.ui.cells.PayModeCell;
import com.romens.yjk.health.ui.cells.TipCell;
import com.romens.yjk.health.ui.components.ToastCell;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public abstract class PayPrepareBaseActivity extends DarkActionBarActivity {
    public static final String ARGUMENTS_KEY_FROM_ORDER_DETAIL = "key_from_order_detail";
    public static final String ARGUMENTS_KEY_ORDER_NO = "key_order_no";
    public static final String ARGUMENTS_KEY_ORDER_DATE = "key_order_date";
    public static final String ARGUMENTS_KEY_ORDER_AMOUNT = "key_order_amount";
    public static final String ARGUMENTS_KEY_ORDER_PAY_AMOUNT = "key_order_pay_amount";

    protected ListView listView;
    protected BaseAdapter listAdapter;

    //是否来自订单详情
    protected boolean isFromOrderDetail = false;
    //订单编号
    protected String orderNo;
    protected String orderDate;
    protected BigDecimal orderAmount;
    //订单待支付金额
    protected BigDecimal orderPayAmount;

    protected int selectedPayModeId;
    protected final SparseArray<PayMode> payModes = new SparseArray<>();
    protected final SparseArray<PayMode> otherPayModes = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        isFromOrderDetail = bundle.getBoolean(ARGUMENTS_KEY_FROM_ORDER_DETAIL, false);
        orderNo = bundle.getString(ARGUMENTS_KEY_ORDER_NO);
        orderDate = bundle.getString(ARGUMENTS_KEY_ORDER_DATE);
        double amount = bundle.getDouble(ARGUMENTS_KEY_ORDER_AMOUNT, 0);
        orderAmount = new BigDecimal(amount);
        amount = bundle.getDouble(ARGUMENTS_KEY_ORDER_PAY_AMOUNT, 0);
        orderPayAmount = new BigDecimal(amount);

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    needFinish();
                }
            }
        });
        actionBar.setBackButtonImage(R.drawable.ic_close_white_24dp);
        FrameLayout listContainer = new FrameLayout(this);
        content.addView(listContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(this);
        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setSelector(R.drawable.list_selector);

        listAdapter = onCreateAdapter();
        listView.setAdapter(listAdapter);
    }

    protected void onCreatePayMode(SparseArray<PayMode> payModes) {
        payModes.put(0, new PayMode.Builder(0)
                .withIconResId(R.drawable.ic_appwx_logo)
                .withName("微信支付(现金支付)")
                .withDesc("推荐微信用户使用")
                .withMode(PayModeEnum.WX).build());

        payModes.put(1, new PayMode.Builder(1)
                .withIconResId(R.drawable.ic_pay_alipay)
                .withName("支付宝支付(现金支付)")
                .withDesc("推荐支付宝用户使用")
                .withMode(PayModeEnum.ALIPAY).build());
        selectedPayModeId = 0;
    }

    protected void onCreateOtherPayMode(SparseArray<PayMode> payModes) {

    }

    protected void sendPayPrepareRequest() {
        Map<String, String> args = new HashMap<>();
        args.put("APPTYPE", "ANDROID");
        args.put("ORDERCODE", orderNo);
        String payMode = payModes.get(selectedPayModeId).getPayModeKey();
        args.put("PAYMODE", payMode);
        doPayPrepareRequest(args);
    }

    @Override
    public void onBackPressed() {
        needFinish();
    }


    protected abstract void needFinish();

    protected BaseAdapter onCreateAdapter() {
        return new ListAdapter(this);
    }

    protected void doPayPrepareRequest(Map<String, String> args) {
        needShowProgress("正在请求支付,请稍候...");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetPaymentParameter", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(PayPrepareBaseActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        needHideProgress();
                        String error;
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (response.has("ERROR")) {
                                error = response.get("ERROR").asText();
                            } else {
                                formatPayParamsResponse(response);
                                return;
                            }
                        } else {
                            error = "请求支付失败,请稍后重试!";
                        }
                        ToastCell.toast(PayPrepareBaseActivity.this, error);
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    protected void formatPayParamsResponse(JsonNode jsonNode) {
        //{"PAYMODE":"PAY_ALIPAY",
        // "PAYPARAMS":{
        // "partner":"2088701740074813",
        // "seller_id":"2088701740074813",
        // "out_trade_no":"2048131457006054",
        // "subject":"测试","total_fee":"0.01",
        // "notify_url":"http://115.28.244.190/index.php/Alipay",
        // "service":"mobile.securitypay.pay",
        // "payment_type":"1","_input_charset":"utf-8","it_b_pay":"30m","sign":"yCu9SAXLndsM+UgIsC3BnTPeHW85shxz8G9BLCrMNfjFk4cKI9kf/y3KPI8ebf7yBGQDenhArXOvdjq6EOxMpAKKOxqqZBmm/g3/0dQNnZJWcrIentUE6LMmWkiOr+Uz6TC9bZDS0aGCFES3VYt0NvQGfnw8rMra4QITX2Q1CMk=","signText":"_input_charset=utf-8&it_b_pay=30m&notify_url=http://115.28.244.190/index.php/Alipay&out_trade_no=2048131457006054&partner=2088701740074813&payment_type=1&seller_id=2088701740074813&service=mobile.securitypay.pay&subject=测试&total_fee=0.01","sign_type":"RSA"}}
        final String payMode = jsonNode.get("PAYMODE").asText();
        JsonNode payParamsNode = jsonNode.get("PAYPARAMS");
        //2016-04-09 zhoulisi 支付金额和订单总金额总服务器获取返回
        double payAmount = jsonNode.get("PAYPRICE").asDouble();
        double orderTransportAmount = jsonNode.get("TRANSPORTAMOUNT").asDouble();
        double orderAmount = jsonNode.get("ORDERPRICE").asDouble();
        Bundle extBundle = new Bundle();
        extBundle.putString("ORDER_NO", orderNo);
        Bundle payParams = Pay.getInstance().createPayParams(PayPrepareBaseActivity.this, payMode, payParamsNode, extBundle);
        Intent intent = Pay.getInstance().createPayComponentName(PayPrepareBaseActivity.this, payMode);
        Bundle bundle = new Bundle();
        bundle.putBoolean(PayActivity.ARGUMENTS_KEY_FROM_PAY_PREPARE, true);

        Bundle pay = new Bundle();
        pay.putString(PayActivity.ARGUMENT_KEY_ORDER_NO, orderNo);
        pay.putString(PayActivity.ARGUMENT_KEY_ORDER_TIME, orderDate);
        pay.putDouble(PayActivity.ARGUMENT_KEY_ORDER_AMOUNT, orderAmount);
        pay.putDouble(PayActivity.ARGUMENT_KEY_ORDER_TRANSPORT_AMOUNT, orderTransportAmount);
        pay.putDouble(PayActivity.ARGUMENT_KEY_ORDER_PAY_AMOUNT, payAmount);
        pay.putBundle("PAY", payParams);

        bundle.putBundle(PayActivity.ARGUMENTS_KEY_PAY_PARAMS, pay);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    protected boolean hasTip() {
        return false;
    }

    protected CharSequence getTip() {
        return "";
    }

    protected void updateAdapter() {
        rowCount = 0;
        if (hasTip()) {
            tipRow = rowCount++;
        } else {
            tipRow = -1;
        }
        billSection = rowCount++;
        billNoRow = rowCount++;
        orderDateRow = rowCount++;
        orderAmountRow = rowCount++;
        payAmountRow = rowCount++;

        dividerRow = rowCount++;
        payModeSection = rowCount++;
        payModeStartRow = rowCount;
        rowCount += payModes.size();
        payModeEndRow = rowCount - 1;

        if (otherPayModes.size() > 0) {
            otherPayModeSection = rowCount++;
            otherPayModeStartRow = rowCount;
            rowCount += otherPayModes.size();
            otherPayModeEndRow = rowCount - 1;
        } else {
            otherPayModeSection = -1;
            otherPayModeStartRow = -1;
            otherPayModeEndRow = -1;
        }

        payModeDividerRow = rowCount++;
        payActionRow = rowCount++;

        listAdapter.notifyDataSetChanged();
    }

    protected String getPayModeSectionText() {
        return "支付方式";
    }

    protected String getOtherPayModeSectionText() {
        return "其他支付方式";
    }


    protected int rowCount;
    protected int tipRow;
    protected int billSection;
    protected int billNoRow;
    protected int orderDateRow;
    protected int orderAmountRow;
    protected int payAmountRow;

    protected int dividerRow;
    protected int payModeSection;
    protected int payModeStartRow;
    protected int payModeEndRow;
    protected int otherPayModeSection;
    protected int otherPayModeStartRow;
    protected int otherPayModeEndRow;
    protected int payModeDividerRow;
    protected int payActionRow;

    class ListAdapter extends BaseAdapter {

        private Context adapterContext;

        public ListAdapter(Context context) {
            adapterContext = context;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            if (position >= payModeStartRow && position <= payModeEndRow) {
                return true;
            } else if (position >= otherPayModeStartRow && position <= otherPayModeEndRow) {
                return true;
            } else if (position == payActionRow) {
                return true;
            }
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == billNoRow || position == orderDateRow || position == payAmountRow || position == orderAmountRow) {
                return 1;
            } else if (position == billSection || position == payModeSection || position == otherPayModeSection) {
                return 2;
            } else if (position >= payModeStartRow && position <= payModeEndRow) {
                return 3;
            } else if (position >= otherPayModeStartRow && position <= otherPayModeEndRow) {
                return 3;
            } else if (position == payModeDividerRow) {
                return 4;
            } else if (position == payActionRow) {
                return 5;
            } else if (position == tipRow) {
                return 6;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 7;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int viewType = getItemViewType(position);
            if (viewType == 0) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(adapterContext);
                }
            } else if (viewType == 1) {
                if (convertView == null) {
                    convertView = new PayInfoCell(adapterContext);
                }
                PayInfoCell cell = (PayInfoCell) convertView;
                cell.setSmall(true);
                cell.setTextSize(16);
                cell.setValueTextSize(18);
                cell.setTextColor(0xff212121);
                if (position == billNoRow) {
                    cell.setValueTextColor(0xff757575);
                    cell.setTextAndValue("订单编号", orderNo, true);
                } else if (position == orderDateRow) {
                    cell.setValueTextColor(0xff757575);
                    cell.setTextAndValue("订单日期", orderDate, true);
                } else if (position == payAmountRow) {
                    cell.setValueTextColor(ResourcesConfig.priceFontColor);
                    cell.setTextAndValue("支付金额", ShoppingHelper.formatPrice(orderPayAmount), false);
                } else if (position == orderAmountRow) {
                    cell.setValueTextColor(0xff212121);
                    cell.setTextAndValue("订单金额", ShoppingHelper.formatPrice(orderPayAmount, false), true);
                }
            } else if (viewType == 2) {
                if (convertView == null) {
                    convertView = new H3HeaderCell(adapterContext);
                }
                H3HeaderCell cell = (H3HeaderCell) convertView;
                cell.setTextSize(16);
                cell.setTextColor(ResourcesConfig.textPrimary);
                if (position == billSection) {
                    cell.setText("待支付订单");
                } else if (position == payModeSection) {
                    cell.setText(getPayModeSectionText());
                } else if (position == otherPayModeSection) {
                    cell.setText(getOtherPayModeSectionText());
                }
            } else if (viewType == 3) {
                if (convertView == null) {
                    convertView = new PayModeCell(adapterContext);
                }
                PayModeCell cell = (PayModeCell) convertView;
                if (position >= payModeStartRow && position <= payModeEndRow) {
                    int index = position - payModeStartRow;
                    PayMode mode = payModes.valueAt(index);
                    cell.setValue(mode.iconResId, mode.name, mode.desc, mode.id == selectedPayModeId, position != payModeEndRow);
                } else if (position >= otherPayModeStartRow && position <= otherPayModeEndRow) {
                    int index = position - otherPayModeStartRow;
                    PayMode mode = otherPayModes.valueAt(index);
                    cell.setValue(mode.iconResId, mode.name, mode.desc, mode.id == selectedPayModeId, position != otherPayModeEndRow);
                }

            } else if (viewType == 4) {
                if (convertView == null) {
                    convertView = new DividerCell(adapterContext);
                }
            } else if (viewType == 5) {
                if (convertView == null) {
                    convertView = new ActionCell(adapterContext);
                }

                ActionCell cell = (ActionCell) convertView;
                if (position == payActionRow) {
                    cell.setValue("支付");
                }
            } else if (viewType == 6) {
                if (convertView == null) {
                    convertView = new TipCell(adapterContext);
                }
                TipCell cell = (TipCell) convertView;
                CharSequence tipText = getTip();
                cell.setValue(tipText);
            }
            return convertView;
        }
    }
}
