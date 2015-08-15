package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.ADGroupCell;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.components.AttachView;

/**
 * Created by siery on 15/8/14.
 */
public class ADGroupControl extends ADBaseControl {
    private String groupName;
    private boolean groupMoreEnable;
    private int groupMoreResId;

    public ADGroupControl bindModel(String name, boolean moreEnable, int moreResId) {
        groupName = name;
        groupMoreEnable = moreEnable;
        groupMoreResId = moreResId;
        return this;
    }

    public static ADHolder createViewHolder(Context context) {
        ADGroupCell adGroupCell = new ADGroupCell(context);
        adGroupCell.setBackgroundResource(R.drawable.list_selector);
        adGroupCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        return new ADHolder(adGroupCell);
    }

    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        ADGroupCell cell = (ADGroupCell) holder.itemView;
        cell.setName(groupName);
        cell.setMoreButton(groupMoreEnable, groupMoreResId);
    }

    @Override
    public int getType() {
        return ControlType.TYPE_AD_GROUP;
    }
}
