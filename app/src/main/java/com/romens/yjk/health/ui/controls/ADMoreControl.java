package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADMoreCell;

/**
 * Created by AUSU on 2015/10/14.
 */
public class ADMoreControl extends ADBaseControl{
    private String mValue;
    public  ADMoreControl bindModle(String value){
        this.mValue=value;
        return this;
    }
    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        ADMoreCell adMoreCell= (ADMoreCell) holder.itemView;
        adMoreCell.SetValue(mValue);
    }
    public static ADHolder createViewHolder(Context context) {
        ADMoreCell adMoreCell=new ADMoreCell(context);
        adMoreCell.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ADHolder(adMoreCell);
    }

    @Override
    public int getType() {
        return 9;
    }
}
