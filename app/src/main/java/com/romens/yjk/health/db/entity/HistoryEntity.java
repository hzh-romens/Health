package com.romens.yjk.health.db.entity;

/**
 * Created by AUSU on 2015/10/16.
 */
public class HistoryEntity {
    private long id;
    private String shopName;
    private String imgUrl;
    private boolean isSelect;
    private String medicinalName;
    private String currentPrice;
    private String discountPrice;
    private String saleCount;
    private String commentCount;
    private String guid;
    private String shopIp;

    public String getShopIp() {
        return shopIp;
    }

    public void setShopIp(String shopIp) {
        this.shopIp = shopIp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getMedicinalName() {
        return medicinalName;
    }

    public void setMedicinalName(String medicinalName) {
        this.medicinalName = medicinalName;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(String saleCount) {
        this.saleCount = saleCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
