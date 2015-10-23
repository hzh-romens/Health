package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.content.Intent;

import com.romens.yjk.health.ui.cells.ADErrorDataCell;
import com.romens.yjk.health.ui.cells.ADHolder;

/**
 * Created by HZH on 2015/10/22.
 */
public class ADErrorControl extends ADBaseControl{

    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        ADErrorDataCell adErrorDataCell= (ADErrorDataCell) holder.itemView;

    }
    public static ADHolder createViewHolder(Context context) {
        ADErrorDataCell adErrorDataCell=new ADErrorDataCell(context);
        return new ADHolder(adErrorDataCell);
    }

    @Override
    public int getType() {
        return -2;
    }
}
