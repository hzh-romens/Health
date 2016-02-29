package com.romens.yjk.health.db.entity;

/**
 * @author Zhou Lisi
 * @create 16/2/29
 * @description 药店
 */
public class ShopEntity {
    public final String shopId;
    public final String shopName;

    public ShopEntity(String id, String name) {
        this.shopId = id;
        this.shopName = name;
    }
}
