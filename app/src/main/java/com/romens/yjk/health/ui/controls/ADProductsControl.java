package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADProductsCell;

/**
 * Created by siery on 15/8/15.
 */
public class ADProductsControl extends ADBaseControl {
    public static ADHolder createViewHolder(Context context) {
        ADProductsCell cell = new ADProductsCell(context);
        cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        return new ADHolder(cell);
    }

    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        ADProductsCell cell = (ADProductsCell) holder.itemView;
    }

    @Override
    public int getType() {
        return ControlType.TYPE_AD_PRODUCTS;
    }
}
