package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.network.FacadeArgs;
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
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.OrderDao;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.model.GoodsListEntity;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.OrderGoodsCell;
import com.romens.yjk.health.ui.cells.OrderStoreCell;
import com.romens.yjk.health.ui.cells.RatingBarCell;
import com.romens.yjk.health.ui.cells.TextInputNoLineCell;
import com.romens.yjk.health.ui.components.ToastCell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 2016-04-06 14:08
 * @description
 */
public class OrderCommitActivity extends DarkActionBarActivity {
    public static final String ARGUMENT_KEY_ORDER_ENTITY = "key_order_entity";

    private TextInputNoLineCell commitTextCell;
    private RatingBarCell ratingBarCell1;
    private RatingBarCell ratingBarCell2;
    private ListView listView;
    private OrderEntity entity;

    private List<GoodsListEntity> goodsListEntities;
    private GoodsAdapter adapter;

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

        adapter = new GoodsAdapter();
        initData();
    }

    private void trySubmitCommit() {
        //暂时不检测属性有效性
        String orderId = entity.orderId;
        String bodyText = commitTextCell.getValue();
        int goodsStar = (int) ratingBarCell1.getValue();
        int buyStar = (int) ratingBarCell2.getValue();
        submitCommit(orderId, bodyText, goodsStar, buyStar);
    }

    public void initData() {
        requestOrderDetailList(entity.orderId);
    }

    private int rowCount;
    private int shopNameRow;
    private int goodsBeginRow;
    private int goodsEndRow;

    public void setRow() {
        rowCount = 0;
        shopNameRow = rowCount++;
        goodsBeginRow = rowCount;
        rowCount += goodsListEntities.size();
        goodsEndRow = rowCount - 1;
    }

    private void requestOrderDetailList(String orderId) {
        String userGuid = UserGuidConfig.USER_GUID;
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("ORDERID", orderId);

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
                        } else {
                            ToastCell.toast(OrderCommitActivity.this, "查询订单失败!");
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    public void setOrderData(JsonNode response) {
        if (response == null) {
            return;
        }
        goodsListEntities = new ArrayList<>();
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
            goodsEntity.setDetailDescitption(subObjcet.get("DETAILDESCRIPTION").asText());
            goodsEntity.setSpec(subObjcet.get("SPEC").asText());
            goodsEntity.setGoodsSortGuid(subObjcet.get("GOODSSORTGUID").asText());
            goodsEntity.setShopId(subObjcet.get("SHOPID").asText());
            goodsEntity.setShopName(subObjcet.get("SHOPNAME").asText());
            goodsListEntities.add(goodsEntity);
        }
        setRow();
        listView.setAdapter(adapter);
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
                                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange, orderId);
                                finish();
                                return;
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

    @Override
    protected String getActivityName() {
        return "订单新增评价";
    }

    class GoodsAdapter extends BaseAdapter {

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
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
            if (position == shopNameRow) {
                return 1;
            } else if (position >= goodsBeginRow && position <= goodsEndRow) {
                return 2;
            }

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 1) {
                OrderStoreCell cell = new OrderStoreCell(parent.getContext());
                GoodsListEntity item = goodsListEntities.get(0);
                cell.setValue(item.getShopName(), true);
                return cell;
            } else if (type == 2) {
                OrderGoodsCell cell = new OrderGoodsCell(parent.getContext());
                int itemIndex = position - goodsBeginRow;
                GoodsListEntity item = goodsListEntities.get(itemIndex);
                String iconPath = item.getGoodsUrl();
                String name = item.getName();
                String desc = String.format("规格:%s", item.getSpec());
                BigDecimal userPrice = new BigDecimal(item.getGoodsPrice());
                int count = Integer.parseInt(item.getBuyCount());
                cell.setValue(iconPath, name, desc, userPrice, count, true);
                return cell;
            }
            return null;
        }
    }
}
