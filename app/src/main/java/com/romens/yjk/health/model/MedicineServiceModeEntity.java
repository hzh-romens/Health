package com.romens.yjk.health.model;

/**
 * Created by siery on 15/12/16.
 */
public class MedicineServiceModeEntity {
    public final int icon;
    public final int color;
    public final String text;

    public MedicineServiceModeEntity(int icon, String text) {
        this(icon, 0, text);
    }

    public MedicineServiceModeEntity(int icon, int color, String text) {
        this.icon = icon;
        this.color = color;
        this.text = text;
    }
}
