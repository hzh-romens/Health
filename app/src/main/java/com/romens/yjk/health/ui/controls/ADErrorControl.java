package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.romens.yjk.health.ui.cells.ADErrorDataCell;
import com.romens.yjk.health.ui.cells.ADHolder;

/**
 * Created by HZH on 2015/10/22.
 */
public class ADErrorControl extends ADBaseControl{
    private String errorMsg;
    public ADErrorControl bindModel(String msg){
        errorMsg=msg;
        return this;
    }

    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        ADErrorDataCell adErrorDataCell= (ADErrorDataCell) holder.itemView;
        adErrorDataCell.setValue(errorMsg);

    }
    public static ADHolder createViewHolder(Context context) {
        ADErrorDataCell adErrorDataCell=new ADErrorDataCell(context);
        adErrorDataCell.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ADHolder(adErrorDataCell);
    }

    @Override
    public int getType() {
        return -2;
    }
}
