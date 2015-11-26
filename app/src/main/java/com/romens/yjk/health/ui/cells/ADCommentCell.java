package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;


/**
 * Created by HZH on 2015/11/26.
 */
public class ADCommentCell extends FrameLayout{
    private TextView tv_comment,tv_name,tv_time;
    private FlexibleRatingBar ratingBar;
    public ADCommentCell(Context context) {
        super(context);
        View view=View.inflate(context,R.layout.list_item_comment,null);
        tv_comment= (TextView) view.findViewById(R.id.tv_comment);
        tv_name= (TextView) view.findViewById(R.id.tv_name);
        tv_time= (TextView) view.findViewById(R.id.tv_time);
        ratingBar= (FlexibleRatingBar) view.findViewById(R.id.ratingbar);
        ratingBar.setDesiredCount(30);
        addView(view, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT));
    }
    public void SetValue(String level,String commnet,String name,String time){
        tv_time.setText(time);
        tv_name.setText(name);
        tv_comment.setText(commnet);
        ratingBar.setRating(Float.parseFloat(level));
    }
}
