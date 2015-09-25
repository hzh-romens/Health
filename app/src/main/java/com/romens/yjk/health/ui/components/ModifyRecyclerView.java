package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.romens.android.AndroidUtilities;

/**
 * Created by romens007 on 2015/8/17.
 */
public class ModifyRecyclerView extends RecyclerView {
    public ModifyRecyclerView(Context context) {
        super(context);
    }

    public ModifyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModifyRecyclerView(Context context, AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int mode2 = MeasureSpec.getMode(heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() != View.GONE) {
                measureChild(childAt, MeasureSpec.makeMeasureSpec(width - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90), MeasureSpec.EXACTLY));
            }
        }

        super.onMeasure(widthMeasureSpec, AndroidUtilities.dp(90)+getPaddingBottom()+getPaddingTop());
    }

}
