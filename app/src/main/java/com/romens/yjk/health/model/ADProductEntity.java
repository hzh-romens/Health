package com.romens.yjk.health.model;

/**
 * Created by siery on 15/8/14.
 */
public class ADProductEntity {
    public final String id;
    public final String icon;
    public final String name;
    public final String price;
    public final String oldPrice;
    private String tag;

    public ADProductEntity(String _id, String _icon, String _name, String _oldPrice, String _price) {
        this.id = _id;
        this.icon = _icon;
        this.name = _name;
        this.oldPrice = _oldPrice;
        this.price = _price;
    }

    public void setTag(String _tag) {
        tag = _tag;
    }

    public String getTag() {
        return tag;
    }
}
