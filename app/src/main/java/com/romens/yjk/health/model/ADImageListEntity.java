package com.romens.yjk.health.model;

import android.util.SparseArray;

/**
 * Created by siery on 15/8/14.
 */
public class ADImageListEntity {
    public final SparseArray<ADImageListEntity> imageEntities = new SparseArray<>();

    public ADImageListEntity addEntity(int key, ADImageListEntity entity) {
        imageEntities.append(key, entity);
        return this;
    }
}
