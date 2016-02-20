package com.romens.yjk.health.model;

/**
 * Created by AUSU on 2015/9/23.
 */
public class FilterChildEntity {
    private String GOODSGUID;//商品ID
    private String SHOPID;   //药店ID
    private String BUYCOUNT;   //商品数量
    private String GOODSPRICE;  //商品价格


    public String getGOODSGUID() {
        return GOODSGUID;
    }

    public void setGOODSGUID(String GOODSGUID) {
        this.GOODSGUID = GOODSGUID;
    }

    public String getSHOPID() {
        return SHOPID;
    }

    public void setSHOPID(String SHOPID) {
        this.SHOPID = SHOPID;
    }

    public String getBUYCOUNT() {
        return BUYCOUNT;
    }

    public void setBUYCOUNT(String BUYCOUNT) {
        this.BUYCOUNT = BUYCOUNT;
    }

    public String getGOODSPRICE() {
        return GOODSPRICE;
    }

    public void setGOODSPRICE(String GOODSPRICE) {
        this.GOODSPRICE = GOODSPRICE;
    }
}
