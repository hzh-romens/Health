package com.romens.yjk.health.db.entity;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;

/**
 * Created by anlc on 2015/9/22.
 * 订单的实体
 */
public class AllOrderEntity implements Serializable {

    private String orderId;
    private String orderStatus;
    private String orderStatuster;
    private String goodsName;
    private String orderPrice;
    private String createDate;
    private String merCount;
    private String orderNo;
    private String picSmall;

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getMerCount() {
        return merCount;
    }

    public void setMerCount(String merCount) {
        this.merCount = merCount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderStatuster() {
        return orderStatuster;
    }

    public void setOrderStatuster(String orderStatuster) {
        this.orderStatuster = orderStatuster;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getPicSmall() {
        return picSmall;
    }

    public void setPicSmall(String picSmall) {
        this.picSmall = picSmall;
    }

    public static AllOrderEntity mapToEntity(LinkedTreeMap<String, String> item) {
        AllOrderEntity entity = new AllOrderEntity();
        entity.setOrderId(item.get("ORDERID"));
        entity.setOrderStatus(item.get("ORDERSTATUS"));
        entity.setOrderStatuster(item.get("ORDERSTATUSSTR"));
        entity.setGoodsName(item.get("MEDICINENAME"));
        entity.setOrderPrice(item.get("ORDERPRICE"));
        entity.setCreateDate(item.get("CREATEDATE"));
        entity.setMerCount(item.get("MERCOUNT"));
        entity.setOrderNo(item.get("ORDERNO"));
        entity.setPicSmall(item.get("PICSMALL"));
        return entity;
    }
}
