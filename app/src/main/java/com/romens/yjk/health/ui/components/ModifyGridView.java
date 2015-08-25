package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.romens.android.AndroidUtilities;


/**
 * Created by romens007 on 2015/8/18.
 */
public class ModifyGridView extends GridView {
    private int counts=3;
    public ModifyGridView(Context context) {
        super(context);
    }

    public ModifyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModifyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = measureWidth/counts+ AndroidUtilities.dp(32)+counts*AndroidUtilities.dp(16) ;
        int count = getChildCount();
        int yCount=0;
        if(count%counts==0){
            yCount= count /counts;
        }else {
            yCount=(count/counts)+1;
        }
        if(yCount!=0) {
            for (int index = 0; index < count; index++) {
                final View child = getChildAt(index);
                if (child.getVisibility() != GONE) {
                    measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((measureHeight - getPaddingTop() - getPaddingBottom()), MeasureSpec.EXACTLY));

                }
            }

            setMeasuredDimension(measureWidth, measureHeight * yCount);
        }else{
            setMeasuredDimension(measureWidth, measureHeight);
        }
    }
}
