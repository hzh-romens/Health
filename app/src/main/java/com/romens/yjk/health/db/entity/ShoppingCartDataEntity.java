package com.romens.yjk.health.db.entity;

/**
 * @author Zhou Lisi
 * @create 16/2/27
 * @description
 */
public class ShoppingCartDataEntity {
    public static final String ClassId = "com.romens.yjk.health.db.entity.ShoppingCartEntity";

    private String id;
    private Long parent;
    private int buyCount;//数量
    private Double goodsPrice;// 商品价格
    private String createTime;// 日期
    private String name; //商品名称
    private String goodsClassName; //商品分类名称
    private String goodsURL; //商品图片url
    private String code;// 商品code
    private String detailDesc;// 商品描述
    private String spen;// 商品规格
    private String shopID;// 药店id
    private String shopName;//药店名称
    private String goodsGuid;//药品ID
    private String userGuid;
    private Long createdTime;
    private Long updatedTime;

    public ShoppingCartDataEntity() {

    }

    public String getGuid() {
        return id;
    }

    public long getParent() {
        return parent;
    }

    public long getCrateTime() {
        return createdTime;
    }

    public long getUpdateTime() {
        return updatedTime;
    }

}
