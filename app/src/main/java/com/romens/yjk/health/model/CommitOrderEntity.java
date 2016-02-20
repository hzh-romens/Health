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


    private String COUPONGUID;// 优惠券GUID
    private String BILLNAME; //发票抬头名称

    public String getCOUPONGUID() {
        return COUPONGUID;
    }

    public void setCOUPONGUID(String COUPONGUID) {
        this.COUPONGUID = COUPONGUID;
    }

    public String getBILLNAME() {
        return BILLNAME;
    }

    public void setBILLNAME(String BILLNAME) {
        this.BILLNAME = BILLNAME;
    }

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
