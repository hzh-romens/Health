package com.romens.yjk.health.model;

/**
 * Created by siery on 15/8/14.
 */
public class ADImageEntity {
    public final String id;
    public final String value;

    private int type;
    private String action;

    public ADImageEntity(String _id, String _value) {
        this.id = _id;
        this.value = _value;
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
