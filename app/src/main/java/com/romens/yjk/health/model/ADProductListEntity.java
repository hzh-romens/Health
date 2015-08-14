package com.romens.yjk.health.model;

import android.util.SparseArray;

/**
 * Created by siery on 15/8/14.
 */
public class ADProductListEntity {
    public final String adId;
    public final String name;
    public final String adBackground;
    public final SparseArray<ADProductEntity> productLis = new SparseArray<>();

    public ADProductListEntity(String _adId, String _name, String _adBackground) {
        this.adId = _adId;
        this.name = _name;
        this.adBackground = _adBackground;
    }

    public ADProductListEntity addProductEntity(int key, ADProductEntity entity) {
        productLis.append(key, entity);
        return this;
    }
}
