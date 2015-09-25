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
    private String goodsName;
    private String orderPrice;
    private String orderStatuster;

    private String drugStroe;

    public String getDrugStroe() {
        return drugStroe;
    }

    public void setDrugStroe(String drugStroe) {
        this.drugStroe = drugStroe;
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

    public static AllOrderEntity mapToEntity(LinkedTreeMap<String, String> item) {
        AllOrderEntity entity = new AllOrderEntity();
        entity.setOrderId(item.get("ORDERID"));
        entity.setGoodsName(item.get("GOODSNAME"));
        entity.setOrderPrice(item.get("ORDERPRICE"));
        entity.setOrderStatus(item.get("ORDERSTATUS"));
        entity.setOrderStatuster(item.get("ORDERSTATUSSTR"));
        return entity;
    }
}
