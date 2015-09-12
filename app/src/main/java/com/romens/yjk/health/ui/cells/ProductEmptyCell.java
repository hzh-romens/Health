package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;

/**
 * Created by siery on 15/9/8.
 */
public class ProductEmptyCell extends FrameLayout {

    public ProductEmptyCell(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = measureWidth + AndroidUtilities.dp(64);
        setMeasuredDimension(measureWidth, measureHeight);
    }
}
