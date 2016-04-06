package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.easemob.exceptions.EMNetworkUnconnectedException;
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
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.OrderDao;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.RatingBarCell;
import com.romens.yjk.health.ui.cells.TextInputNoLineCell;
import com.romens.yjk.health.ui.components.ToastCell;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 2016-04-06 14:08
 * @description
 */
public class OrderCommitActivity extends BaseActionBarActivityWithAnalytics {
    public static final String ARGUMENT_KEY_ORDER_ENTITY = "key_order_entity";

    private TextInputNoLineCell commitTextCell;
    private RatingBarCell ratingBarCell1;
    private RatingBarCell ratingBarCell2;
    private ListView listView;

    private OrderEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String orderId = intent.getStringExtra(ARGUMENT_KEY_ORDER_ENTITY);
        OrderDao orderDao = DBInterface.instance().openReadableDb().getOrderDataDao();
        entity = orderDao.queryBuilder().where(OrderDao.Properties.Id.eq(orderId)).unique();

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);

        actionBar.setTitle("评价");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });

        LinearLayout dataContent = new LinearLayout(this);
        dataContent.setOrientation(LinearLayout.VERTICAL);
        content.addView(dataContent, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        TextSettingsCell orderNoCell = new TextSettingsCell(this);
        orderNoCell.setTextColor(0x80000000);
        orderNoCell.setValueTextColor(0xff212121);
        dataContent.addView(orderNoCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        orderNoCell.setTextAndValue("订单编号", entity.orderNo, true);
        commitTextCell = new TextInputNoLineCell(this);
        commitTextCell.setMultilineValue(true);
        commitTextCell.setTextAndValue("评价信息", "", "点击输入评价内容", false, true);
        dataContent.addView(commitTextCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        ratingBarCell1 = new RatingBarCell(this);
        ratingBarCell1.setValue("药品质量", 5f, false);
        dataContent.addView(ratingBarCell1, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        ratingBarCell2 = new RatingBarCell(this);
        ratingBarCell2.setValue("购药体验", 5f, false);
        dataContent.addView(ratingBarCell2, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        ActionCell commitCell = new ActionCell(this);
        commitCell.setBackgroundResource(R.drawable.list_selector);
        commitCell.setClickable(true);
        commitCell.setValue("提交评价");
        dataContent.addView(commitCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        commitCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySubmitCommit();
            }
        });
        ShadowSectionCell divider = new ShadowSectionCell(this);
        dataContent.addView(divider, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        dataContent.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

    }

    private void trySubmitCommit() {
        //暂时不检测属性有效性
        String orderId = entity.orderId;
        String bodyText = commitTextCell.getValue();
        int goodsStar = (int) ratingBarCell1.getValue();
        int buyStar = (int) ratingBarCell2.getValue();
        submitCommit(orderId, bodyText, goodsStar, buyStar);
    }


    private void submitCommit(final String orderId, String bodyText, int goodsStar, int buyStar) {
        needShowProgress("正在提交评价...");
        Map<String, Object> args = new HashMap<>();
        args.put("MERCHANDISEID", orderId);
        args.put("DILEVERYSTAR", goodsStar);
        args.put("QUALITYSTAR", buyStar);
        args.put("TEXT", bodyText);
        args.put("ISAPPEND", "0");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "AssessMerch", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(OrderCommitActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        needHideProgress();
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (!response.has("ERROR")) {
                                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange);
                                finish();
                            }
                        }
                        ToastCell.toast(OrderCommitActivity.this, "提交评价失败,请重试!");
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);

//        Message message = new Message.MessageBuilder()
//                .withProtocol(protocol)
////                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
////                }))
//                .build();
//        FacadeClient.request(OrderEvaluateActivity.this, message, new FacadeClient.FacadeCallback() {
//            @Override
//            public void onTokenTimeout(Message msg) {
//                Toast.makeText(OrderEvaluateActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResult(Message msg, Message errorMsg) {
//                if (msg != null) {
//                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
//                    String requestCode = "";
//                    try {
//                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
//                        requestCode = jsonObject.getString("success");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    if (requestCode.equals("yes")) {
//                        Toast.makeText(OrderEvaluateActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(OrderEvaluateActivity.this, MyOrderActivity.class);
////                        startActivity(intent);
//                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange);
//                        finish();
//                    } else {
//                        Toast.makeText(OrderEvaluateActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                if (errorMsg != null) {
//                    Log.e("tag", "--requestCode--->" + errorMsg.msg);
//                }
//            }
//        });
    }
}
