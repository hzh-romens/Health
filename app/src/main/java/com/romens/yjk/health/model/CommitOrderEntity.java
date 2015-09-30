package com.romens.yjk.health.model;

import java.util.List;

/**
 * Created by AUSU on 2015/9/24.
 * 向服务器提交的订单数据
 */
public class CommitOrderEntity {
    private String ADDRESSID; //送货地址id
    private String DELIVERYTYPE;//送货方式
    private List<FilterChildEntity> GOODSLIST;

    public String getADDRESSID() {
        return ADDRESSID;
    }

    public void setADDRESSID(String ADDRESSID) {
        this.ADDRESSID = ADDRESSID;
    }

    public String getDELIVERYTYPE() {
        return DELIVERYTYPE;
    }

    public void setDELIVERYTYPE(String DELIVERYTYPE) {
        this.DELIVERYTYPE = DELIVERYTYPE;
    }

    public List<FilterChildEntity> getGOODSLIST() {
        return GOODSLIST;
    }

    public void setGOODSLIST(List<FilterChildEntity> GOODSLIST) {
        this.GOODSLIST = GOODSLIST;
    }
}
