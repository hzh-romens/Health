package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.EmptyCell;
import com.romens.yjk.health.ui.cells.ADHolder;

/**
 * Created by siery on 15/8/14.
 */
public class ADEmptyControl extends ADBaseControl {
    public static final int TYPE = -1;

    public static ADHolder createViewHolder(Context context) {
        EmptyCell emptyCell = new EmptyCell(context);
        emptyCell.setHeight(AndroidUtilities.dp(16));
        emptyCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        return new ADHolder(emptyCell);
    }

    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
