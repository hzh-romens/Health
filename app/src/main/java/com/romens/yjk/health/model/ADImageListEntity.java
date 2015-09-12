package com.romens.yjk.health.model;

import java.util.HashMap;

/**
 * Created by siery on 15/8/14.
 */
public class ADImageListEntity {
    public final HashMap<String, ADImageEntity> imageEntities = new HashMap<>();

    public ADImageListEntity addEntity(String key, ADImageEntity entity) {
        imageEntities.put(key, entity);
        return this;
    }

    public ADImageEntity get(String key) {
        return imageEntities.get(key);
    }

    public int size() {
        return imageEntities.size();
    }

}
