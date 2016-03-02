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
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.H3HeaderCell;
import com.romens.yjk.health.ui.cells.PayInfoCell;
import com.romens.yjk.health.ui.cells.PayModeCell;
import com.romens.yjk.health.ui.components.ToastCell;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public abstract class PayPrepareBaseActivity extends BaseActionBarActivityWithAnalytics {
    public static final String ARGUMENTS_KEY_FROM_ORDER_DETAIL = "key_from_order_detail";
    public static final String ARGUMENTS_KEY_ORDER_NO = "key_order_no";
    public static final String ARGUMENTS_KEY_NEED_PAY_AMOUNT = "key_need_pay_amount";

    protected ListView listView;
    protected BaseAdapter listAdapter;

    //是否来自订单详情
    protected boolean isFromOrderDetail = false;
    //订单编号
    protected String orderNo;
    //订单待支付金额
    protected BigDecimal orderPayAmount;

    protected int selectedPayModeKey;
    protected final SparseArray<PayMode> medicarePayModes = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        isFromOrderDetail = bundle.getBoolean(ARGUMENTS_KEY_FROM_ORDER_DETAIL, false);
        orderNo = bundle.getString(ARGUMENTS_KEY_ORDER_NO);
        double amount = bundle.getDouble(ARGUMENTS_KEY_NEED_PAY_AMOUNT, 0);
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

    protected void onInitPayMode(SparseArray<PayMode> payModes) {
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
        selectedPayModeKey = 0;
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
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetBillPayRequestParams", args);
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

    protected abstract void formatPayParamsResponse(JsonNode jsonNode);

    protected void updateAdapter() {
        rowCount = 0;
        billSection = rowCount++;
        billNoRow = rowCount++;
        payAmountRow = rowCount++;

        dividerRow = rowCount++;
        payModeSection = rowCount++;
        payModeStartRow = rowCount;
        rowCount += medicarePayModes.size();
        payModeEndRow = rowCount - 1;
        payModeDividerRow = rowCount++;
        payActionRow = rowCount++;

        listAdapter.notifyDataSetChanged();
    }

    protected int rowCount;
    protected int billSection;
    protected int billNoRow;
    protected int payAmountRow;

    protected int dividerRow;
    protected int payModeSection;
    protected int payModeStartRow;
    protected int payModeEndRow;
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
            } else if (position == payActionRow) {
                return true;
            }
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == billNoRow || position == payAmountRow) {
                return 1;
            } else if (position == billSection || position == payModeSection) {
                return 2;
            } else if (position >= payModeStartRow && position <= payModeEndRow) {
                return 3;
            } else if (position == payModeDividerRow) {
                return 4;
            } else if (position == payActionRow) {
                return 5;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 6;
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
                cell.setTextSize(16);
                cell.setValueTextSize(18);
                cell.setTextColor(0xff212121);
                if (position == billNoRow) {
                    cell.setValueTextColor(0xff757575);
                    cell.setTextAndValue("订单编号", orderNo, true);
                } else if (position == payAmountRow) {
                    cell.setValueTextColor(ResourcesConfig.priceFontColor);
                    cell.setTextAndValue("支付金额", ShoppingHelper.formatPrice(orderPayAmount), false);
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
                    cell.setText("支付方式");
                }
            } else if (viewType == 3) {
                if (convertView == null) {
                    convertView = new PayModeCell(adapterContext);
                }

                PayModeCell cell = (PayModeCell) convertView;
                int key = position - payModeStartRow;
                PayMode mode = medicarePayModes.get(key);
                cell.setValue(mode.iconResId, mode.name, mode.desc, key == selectedPayModeKey, position != payModeEndRow);
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
            }
            return convertView;
        }
    }
}
