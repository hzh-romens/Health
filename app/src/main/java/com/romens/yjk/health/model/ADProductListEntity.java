package com.romens.yjk.health.model;

import android.util.SparseArray;

/**
 * Created by siery on 15/8/14.
 */
public class ADProductListEntity {
    public final String adId;
    public final String name;
    public final String desc;
    public final String adBackground;
    private String layoutStyle;
    public final SparseArray<ADProductEntity> productLis = new SparseArray<>();

    public ADProductListEntity(String _adId, String _name, String _desc, String _adBackground) {
        this.adId = _adId;
        this.name = _name;
        this.desc = _desc;
        this.adBackground = _adBackground;
    }

    public ADProductListEntity addProductEntity(int key, ADProductEntity entity) {
        productLis.append(key, entity);
        return this;
    }

    public int size() {
        return productLis.size();
    }

    public ADProductEntity get(int position) {
        return productLis.get(position);
    }

    public void setLayoutStyle(String style) {
        layoutStyle = style;
    }

    public String getLayoutStyle() {
        return layoutStyle;
    }
}
