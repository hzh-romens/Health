package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.model.ADProductListEntity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADProductsCell;

/**
 * Created by siery on 15/8/15.
 */
public class ADProductsControl extends ADBaseControl {
    private ADProductListEntity entity;

    public ADProductsControl bindModel(ADProductListEntity _entity) {
        entity = _entity;
        return this;
    }

    public static ADHolder createViewHolder(Context context) {
        ADProductsCell cell = new ADProductsCell(context);
        cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 8, 4, 8, 8));
        return new ADHolder(cell);
    }

    @Override
    public void bindViewHolder(final Context context, ADHolder holder) {
        ADProductsCell cell = (ADProductsCell) holder.itemView;
        cell.setDelegate(new ADProductsCell.ADProductsDelegate() {
            @Override
            public void onGroupCellClick(Bundle arguments) {
                onActionForGroup(context, arguments);
            }

            @Override
            public void onChildCellClick(Bundle arguments) {
                onActionForProduct(context, arguments);
            }
        });
        cell.setValue(entity);
    }

    @Override
    public int getType() {
        return ControlType.TYPE_AD_PRODUCTS;
    }
}
