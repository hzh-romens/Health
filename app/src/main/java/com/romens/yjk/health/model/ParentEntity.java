package com.romens.yjk.health.model;

import java.io.Serializable;

/**
 * Created by AUSU on 2015/9/19.
 */
public class ParentEntity implements Serializable{
    private String check;
    private String shopID;
    private String shopName;


    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
