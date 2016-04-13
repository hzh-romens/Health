package com.romens.yjk.health.model;

import com.romens.yjk.health.common.GoodsFlag;

/**
 * Created by anlc on 2015/9/25.
 * 药品详情中的列表的实体类
 */
public class GoodsListEntity {
    private String goodsGuid;
    private String buyCount;
    private String goodsPrice;
    private String name;
    private String goodsClassName;
    private String code;
    private String goodsUrl;
    private String detailDescitption;
    private String spec;
    private String goodsSortGuid;
    private String shopId;
    private String shopName;
    private boolean isSelect;

    private int goodsType = GoodsFlag.NORMAL;

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getGoodsGuid() {
        return goodsGuid;
    }

    public void setGoodsGuid(String goodsGuid) {
        this.goodsGuid = goodsGuid;
    }

    public String getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(String buyCount) {
        this.buyCount = buyCount;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoodsClassName() {
        return goodsClassName;
    }

    public void setGoodsClassName(String goodsClassName) {
        this.goodsClassName = goodsClassName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGoodsUrl() {
        return goodsUrl;
    }

    public void setGoodsUrl(String goodsUrl) {
        this.goodsUrl = goodsUrl;
    }

    public String getDetailDescitption() {
        return detailDescitption;
    }

    public void setDetailDescitption(String detailDescitption) {
        this.detailDescitption = detailDescitption;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getGoodsSortGuid() {
        return goodsSortGuid;
    }

    public void setGoodsSortGuid(String goodsSortGuid) {
        this.goodsSortGuid = goodsSortGuid;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setGoodsType(int goodsType) {
        this.goodsType = goodsType;
    }

    public int getGoodsType() {
        return goodsType;
    }
}
