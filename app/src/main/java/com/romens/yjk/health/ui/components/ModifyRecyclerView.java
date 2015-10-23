package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.romens.android.AndroidUtilities;

/**
 * Created by romens007 on 2015/8/17.
 */
public class ModifyRecyclerView extends GridView {
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
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
