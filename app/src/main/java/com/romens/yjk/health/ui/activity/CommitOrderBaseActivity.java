package com.romens.yjk.health.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.romens.android.AndroidUtilities;
import com.romens.android.io.json.JacksonMapper;
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
import com.romens.android.ui.cells.EmptyCell;
import com.romens.android.ui.cells.LoadingCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.db.entity.ShopEntity;
import com.romens.yjk.health.db.entity.ShoppingCartDataEntity;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.CommitOrderEntity;
import com.romens.yjk.health.model.FilterChildEntity;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.H3HeaderCell;
import com.romens.yjk.health.ui.cells.OrderGoodsCell;
import com.romens.yjk.health.ui.cells.OrderInfoCell;
import com.romens.yjk.health.ui.cells.OrderStoreCell;
import com.romens.yjk.health.ui.cells.TextDetailInfoCell;
import com.romens.yjk.health.ui.components.ToastCell;
import com.romens.yjk.health.ui.fragment.ShoppingCartFragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/24
 * @description
 */
public abstract class CommitOrderBaseActivity extends BaseActionBarActivityWithAnalytics {
    public static final String ARGUMENTS_KEY_SELECT_GOODS = "key_select_goods";

    private RecyclerView listView;
    private ListAdapter adapter;

//    private FrameLayout bottomBar;
//    private TextView amountDescView;

    private final HashMap<String, String> addressInfo = new HashMap<>();
    private final List<ShopEntity> shopEntities = new ArrayList<>();
    private final Map<String, List<ShoppingCartDataEntity>> needCommitGoods = new HashMap<>();

    private int goodsCount = 0;
    private BigDecimal goodsAmount = BigDecimal.ZERO;
    private BigDecimal couponAmount = BigDecimal.ZERO;

    private List<OrderItem> orderItems;

    private int selectPayType = 0;
    private int selectDeliveryType = 0;

    private String orderCouponID;
    private String orderInvoice;

    private static final int REQUEST_CODE_ADDRESS = 0;
    private static final int REQUEST_CODE_PAY_DELIVERY = 1;
    private static final int REQUEST_CODE_INVOICE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        content.setBackgroundColor(0xffffffff);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);
        actionBar.setTitle("提交订单");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });
        FrameLayout dataContainer = new FrameLayout(this);
        content.addView(dataContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView = new RecyclerView(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        dataContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP));
//
//        bottomBar = new FrameLayout(this);
//        bottomBar.setBackgroundColor(0xfff0f0f0);
//        dataContainer.addView(bottomBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 64, Gravity.BOTTOM));
//        amountDescView = new TextView(this);
//        amountDescView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//        AndroidUtilities.setMaterialTypeface(amountDescView);
//        amountDescView.setTextColor(0xff212121);
//        amountDescView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//        amountDescView.setText("小计(3个商品) ￥10000");
//        bottomBar.addView(amountDescView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 8, 96, 8));
//
//        LinearLayout commitBtn = new LinearLayout(this);
//        commitBtn.setOrientation(LinearLayout.HORIZONTAL);
//        commitBtn.setGravity(Gravity.CENTER_VERTICAL);
//        commitBtn.setClickable(true);
//        commitBtn.setBackgroundResource(R.drawable.btn_primary);
//        commitBtn.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
//        ImageView commitIcon = new ImageView(this);
//        commitIcon.setScaleType(ImageView.ScaleType.CENTER);
//        commitIcon.setImageResource(R.drawable.ic_done_all_white_24dp);
//        commitBtn.addView(commitIcon, LayoutHelper.createLinear(24, 24));
//
//        TextView commitText = new TextView(this);
//        commitText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
//        AndroidUtilities.setMaterialTypeface(commitText);
//        commitText.setTextColor(0xffffffff);
//        commitText.setGravity(Gravity.CENTER);
//        commitText.setText("结算");
//        commitBtn.addView(commitText, LayoutHelper.createLinear(56, LayoutHelper.WRAP_CONTENT));
//        bottomBar.addView(commitBtn, LayoutHelper.createFrame(88, 40, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 8, 8, 8));

        adapter = new ListAdapter(new AdapterDelegate() {
            @Override
            public void onItemSelect(int position) {
                processItemSelect(position);
            }
        });
        listView.setAdapter(adapter);

        handleShoppingCartData();
        updateAdapter();
        getUserDefaultAddress();
    }

    private void processItemSelect(int position) {
        if (position == addressRow) {
            UIOpenHelper.openControlAddressActivityForResult(CommitOrderBaseActivity.this, 0);
        } else if (position == orderPayTypeRow) {
            Intent intent = new Intent(CommitOrderBaseActivity.this, OrderPayTypeActivity.class);
            boolean supportMedicareCard = supportMedicareCardPay();
            intent.putExtra("SupportMedicareCard", supportMedicareCard);
            intent.putExtra("PayType", selectPayType);
            intent.putExtra("DeliveryType", selectDeliveryType);
            startActivityForResult(intent, REQUEST_CODE_PAY_DELIVERY);
        } else if (position == couponRow) {

        } else if (position == invoiceRow) {
            Intent intent = new Intent(CommitOrderBaseActivity.this, OrderInvoiceActivity.class);
            intent.putExtra(OrderInvoiceActivity.ARGUMENTS_KEY_INVOICE_NAME, TextUtils.isEmpty(orderInvoice) ? "" : orderInvoice);
            startActivityForResult(intent, REQUEST_CODE_INVOICE);
        }
    }

    private void handleShoppingCartData() {
        shopEntities.clear();
        needCommitGoods.clear();

        Intent intent = getIntent();
        ArrayList<String> needCommitGoodIds = intent.getStringArrayListExtra(ARGUMENTS_KEY_SELECT_GOODS);
        List<ShoppingCartDataEntity> data = DBInterface.instance().findShoppingCartData(needCommitGoodIds);
        goodsCount = 0;
        goodsAmount = BigDecimal.ZERO;
        for (ShoppingCartDataEntity entity :
                data) {
            goodsCount += entity.getBuyCount();
            goodsAmount = goodsAmount.add(entity.getUserPrice());
            String shopID = entity.getShopID();
            if (!needCommitGoods.containsKey(shopID)) {
                shopEntities.add(new ShopEntity(shopID, entity.getShopName()));
                needCommitGoods.put(shopID, new ArrayList<ShoppingCartDataEntity>());
            }
            needCommitGoods.get(shopID).add(entity);
        }

        orderItems = new ArrayList<>();
        List<ShoppingCartDataEntity> dataTemp;
        int position = 0;
        for (ShopEntity entity : shopEntities) {
            OrderStoreItem storeItem = new OrderStoreItem(entity.shopId, entity.shopName);
            dataTemp = needCommitGoods.get(entity.shopId);
            storeItem.setStart(position);
            storeItem.setCount(dataTemp.size());
            orderItems.add(storeItem);
            for (ShoppingCartDataEntity goods : dataTemp) {
                orderItems.add(new OrderGoodsItem(goods.getGuid(), entity.shopId, position));
            }
            position++;
            position += dataTemp.size();
        }
    }

    public void getUserDefaultAddress() {
        isLoadingDefaultAddress = true;
        updateAdapter();
        addressInfo.clear();
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).put("DEFAULTFLAG", "1").build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserAddressList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(CommitOrderBaseActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        isLoadingDefaultAddress = false;
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (response != null && response.size() > 0) {

                                //ResponseProtocol{response='[{
                                // "ADDRESSID":"567905bac6247",
                                // "USERGUID":"5673a3bcd20bf1450419132",
                                // "RECEIVER":"呵呵v个",
                                // "CONTACTPHONE":"18261606920",
                                // "PROVINCE":"P5",
                                // "PROVINCENAME":"内蒙古自治区",
                                // "CITYNAME":"内蒙古自治区",
                                // "REGION":"P5",
                                // "REGIONNAME":"内蒙古自治区",
                                // "ADDRESS":"呼呼呼",
                                // "ISDEFAULT":"1",
                                // "ADDRESSTYPE":"1"}]'}
                                JsonNode addressNode = response.get(0);
                                addressInfo.put("ID", addressNode.get("ADDRESSID").asText());
                                addressInfo.put("USER", addressNode.get("RECEIVER").asText());
                                addressInfo.put("PHONE", addressNode.get("CONTACTPHONE").asText());
                                addressInfo.put("PROVINCENAME", addressNode.get("PROVINCENAME").asText());
                                addressInfo.put("CITYNAME", addressNode.get("CITYNAME").asText());
                                addressInfo.put("REGIONNAME", addressNode.get("REGIONNAME").asText());
                                addressInfo.put("ADDRESS", addressNode.get("ADDRESS").asText());
                            }
                        }
                        updateAdapter();
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    private void tryPostOrder() {
        if (addressInfo == null || addressInfo.size() <= 0) {
            ToastCell.toast(CommitOrderBaseActivity.this, "请选择送货地址!");
            return;
        }
//        SpannableStringBuilder message = new SpannableStringBuilder();
//        //String amountText=
//        String orderInfo = String.format("共 %d 个商品,合计 %s ,是否确定提交订单?",goodsCount,)
//        SpannableString phone = new SpannableString();
//        phone.setSpan(new ForegroundColorSpan(0xff2baf2b), 0, phone.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        phone.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, phone.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        name.append(phone);
//        new AlertDialog.Builder(CommitOrderBaseActivity.this)
//                .setTitle("提交订单")
//                .setMessage()
    }

    /**
     * 提交订单
     */
    private void postOrder() {
        needShowProgress("正在提交订单...");
        ObjectNode orderNode = JacksonMapper.getInstance().createObjectNode();
        orderNode.put("ADDRESSID", addressInfo.get("ID"));
        orderNode.put("DELIVERYTYPE", selectDeliveryType);
        orderNode.put("PAYTYPE", selectPayType);
        orderNode.put("COUPONGUID", orderCouponID);
        orderNode.put("BILLNAME", orderInvoice);
        ArrayNode goodsArrayNode = JacksonMapper.getInstance().createArrayNode();

        Iterator<Map.Entry<String, List<ShoppingCartDataEntity>>> goodsData = needCommitGoods.entrySet().iterator();
        while (goodsData.hasNext()) {
            Map.Entry<String, List<ShoppingCartDataEntity>> entry = goodsData.next();

            for (ShoppingCartDataEntity goods :
                    entry.getValue()) {
                ObjectNode goodsNode = JacksonMapper.getInstance().createObjectNode();
                goodsNode.put("GOODSGUID", goods.getGuid());
                goodsNode.put("SHOPID", goods.getShopID());
                goodsNode.put("BUYCOUNT", goods.getBuyCount());
                goodsNode.put("GOODSPRICE", goods.getBuyPrice());
                goodsArrayNode.add(goodsNode);
            }
        }
        orderNode.set("GOODSLIST", goodsArrayNode);

        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid())
                .put("JSONDATA", orderNode.toString())
                .build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "saveOrder", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(CommitOrderBaseActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        needHideProgress();
                        if (errorMessage == null) {

                        }
                    }
                }).build();
        ConnectManager.getInstance().request(CommitOrderBaseActivity.this, connect);
//            Message message = new Message.MessageBuilder()
//                    .withProtocol(protocol).build();
//            FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
//                @Override
//                public void onTokenTimeout(Message msg) {
//                    Log.e("GetBuyCarCount", "ERROR");
//                    needHideProgress();
//                }
//
//                @Override
//                public void onResult(Message msg, Message errorMsg) {
//                    needHideProgress();
//                    if (errorMsg == null) {
//                        ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
//                        try {
//                            JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
//                            String success = jsonObject.getString("success");
//
//                            if (success.equals("yes")) {
//                                if ("支付宝支付".equals(deliveryName) || "微信支付".equals(deliveryName)) {
//                                    UIOpenHelper.openPayActivity(CommitOrderActivity.this, deliveryName, sumMoney, jsonObject.getString("msg1"));
//                                } else {
//                                    Intent i = new Intent(CommitOrderActivity.this, CommitResultActivity.class);
//                                    i.putExtra("success", "true");
//                                    i.putExtra("sumMoney", (sumMoney - coupon) + "");
//                                    i.putExtra("orderNumber", jsonObject.getString("msg1"));
//                                    i.putExtra("time", jsonObject.getString("msg2"));
//                                    AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, -count);
//                                    startActivity(i);
//                                    finish();
//                                }
//                            } else {
//                                Intent i = new Intent(CommitOrderActivity.this, CommitResultActivity.class);
//                                String errorMsgs = jsonObject.getString("errorMsg");
//                                i.putExtra("success", "false");
//                                i.putExtra("errormsg", errorMsgs);
//                                startActivity(i);
//                                finish();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Log.e("CommitOrder", errorMsg.msg);
//                    }
//                }
//            });
    }

    public String getJsonData(List<FilterChildEntity> data, String deliverytype, String addressid, String billName) {
        Gson gson = new Gson();
        CommitOrderEntity commitOrderEntity = new CommitOrderEntity();
        commitOrderEntity.setDELIVERYTYPE(deliverytype);
        commitOrderEntity.setADDRESSID(addressid);
        commitOrderEntity.setGOODSLIST(data);
        commitOrderEntity.setBILLNAME(billName);
        commitOrderEntity.setCOUPONGUID("");
        String JSON_DATA = gson.toJson(commitOrderEntity);
        return JSON_DATA;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                AddressEntity addressEntity = (AddressEntity) data.getSerializableExtra("responseCommitEntity");
                addressInfo.clear();
                addressInfo.put("ID", addressEntity.getADDRESSID());
                addressInfo.put("USER", addressEntity.getRECEIVER());
                addressInfo.put("PHONE", addressEntity.getCONTACTPHONE());
                addressInfo.put("PROVINCENAME", addressEntity.getPROVINCENAME());
                addressInfo.put("CITYNAME", addressEntity.getCITYNAME());
                addressInfo.put("REGIONNAME", addressEntity.getREGIONNAME());
                addressInfo.put("ADDRESS", addressEntity.getADDRESS());
                updateAdapter();
            }
        } else if (requestCode == REQUEST_CODE_PAY_DELIVERY) {
            if (resultCode == RESULT_OK) {
                selectPayType = data.getIntExtra("PayType", 0);
                selectDeliveryType = data.getIntExtra("DeliveryType", 0);
                updateAdapter();
            }
        } else if (requestCode == REQUEST_CODE_INVOICE) {
            if (resultCode == RESULT_OK) {
                orderInvoice = data.getStringExtra(OrderInvoiceActivity.ARGUMENTS_KEY_INVOICE_NAME);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private int rowCount;
    private int addressSection;
    private int addressSection1;
    private int addressLoadingRow;
    private int addressRow;

    private int orderPayTypeSection;
    private int orderPayTypeSection1;
    private int orderPayTypeRow;
    private int couponRow;
    private int invoiceRow;

    private int orderInfoSection;
    private int orderInfoSection1;
    private int orderInfoRow;
    private int orderSubmitRow;

    private int goodsSection;
    private int goodsSection1;
    private int goodsBeginRow;
    private int goodsEndRow;


    private boolean isLoadingDefaultAddress;

    private void updateAdapter() {
        rowCount = 0;
        addressSection = rowCount++;
        addressSection1 = rowCount++;
        if (isLoadingDefaultAddress) {
            addressLoadingRow = rowCount++;
            addressRow = -1;
        } else {
            addressLoadingRow = -1;
            addressRow = rowCount++;
        }

        orderPayTypeSection = rowCount++;
        orderPayTypeSection1 = rowCount++;
        orderPayTypeRow = rowCount++;
        couponRow = rowCount++;
        invoiceRow = rowCount++;

        orderInfoSection = rowCount++;
        orderInfoSection1 = rowCount++;
        orderInfoRow = rowCount++;
        orderSubmitRow = rowCount++;

        goodsSection = rowCount++;
        goodsSection1 = rowCount++;
        goodsBeginRow = rowCount;
        rowCount += orderItems.size();
        goodsEndRow = rowCount - 1;


        adapter.notifyDataSetChanged();
    }


    public interface AdapterDelegate {
        void onItemSelect(int position);
    }

    class ListAdapter extends RecyclerView.Adapter {
        private AdapterDelegate adapterDelegate;

        public ListAdapter(AdapterDelegate delegate) {
            adapterDelegate = delegate;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                EmptyCell cell = new EmptyCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                cell.setHeight(AndroidUtilities.dp(16));
                return new Holder(cell);
            } else if (viewType == 1) {
                H3HeaderCell cell = new H3HeaderCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 2) {
                ShadowSectionCell cell = new ShadowSectionCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 3) {
                LoadingCell cell = new LoadingCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 4) {
                TextDetailInfoCell cell = new TextDetailInfoCell(parent.getContext());
                cell.setClickable(true);
                cell.setBackgroundResource(R.drawable.list_selector);
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 5) {
                OrderStoreCell cell = new OrderStoreCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 6) {
                OrderGoodsCell cell = new OrderGoodsCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 7) {
                OrderInfoCell cell = new OrderInfoCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            } else if (viewType == 8) {
                ActionCell cell = new ActionCell(parent.getContext());
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new Holder(cell);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            int viewType = getItemViewType(position);
            if (viewType == 1) {
                H3HeaderCell cell = (H3HeaderCell) holder.itemView;
                cell.setTextSize(18);
                cell.setTextColor(ResourcesConfig.primaryColor);
                if (position == addressSection1) {
                    cell.setText("送货地址");
                } else if (position == orderPayTypeSection1) {
                    cell.setText("付款信息");
                } else if (position == goodsSection1) {
                    cell.setText("商品清单");
                } else if (position == orderInfoSection1) {
                    cell.setText("订单信息");
                }
            } else if (viewType == 4) {
                TextDetailInfoCell cell = (TextDetailInfoCell) holder.itemView;
                cell.setTextSize(16);
                cell.setValueSize(16);
                cell.setValueTextColor(0xff8a8a8a);
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapterDelegate != null) {
                            adapterDelegate.onItemSelect(position);
                        }
                    }
                });
                if (position == addressRow) {
                    cell.setValueTextColor(0xff212121);
                    if (addressInfo.size() > 0) {
                        SpannableStringBuilder name = new SpannableStringBuilder();
                        SpannableString user = new SpannableString(addressInfo.get("USER"));
                        user.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, user.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        name.append(user);
                        name.append(" ");
                        SpannableString phone = new SpannableString(addressInfo.get("PHONE"));
                        phone.setSpan(new ForegroundColorSpan(0xff2baf2b), 0, phone.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        phone.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, phone.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        name.append(phone);

                        StringBuilder address = new StringBuilder();
                        String temp = addressInfo.get("PROVINCENAME");
                        if (!TextUtils.isEmpty(temp)) {
                            address.append(temp);
                        }
                        temp = addressInfo.get("CITYNAME");
                        if (!TextUtils.isEmpty(temp)) {
                            address.append("-");
                            address.append(temp);
                        }
                        temp = addressInfo.get("REGIONNAME");
                        if (!TextUtils.isEmpty(temp)) {
                            address.append("-");
                            address.append(temp);
                        }
                        temp = addressInfo.get("ADDRESS");
                        if (!TextUtils.isEmpty(temp)) {
                            address.append("-");
                            address.append(temp);
                        }
                        cell.setTextAndValue(name, address, true, false);
                    } else {
                        cell.setTextAndValue("", "点击选择送货地址", true, false);
                    }
                } else if (position == orderPayTypeRow) {
                    String payAndDelivery = String.format("%s (%s)", OrderPayTypeActivity.payType[selectPayType], OrderPayTypeActivity.deliveryType[selectDeliveryType]);
                    cell.setTextAndValue("付款与配送方式", payAndDelivery, true, true);
                } else if (position == couponRow) {
                    cell.setTextAndValue("优惠券", "点击选择优惠券", true, true);
                } else if (position == invoiceRow) {
                    if (TextUtils.isEmpty(orderInvoice)) {
                        cell.setTextAndValue("发票信息", "点击填写发票抬头", true, false);
                    } else {
                        cell.setTextAndValue("发票信息", String.format("个人(%s)", orderInvoice), true, false);
                    }
                }
            } else if (viewType == 5) {
                OrderStoreCell cell = (OrderStoreCell) holder.itemView;
                int itemIndex = position - goodsBeginRow;
                OrderStoreItem item = (OrderStoreItem) orderItems.get(itemIndex);
                cell.setValue(item.shopName, true);
            } else if (viewType == 6) {
                OrderGoodsCell cell = (OrderGoodsCell) holder.itemView;
                int itemIndex = position - goodsBeginRow;
                OrderGoodsItem item = (OrderGoodsItem) orderItems.get(itemIndex);
                int goodsIndex = position - item.storePosition - goodsBeginRow - 1;
                ShoppingCartDataEntity entity = needCommitGoods.get(item.shopID).get(goodsIndex);
                String iconPath = entity.getIcon();
                String name = entity.getName();
                String desc = String.format("规格:%s", entity.getSpec());
                BigDecimal userPrice = entity.getUserPrice();
                int count = entity.getBuyCount();
                cell.setValue(iconPath, name, desc, userPrice, count, true);
            } else if (viewType == 7) {
                OrderInfoCell cell = (OrderInfoCell) holder.itemView;
                String deliveryType = OrderPayTypeActivity.deliveryType[selectDeliveryType];

                String name = "";
                String address = "";
                if (addressInfo.size() > 0) {
                    name = addressInfo.get("USER");
                    address = addressInfo.get("ADDRESS");
                }
                cell.setValue(deliveryType, name, address, goodsAmount, couponAmount);
            } else if (viewType == 8) {
                ActionCell cell = (ActionCell) holder.itemView;
                cell.setValue("提交订单");
                cell.setClickable(true);
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapterDelegate != null) {
                            adapterDelegate.onItemSelect(position);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == addressSection1 || position == orderPayTypeSection1 || position == goodsSection1 || position == orderInfoSection1) {
                return 1;
            } else if (position == orderPayTypeSection || position == goodsSection || position == orderInfoSection) {
                return 2;
            } else if (position == addressLoadingRow) {
                return 3;
            } else if (position == addressRow || position == orderPayTypeRow || position == couponRow || position == invoiceRow) {
                return 4;
            } else if (position >= goodsBeginRow && position <= goodsEndRow) {
                int itemIndex = position - goodsBeginRow;
                return orderItems.get(itemIndex).getItemViewType();
            } else if (position == orderInfoRow) {
                return 7;
            } else if (position == orderSubmitRow) {
                return 8;
            }
            return 0;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }
    }

    protected abstract boolean supportMedicareCardPay();

    static class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 购物车店铺行UI
     */
    private static class OrderStoreItem extends OrderItem {
        public final String shopID;
        public final String shopName;
        private int start = 0;
        private int count = 0;

        public OrderStoreItem(String id, String name) {
            this.shopID = id;
            this.shopName = name;
        }

        @Override
        public int getItemViewType() {
            return 5;
        }

        @Override
        public String getKey() {
            return shopID;
        }

        public void setStart(int position) {
            start = position + 1;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    /**
     * 购物车商品行UI
     */
    private static class OrderGoodsItem extends OrderItem {
        public final String shopID;
        public final String goodsID;
        public final int storePosition;

        public OrderGoodsItem(String goodsID, String shopID, int storePosition) {
            this.shopID = shopID;
            this.goodsID = goodsID;
            this.storePosition = storePosition;
        }

        @Override
        public int getItemViewType() {
            return 6;
        }

        @Override
        public String getKey() {
            return goodsID;
        }
    }

    private static abstract class OrderItem extends ShoppingCartFragment.ShoppingCartItem {

    }
}