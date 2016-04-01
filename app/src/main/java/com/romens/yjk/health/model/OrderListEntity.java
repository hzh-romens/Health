package com.romens.yjk.health.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by anlc on 2015/9/25.
 */
public class OrderListEntity {

    private String orderId;
    private String orderNo;
    private String createTime;
    private BigDecimal orderPrice;
    private BigDecimal payPrice ;
    private BigDecimal couponPrice ;
    private String receiver;
    private String address;
    private String telephone;
    private String deliverType;
    private String orderStatus;
    private String orderStatusStr;
    private String payType;

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = new BigDecimal(orderPrice);
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDeliverType() {
        return deliverType;
    }

    public void setDeliverType(String deliverType) {
        this.deliverType = deliverType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatusStr() {
        return orderStatusStr;
    }

    public void setOrderStatusStr(String orderStatusStr) {
        this.orderStatusStr = orderStatusStr;
    }

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(double payPrice) {
        this.payPrice = new BigDecimal(payPrice);
    }

    public BigDecimal getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(double couponPrice) {
        this.couponPrice =new BigDecimal(couponPrice);
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
