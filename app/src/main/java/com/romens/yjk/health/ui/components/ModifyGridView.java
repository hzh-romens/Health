package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.romens.android.AndroidUtilities;
import com.squareup.okhttp.internal.Util;


/**
 * Created by romens007 on 2015/8/18.
 */
public class ModifyGridView extends GridView {
    private int counts;
    int verticalSpacing;
    int horizontalSpacing;
    int paddings;
    int measureHeight;
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
        if(getNumColumns()!=0){
            counts=getNumColumns();
        }
        if(getVerticalSpacing()!=0) {

            verticalSpacing = getVerticalSpacing();
        }
        if(getHorizontalSpacing()!=0) {

            horizontalSpacing = getHorizontalSpacing();
        }
        if(getPaddingBottom()!=0){
            paddings=getPaddingBottom();
        }

        int count = getChildCount();
        int yCount=0;

        if(count%counts==0){
            yCount= count /counts;
        }else {
            yCount=(count/counts)+1;
        }
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
      // int measureHeight = measureWidth/counts+counts*AndroidUtilities.dp(16)+yCount*verticalSpacing;
        Log.i("宽度=====",""+measureWidth);
       //int measureHeight=measureWidth/counts+AndroidUtilities.dp(48);
       // int measureHeight=MeasureSpec.getSize(heightMeasureSpec);
        if(MeasureSpec.getSize(heightMeasureSpec)!=0){
            Log.i("item高度----",""+MeasureSpec.getSize(heightMeasureSpec)+"---"+measureHeight);
            measureHeight=MeasureSpec.getSize(heightMeasureSpec);
        }
        if(yCount!=0) {
            for (int index = 0; index < count; index++) {
                final View child = getChildAt(index);
                if (child.getVisibility() != GONE) {
                    measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth - horizontalSpacing, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((measureHeight - verticalSpacing), MeasureSpec.EXACTLY));
                }
            }

          //  setMeasuredDimension(measureWidth,measureHeight+counts*AndroidUtilities.dp(16));
      //  }else{
            Log.i("行数------", "" + yCount);
         //   setMeasuredDimension(measureWidth,yCount*measureHeight+(yCount+1)*verticalSpacing);
            setMeasuredDimension(measureWidth,yCount*(getPaddingBottom()+verticalSpacing+measureHeight));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
