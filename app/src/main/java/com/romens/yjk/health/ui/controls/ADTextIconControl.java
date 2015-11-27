package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.ADHolder;

/**
 * Created by HZH on 2015/11/26.
 */
public class ADTextIconControl extends ADBaseControl{
    private String mName;
    private int mDrawable;
    private boolean mFlag;

    public ADTextIconControl bindModel(String name, int drawable, boolean flag){
        this.mDrawable=drawable;
        this.mFlag=flag;
        this.mName=name;
        return this;
    }
    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        TextSettingsCell textIconCell= (TextSettingsCell) holder.itemView;
        textIconCell.setTextAndIcon(mName, R.drawable.ic_chevron_right_grey600_24dp, true);

    }
    public static ADHolder createViewHolder(Context context) {
        TextSettingsCell textIconCell=new TextSettingsCell(context);
        return new ADHolder(textIconCell);
    }

    @Override
    public int getType() {
        return 15;
    }
}
