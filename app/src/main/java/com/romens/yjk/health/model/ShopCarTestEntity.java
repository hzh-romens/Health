package com.romens.yjk.health.model;

/**
 * Created by romens007 on 2015/8/19.
 * 用于测试的bean
 */
public class ShopCarTestEntity {
    private int Type;
    private String json;
    private String imageUrl;
    private String infor;
    private Boolean check;
    private String price;

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInfor() {
        return infor;
    }

    public void setInfor(String infor) {
        this.infor = infor;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
    public ShopCarTestEntity(int type, String json, String imageUrl, String infor,boolean check,String price){
        this.Type=type;
        this.json=json;
        this.imageUrl=imageUrl;
        this.infor=infor;
        this.check=check;
        this.price=price;
    }
}
