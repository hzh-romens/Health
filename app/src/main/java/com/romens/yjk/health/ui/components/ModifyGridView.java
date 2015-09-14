package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();//屏幕宽度
        int height = wm.getDefaultDisplay().getHeight();//屏幕高度
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
      // int measureHeight = measureWidth/counts+counts*AndroidUtilities.dp(16)+yCount*verticalSpacing;
       //int measureHeight=measureWidth/counts+AndroidUtilities.dp(48);
       // int measureHeight=MeasureSpec.getSize(heightMeasureSpec)
       // if(MeasureSpec.getSize(heightMeasureSpec)!=0){
         //   measureHeight=MeasureSpec.getSize(heightMeasureSpec);
        //}
        measureHeight=(int)(width/2.1)+AndroidUtilities.dp(8);
        Log.i("item高度-----",measureHeight+"");
        Log.i("行数",yCount+"");
        if(yCount!=0) {
            for (int index = 0; index < count; index++) {
                final View child = getChildAt(index);
                if (child.getVisibility() != GONE) {
                    measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth - horizontalSpacing, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((measureHeight -getPaddingBottom()), MeasureSpec.EXACTLY));
                }
            }
            setMeasuredDimension(measureWidth,yCount*measureHeight);
        }else{
            setMeasuredDimension(measureWidth,measureHeight);
        }
    }
}
