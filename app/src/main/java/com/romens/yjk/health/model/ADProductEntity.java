package com.romens.yjk.health.model;

/**
 * Created by siery on 15/8/14.
 */
public class ADProductEntity {
    public final String id;
    public final String icon;
    public final String name;
    public final String price;

    public ADProductEntity(String _id, String _icon, String _name, String _price) {
        this.id = _id;
        this.icon = _icon;
        this.name = _name;
        this.price = _price;
    }
}
