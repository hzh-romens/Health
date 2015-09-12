package com.romens.yjk.health.model;

/**
 * Created by siery on 15/8/14.
 */
public class ADFunctionEntity {
    public final String id;
    public final CharSequence name;
    public final int iconResId;
    private String actionValue;

    public ADFunctionEntity(String _id, CharSequence _name, int _iconResId) {
        this.id = _id;
        this.name = _name;
        this.iconResId = _iconResId;
    }

    public void setActionValue(String value) {
        actionValue = value;
    }
}
