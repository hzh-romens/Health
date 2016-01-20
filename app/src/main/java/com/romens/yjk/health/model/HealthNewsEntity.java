package com.romens.yjk.health.model;

/**
 * Created by siery on 15/8/26.
 */
public class HealthNewsEntity {
    public final String id;
    public final String iconUrl;
    public final String title;
    public final String content;

    private String value;

    public HealthNewsEntity(String _id, String _iconUrl, String title, String _content) {
        this.id = _id;
        this.iconUrl = _iconUrl;
        this.title = title;
        this.content = _content;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
