package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.yjk.health.ui.cells.ADGroupNameCell;
import com.romens.yjk.health.ui.cells.ADHolder;

/**
 * Created by AUSU on 2015/10/15.
 */
public class ADGroupNameControls extends ADBaseControl{
    private String strOfGroupName;
    private boolean mFlag;
    public  ADGroupNameControls bindModel(String groupName,boolean flag) {
        strOfGroupName=groupName;
        mFlag=flag;
        return this;
    }
    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
       ADGroupNameCell adGroupNameCell= (ADGroupNameCell) holder.itemView;
        adGroupNameCell.setValue(strOfGroupName,mFlag);
    }
    public static ADHolder createViewHolder(Context context) {
        ADGroupNameCell adGroupNameCell=new ADGroupNameCell(context);
        return new ADHolder(adGroupNameCell);
    }

    @Override
    public int getType() {
        return 12;
    }
}
