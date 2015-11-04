package com.romens.yjk.health.db.entity;

/**
 * Created by siery on 15/10/30.
 */
public class DataCacheEntity {
    private String cacheKey;
    private long cacheUpdated;
    private int cacheValidity;

    public DataCacheEntity() {

    }


    public String getCacheKey() {
        return this.cacheKey;
    }

    public long getCacheUpdated() {
        return cacheUpdated;
    }

    public int getCacheValidity() {
        return cacheValidity;
    }


    public void setCacheKey(String key) {
        this.cacheKey = key;
    }

    public void setCacheUpdated(long time) {
        this.cacheUpdated = time;
    }

    public void setCacheValidity(int validity) {
        this.cacheValidity = validity;
    }


}
