package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;

/**
 * Created by AUSU on 2015/10/14.
 */
public class ADIllustrationCell extends FrameLayout{
    private RelativeLayout rl_illustration;
    //正品保证，免运费，货到付款三个标志
    private TextView tv1,tv2,tv3;
    private ItemClickBack mItemClickBack;
    public ADIllustrationCell(Context context) {
        super(context);
        FrameLayout container=new FrameLayout(context);
        container.setBackgroundColor(getResources().getColor(R.color.md_grey_300));
        View view=View.inflate(context, R.layout.list_item_illustration,null);
        rl_illustration= (RelativeLayout) view.findViewById(R.id.rl_illustration);
        tv1= (TextView) view.findViewById(R.id.tv1);
        tv2= (TextView) view.findViewById(R.id.tv2);
        tv3= (TextView) view.findViewById(R.id.tv3);
        rl_illustration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickBack != null) {
                    mItemClickBack.ToIllustration();
                }
            }
        });
        ShadowSectionCell shadowSectionCell=new ShadowSectionCell(context);
        container.addView(shadowSectionCell,LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(4), Gravity.BOTTOM));
        container.addView(view,LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP,0,0,0,AndroidUtilities.dp(4)));
        addView(container, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }
    //传递一个标志过来，判断三个TextView显示哪几个
    public void setValue(String text1,String text2,String text3){
        tv1.setText(text1);
        tv2.setText(text2);
        tv3.setText(text3);
    }
    public void ItemClickListener(ItemClickBack itemClickBack){
        this.mItemClickBack=itemClickBack;
    }
    public interface ItemClickBack{
        void ToIllustration();
    }
}
