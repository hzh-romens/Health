package com.romens.yjk.health.db.entity;

import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siery on 15/12/17.
 */
public class FavoritesEntity {
    public static final String ClassId="com.romens.yjk.health.db.entity.FavoritesEntity";

    private String id;
    private String merchandiseId;//商品id
    private String medicineName;//商品名称
    private String medicineSpec;//商品规格
    private String shopId;//药店id
    private String shopName;//药店名称
    private String picBig;//药品大图
    private String picSmall;//药品小图
    private double price;//商品价格
    private double memberPrice;//会员价格
    private String assessCount;//评论总数
    private String saleCount;//销量

    private long updated;

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerchandiseId() {
        return merchandiseId;
    }

    public void setMerchandiseId(String merchandiseId) {
        this.merchandiseId = merchandiseId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineSpec() {
        return medicineSpec;
    }

    public void setMedicineSpec(String medicineSpec) {
        this.medicineSpec = medicineSpec;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getPicBig() {
        return picBig;
    }

    public void setPicBig(String picBig) {
        this.picBig = picBig;
    }

    public String getPicSmall() {
        return picSmall;
    }

    public void setPicSmall(String picSmall) {
        this.picSmall = picSmall;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(double memberPrice) {
        this.memberPrice = memberPrice;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAssessCount() {
        return assessCount;
    }

    public void setAssessCount(String assessCount) {
        this.assessCount = assessCount;
    }

    public String getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(String saleCount) {
        this.saleCount = saleCount;
    }

    public static FavoritesEntity jsonObjectToEntity(JSONObject jsonObject) throws JSONException {
        FavoritesEntity entity = new FavoritesEntity();
        entity.setId(jsonObject.getString("FID"));
        entity.setMerchandiseId(jsonObject.getString("MERCHANDISEID"));
        entity.setMedicineName(jsonObject.getString("MEDICINENAME"));
        entity.setMedicineSpec(jsonObject.getString("MEDICINESPEC"));
        entity.setShopId(jsonObject.getString("SHOPID"));
        entity.setShopName(jsonObject.getString("SHOPNAME"));
        entity.setPicBig(jsonObject.getString("PICBIG"));
        entity.setPicSmall(jsonObject.getString("PICSMALL"));
        entity.setPrice(jsonObject.getDouble("PRICE"));
        entity.setMemberPrice(jsonObject.getDouble("MEMBERPRICE"));
        entity.setAssessCount(jsonObject.getString("ASSESSCOUNT"));
        entity.setSaleCount(jsonObject.getString("SALECOUNT"));
        entity.setUpdated(jsonObject.getLong("CREATEDATE"));
        return entity;
    }

}
