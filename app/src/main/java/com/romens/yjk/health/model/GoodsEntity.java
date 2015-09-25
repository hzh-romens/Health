package com.romens.yjk.health.model;

/**
 * Created by AUSU on 2015/9/21.
 */
public class GoodsEntity {
    private String GOODSGUID;     //商品ID
    private String BUYCOUNT;//购买数量

    public String getGOODSGUID() {
        return GOODSGUID;
    }

    public void setGOODSGUID(String GOODSGUID) {
        this.GOODSGUID = GOODSGUID;
    }

    public String getBUYCOUNT() {
        return BUYCOUNT;
    }

    public void setBUYCOUNT(String BUYCOUNT) {
        this.BUYCOUNT = BUYCOUNT;
    }
}
