package com.romens.yjk.health.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.AndroidUtilities;
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
import com.romens.android.ui.cells.EmptyCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.PayInfoCell;
import com.romens.yjk.health.ui.cells.PayStateCell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/3/1
 * @description
 */
public abstract class PayActivity extends BaseActionBarActivityWithAnalytics {
    public static final String ARGUMENTS_KEY_FROM_PAY_PREPARE = "app_key_from_pay_prepare";
    public static final String ARGUMENTS_KEY_PAY_PARAMS = "app_key_pay_params";

    public static final String ARGUMENT_KEY_ORDER_NO = "app_key_order_no";
    public static final String ARGUMENT_KEY_ORDER_TIME = "app_key_order_time";
    public static final String ARGUMENT_KEY_ORDER_AMOUNT = "app_key_order_amount";

    protected boolean isFromPayPrepare = false;
    protected Bundle payParams;

    protected ListView listView;

    private ListAdapter listAdapter;
    private PayState payState;

    protected String orderNo;
    protected String orderDate;
    protected BigDecimal orderAmount;

    private final List<PayResultInfo> payResultInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);
        actionBar.setTitle("支付结果");
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
        listAdapter = new ListAdapter(this);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == checkOrderRow) {
                    openOrderDetail();
                }
            }
        });
    }

    protected abstract String getPayModeText();

    protected void openOrderDetail() {
        if (!TextUtils.isEmpty(orderNo)) {
            UIOpenHelper.openOrderDetailForOrderNoActivity(PayActivity.this, orderNo);
            finish();
        }
    }

    /**
     * 响应继承的PayActivity Create完成事件
     */
    protected void onCreateCompleted() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(ARGUMENTS_KEY_FROM_PAY_PREPARE)) {
            isFromPayPrepare = extras.getBoolean(ARGUMENTS_KEY_FROM_PAY_PREPARE, false);
        } else {
            isFromPayPrepare = false;
        }
        if (isFromPayPrepare) {
            payParams = extras.getBundle(ARGUMENTS_KEY_PAY_PARAMS);
            orderNo = payParams.getString(ARGUMENT_KEY_ORDER_NO);
            orderDate = payParams.getString(ARGUMENT_KEY_ORDER_TIME);
            double amount = payParams.getDouble(ARGUMENT_KEY_ORDER_AMOUNT, 0);
            orderAmount = new BigDecimal(amount);

            onPayRequest(payParams);
        } else {
            onPayResponse(intent);
        }
    }

    protected abstract void onPayRequest(Bundle payParams);

    protected abstract void onPayResponse(Intent intent);

    protected void changePayState(PayState state) {
        payState = state;
        updateAdapter();
    }

    protected void postPayResponseToServerAndCheckPayResult(Map<String, String> args) {
        needShowProgress("正在查询支付结果,请稍候...");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "SubmitOrderPayResult", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(PayActivity.class)
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
                                onPostPayResponseToServerCallback(response, null);
                                return;
                            }
                        } else {
                            error = "请求支付失败,请稍后重试!";
                        }
                        onPostPayResponseToServerCallback(null, error);
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    protected void onPostPayResponseToServerCallback(JsonNode response, String error) {
        if (TextUtils.isEmpty(error)) {
            String payResult = response.get("PAYRESULT").asText();
            if (TextUtils.equals("1", payResult)) {
                changePayState(PayState.SUCCESS);
                return;
            }
        }
        changePayState(PayState.FAIL);
    }

    @Override
    public void onBackPressed() {
        needFinish();
    }


    protected abstract void needFinish();

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onPayResponse(intent);
    }

    protected void updateAdapter() {
        rowCount = 0;
        payStateRow = rowCount++;
        dividerRow = rowCount++;
        orderNoRow = rowCount++;
        orderTimeRow = rowCount++;
        orderAmountRow = rowCount++;
        orderPayModeRow = rowCount++;
        checkOrderRow = rowCount++;
        if (payResultInfo.size() > 0) {
            payResultSection = rowCount++;
            payResultBeginRow = rowCount++;
            rowCount += payResultInfo.size() - 1;
        } else {
            payResultSection = -1;
            payResultBeginRow = -1;
        }

        listAdapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int payStateRow;
    private int dividerRow;
    private int orderNoRow;
    private int orderTimeRow;
    private int orderAmountRow;
    private int orderPayModeRow;
    private int checkOrderSectionRow;
    private int checkOrderRow;

    private int payResultSection;
    private int payResultBeginRow;


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
            if (position == checkOrderRow) {
                return true;
            }
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == payStateRow) {
                return 1;
            } else if (position == dividerRow) {
                return 2;
            } else if (position == checkOrderRow) {
                return 3;
            } else if (position == checkOrderSectionRow || position == payResultSection) {
                return 4;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 5;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int viewType = getItemViewType(position);
            if (viewType == 2) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(adapterContext);
                }
            } else if (viewType == 1) {
                if (convertView == null) {
                    convertView = new PayStateCell(adapterContext);
                }
                PayStateCell cell = (PayStateCell) convertView;
                cell.setValue(payState, false);
            } else if (viewType == 3) {
                if (convertView == null) {
                    convertView = new ActionCell(adapterContext);
                }
                ActionCell cell = (ActionCell) convertView;
                cell.setValue("查看订单详情");
            } else if (viewType == 4) {
                if (convertView == null) {
                    convertView = new EmptyCell(adapterContext);
                }
                EmptyCell cell = (EmptyCell) convertView;
                cell.setHeight(AndroidUtilities.dp(32));
            } else if (viewType == 0) {
                if (convertView == null) {
                    convertView = new PayInfoCell(adapterContext);
                }
                PayInfoCell cell = (PayInfoCell) convertView;
                cell.setTextSize(16);
                cell.setValueTextSize(18);
                cell.setTextColor(0xff212121);
                if (position == orderNoRow) {
                    cell.setValueTextColor(0xff757575);
                    cell.setTextAndValue("订单编号", orderNo, true);
                } else if (position == orderTimeRow) {
                    cell.setValueTextColor(0xff757575);
                    cell.setTextAndValue("订单日期", orderDate, true);
                } else if (position == orderAmountRow) {
                    cell.setValueTextColor(ResourcesConfig.priceFontColor);
                    cell.setTextAndValue("支付金额", ShoppingHelper.formatPrice(orderAmount), true);
                } else if (position == orderPayModeRow) {
                    cell.setValueTextColor(0xff757575);
                    String payModeText = getPayModeText();
                    cell.setTextAndValue("支付方式", payModeText, true);
                } else if (payResultBeginRow != -1 && position > payResultBeginRow) {
                    cell.setValueTextColor(0xff757575);
                    PayResultInfo item = payResultInfo.get(position - payResultBeginRow);
                    cell.setTextAndValue(item.caption, item.value, true);
                }
            }
            return convertView;
        }
    }

    protected static class PayResultInfo {
        public final CharSequence caption;
        public final CharSequence value;

        public PayResultInfo(CharSequence caption, CharSequence value) {
            this.caption = caption;
            this.value = value;
        }
    }
}
