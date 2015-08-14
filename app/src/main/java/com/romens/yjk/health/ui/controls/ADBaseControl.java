package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.yjk.health.ui.cells.ADHolder;

/**
 * Created by siery on 15/8/14.
 */
public abstract class ADBaseControl{
    public abstract void bindViewHolder(Context context,ADHolder holder);
    public abstract int getType();
}
