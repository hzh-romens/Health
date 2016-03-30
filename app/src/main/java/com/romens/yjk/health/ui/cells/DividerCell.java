package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.View;

/**
 * @author Zhou Lisi
 * @create 2016-03-29 15:26
 * @description
 */
public class DividerCell extends View {

    public DividerCell(Context context) {
        super(context);
        setBackgroundColor(0xffd9d9d9);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(1, MeasureSpec.EXACTLY));
    }

}
