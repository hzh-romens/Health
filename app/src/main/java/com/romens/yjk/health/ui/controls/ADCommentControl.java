package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.yjk.health.ui.cells.ADCommentCell;
import com.romens.yjk.health.ui.cells.ADHolder;

/**
 * Created by HZH on 2015/11/26.
 */
public class ADCommentControl extends ADBaseControl{
    private String mLevel,mComment,mName,mTime;
    public  ADCommentControl bindModle(String level,String commnet,String name,String time){
        this.mComment=commnet;
        this.mLevel=level;
        this.mName=name;
        this.mTime=time;
        return this;
    }
    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        ADCommentCell adCommentCell= (ADCommentCell) holder.itemView;
        adCommentCell.SetValue(mLevel,mComment,mName,mTime);
        //绑定值

    }
    public static ADHolder createViewHolder(Context context){
        ADCommentCell adCommentCell=new ADCommentCell(context);
        return new ADHolder(adCommentCell);
    }

    @Override
    public int getType() {
        return 14;
    }
}
