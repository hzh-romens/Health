package com.romens.yjk.health.model;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by siery on 15/8/26.
 */
public class HealthNewsEntity {
    public final String id;
    public final String content;
    public final String iconUrl;
    public final String value;

    public HealthNewsEntity(String _id, String _content,String _iconUrl, String _value) {
        this.id = _id;
        this.content = _content;
        this.iconUrl=_iconUrl;
        this.value = _value;
    }
}
