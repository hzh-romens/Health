package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;

import com.romens.yjk.health.model.GoodSpicsEntity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADHorizontalScrollCell;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by AUSU on 2015/10/15.
 */
public class ADHorizontalScrollControl extends ADBaseControl{

    private  static List<GoodSpicsEntity> mResult=new ArrayList<GoodSpicsEntity>();
    public ADHorizontalScrollControl bindModle(List<GoodSpicsEntity> result){
        if(mResult!=null){
            mResult.clear();
        }
         mResult.addAll(result);
        return this;
    }
    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        ADHorizontalScrollCell adHorizontalScrollCell= (ADHorizontalScrollCell) holder.itemView;
        adHorizontalScrollCell.setEntity(mResult);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        adHorizontalScrollCell.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
    public static ADHolder createViewHolder(Context context) {
        ADHorizontalScrollCell adHorizontalScrollCell=new ADHorizontalScrollCell(context,mResult);
        return new ADHolder(adHorizontalScrollCell);
    }

    @Override
    public int getType() {
        return 13;
    }
}
