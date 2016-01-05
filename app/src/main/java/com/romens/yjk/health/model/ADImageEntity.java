package com.romens.yjk.health.model;

/**
 * Created by siery on 15/8/14.
 */
public class ADImageEntity {
    public final String id;
    public final String iconValue;
    public final String value;

    private int type;
    private String action;

    public ADImageEntity(String id, String icon, String value) {
        this.id = id;
        this.iconValue = icon;
        this.value = value;
    }

    public void setType(int _type) {
        type = _type;
    }

    public int getType() {
        return type;
    }

    public void setAction(String _action) {
        action = _action;
    }

    public String getAction() {
        return action;
    }
}
