package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.model.ADImageEntity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADOnlyImageCell;

/**
 * Created by siery on 15/8/14.
 */
public class ADImageControl extends ADBaseControl {
    public static final int TYPE=2;
    private ADImageEntity adImageEntity;

    public ADImageControl bindModel(ADImageEntity entity){
        adImageEntity=entity;
        return this;
    }

    public static ADHolder createViewHolder(Context context) {
        ADOnlyImageCell adOnlyImageCell = new ADOnlyImageCell(context);
        adOnlyImageCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(100)));
        return new ADHolder(adOnlyImageCell);
    }

    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        ADOnlyImageCell cell = (ADOnlyImageCell) holder.itemView;
        String imageUrl=adImageEntity==null?null:adImageEntity.value;
        cell.setImage(imageUrl, null, null);
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
