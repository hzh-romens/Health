package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADIllustrationCell;
import com.romens.yjk.health.ui.components.logger.Log;

/**
 * Created by AUSU on 2015/10/14.
 */
public class ADIllustrationControl extends ADBaseControl{
    //将优惠的信息传递过来（正品保证，免运费，货到付款）
    private String text1,text2,text3;
    public  ADIllustrationControl bindModel(String value,String value2,String value3){
        text1=value;
        text2=value2;
        text3=value3;
        return this;
    }

    @Override
    public void bindViewHolder(final Context context, ADHolder holder) {
       ADIllustrationCell adIllustrationCell= (ADIllustrationCell) holder.itemView;
        adIllustrationCell.setValue(text1,text2,text3);
        adIllustrationCell.ItemClickListener(new ADIllustrationCell.ItemClickBack() {
            @Override
            public void ToIllustration() {
                //跳转到药品说明页面
                Toast.makeText(context, "跳转到药品说明页面", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static ADHolder createViewHolder(Context context){
        ADIllustrationCell adIllustrationCell=new ADIllustrationCell(context);
        return new ADHolder(adIllustrationCell);
    }

    @Override
    public int getType() {
        return 11;
    }
}
