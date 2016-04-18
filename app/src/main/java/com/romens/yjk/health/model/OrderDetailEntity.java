package com.romens.yjk.health.model;

import android.text.TextUtils;
import android.util.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.io.json.JacksonMapper;
import com.romens.yjk.health.common.GoodsFlag;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.pay.PayActivity;
import com.romens.yjk.health.pay.PayMode;
import com.romens.yjk.health.pay.PayModeEnum;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/9/25.
 */
public class OrderDetailEntity {

    public final List<GoodsListEntity> goodsListEntities = new ArrayList<>();
    public final String orderId;
    public final String orderNo;
    public final String createTime;

    public final BigDecimal orderPrice;
    public final BigDecimal payPrice;
    public final BigDecimal couponPrice;
    public final BigDecimal shippingAmount;

    public final String receiver;
    public final String address;
    public final String telephone;
    public final String deliverType;
    public final String orderStatus;
    public final String orderStatusStr;
    public final String payType;

    private final String payMode;
    public final List<Pair<CharSequence, CharSequence>> payResult = new ArrayList<>();

    public final boolean supportOtherPay;

    //{"ORDER_ID":"8ce7c1eb29b0442a9b76f2ffadd22f41",
    // "ORDERNO":"20160412172801461",
    // "CREATETIME":"2016-04-12 05:28:01",
    // "TRANSPORTAMOUNT":"6",
    // "ORDERPRICE":"21.8",
    // "COUPONPRICE":"0",
    // "PAYPRICE":"27.8",
    // "RECEIVER":"周忠奎",
    // "ADDRESS":"山东省青岛市市南区宁夏路288号市南软件园",
    // "TELEPHONE":"18561606920",
    // "deliveryType":"55b30f7d31f2f",
    // "DELIVERYTYPE":"药店派送",
    // "PAYTYPE":"PAY_ONLINE",
    // "orderStatus":"3",
    // "ORDERSTATUSSTR":"已付款",
    // "GOODSLIST":[
    // {"GOODSGUID":"60641",
    // "BUYCOUNT":"1",
    // "NAME":"慢严舒柠复方青橄榄利咽含片",
    // "CODE":"6937343170296",
    // "GOODURL":"http://files.yunuo365.com/images/conew_60641_small.jpg",
    // "GOODSBIGURL":"http://files.yunuo365.com/images/conew_60641_small.jpg",
    // "DETAILDESCRIPTION":"",
    // "SPEC":"0.5gX8TX4袋",
    // "GOODSSORTGUID":"100",
    // "GOODSPRICE":"21.80",
    // "medicineId":"60641",
    // "SHOPID":"16650218-B89F-4EB2-BD24-0EEA683BAB8E",
    // "SHOPNAME":"人民同泰总部"}]}
    public OrderDetailEntity(JsonNode jsonNode) {
        orderId = jsonNode.get("ORDER_ID").asText();
        orderNo = jsonNode.get("ORDERNO").asText();
        createTime = jsonNode.get("CREATETIME").asText();
        double orderPriceValue = jsonNode.get("ORDERPRICE").asDouble();
        orderPrice = new BigDecimal(orderPriceValue);

        double couponPriceValue = jsonNode.get("COUPONPRICE").asDouble();
        couponPrice = new BigDecimal(couponPriceValue);

        double payPriceValue = jsonNode.get("PAYPRICE").asDouble();
        payPrice = new BigDecimal(payPriceValue);

        double shippingAmountValue = jsonNode.get("TRANSPORTAMOUNT").asDouble();
        shippingAmount = new BigDecimal(shippingAmountValue);

        receiver = jsonNode.get("RECEIVER").asText();
        address = jsonNode.get("ADDRESS").asText();
        deliverType = jsonNode.get("DELIVERYTYPE").asText();
        orderStatus = jsonNode.get("orderStatus").asText();
        orderStatusStr = jsonNode.get("ORDERSTATUSSTR").asText();
        telephone = jsonNode.get("TELEPHONE").asText();
        payType = jsonNode.get("PAYTYPE").asText();

        payMode = jsonNode.get("PAYMENT").asText();
        String payResultString = jsonNode.get("PAYRESULT").asText();
        formatPayResult(payResultString);

        if (jsonNode.has("SUPPORT_OTHER_PAY")) {
            supportOtherPay = TextUtils.equals("1", jsonNode.get("SUPPORT_OTHER_PAY").asText());
        }else{
            supportOtherPay=false;
        }

        JsonNode array = jsonNode.get("GOODSLIST");
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
            goodsEntity.setGoodsType(subObjcet.get("GOODSTYPE").asInt(GoodsFlag.NORMAL));
            goodsListEntities.add(goodsEntity);
        }
    }

    private void formatPayResult(String payResultString) {
        payResult.clear();
        try {
            JsonNode jsonNode = JacksonMapper.getInstance().readTree(payResultString);
            int size = jsonNode == null ? 0 : jsonNode.size();
            if (size > 0) {
                PayModeEnum payModeEnum = getPayMode();
                if (payModeEnum == PayModeEnum.YB_HEB) {
                    payResult.add(new Pair<CharSequence, CharSequence>("姓名", jsonNode.get("custname").asText()));
                    payResult.add(new Pair<CharSequence, CharSequence>("社保卡号码", ShoppingHelper.mixedString(jsonNode.get("cardNo").asText())));
                    payResult.add(new Pair<CharSequence, CharSequence>("身份证号码", ShoppingHelper.mixedString(jsonNode.get("certNo").asText())));
                } else if (payModeEnum == PayModeEnum.ALIPAY) {
                    payResult.add(new Pair<CharSequence, CharSequence>("支付宝交易流水号", jsonNode.get("trade_no").asText()));
                } else if (payModeEnum == PayModeEnum.WX) {
                    payResult.add(new Pair<CharSequence, CharSequence>("微信交易流水号", jsonNode.get("prepayId").asText()));
                }
            }
        } catch (IOException e) {

        }
    }

    public String getPayModeDesc() {
        return PayMode.getPayModeDesc(payMode);
    }

    public PayModeEnum getPayMode() {
        return PayMode.getPayMode(payMode);
    }


//    public static OrderListEntity mapToEntity(LinkedTreeMap<String, String> item) {
//        OrderListEntity entity = new OrderListEntity();
//        entity.setOrderId(item.get("ORDERID"));
//        entity.setOrderNo(item.get("ORDERNO"));
//        entity.setCreateTime(item.get("CREATETIME"));
//        entity.setOrderPrice(item.get("ORDERPRICE"));
//        entity.setPayPrice(item.get("PAYPRICE"));
//        entity.setCouponPrice(item.get("COUPONPRICE"));
//        entity.setReceiver(item.get("RECEIVER"));
//        entity.setAddress(item.get("ADDRESS"));
//        entity.setTelephone(item.get("TELEPHONE"));
//        entity.setDeliverType(item.get("DELIVERYTYPE"));
//        entity.setOrderStatus(item.get("ORDERSTATUS"));
//        entity.setOrderStatusStr(item.get("ORDERSTATUSSTR"));
//        String goodsList = item.get("GOODSLIST");
//        Gson gson = new Gson();
//        List<GoodsListEntity> tempGoodsListEntity = (List<GoodsListEntity>) gson.fromJson(goodsList, GoodsListEntity.class);
//        Log.e("tag", "--->" + gson.toJson(tempGoodsListEntity));
//        return null;
//    }
}
