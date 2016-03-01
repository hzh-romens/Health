package com.romens.yjk.health.pay;

import android.content.Intent;
import android.os.Bundle;
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
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;
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

    @Override
    public void onBackPressed() {
        needFinish();
    }


    protected abstract void needFinish();

    protected abstract BaseAdapter onCreateAdapter();

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
}
