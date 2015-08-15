package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.NewsCell;

/**
 * Created by siery on 15/8/14.
 */
public class ADNewsControl extends ADBaseControl {
    private String info;
    private String iconUrl;
    private boolean needDivider=false;

    public ADNewsControl bindModel(String _info, String _iconUrl,boolean divider) {
        info = _info;
        iconUrl = _iconUrl;
        needDivider=divider;
        return this;
    }

    public static ADHolder createViewHolder(Context context) {
        NewsCell cell = new NewsCell(context);
        cell.setBackgroundResource(R.drawable.list_selector);
        cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        return new ADHolder(cell);
    }

    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        NewsCell cell = (NewsCell) holder.itemView;
        cell.setValue(info, iconUrl,needDivider);
    }

    @Override
    public int getType() {
        return ControlType.TYPE_AD_NEWS;
    }
}
