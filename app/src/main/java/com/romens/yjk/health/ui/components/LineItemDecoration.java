package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Zhou Lisi
 * @create 16/3/10
 * @description
 */
public class LineItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint mDividerPaint = new Paint();
    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;
    private final boolean mNoCustomPadding;

    public LineItemDecoration(Context context) {
        mDividerPaint.setColor(0xffd9d9d9);
        mNoCustomPadding = false;
    }

    public LineItemDecoration(Context context, int paddingLeft, int paddingRight) {
        mDividerPaint.setColor(0xffd9d9d9);
        mPaddingLeft = paddingLeft;
        mPaddingRight = paddingRight;
        mNoCustomPadding = true;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            float y = child.getHeight() + child.getY();
            if (mNoCustomPadding) {
                c.drawLine(child.getX() + mPaddingLeft,
                        y,
                        child.getWidth() - mPaddingRight,
                        y,
                        mDividerPaint);
            } else {
                c.drawLine(child.getX() + child.getPaddingLeft(),
                        y,
                        child.getWidth() - child.getPaddingRight(),
                        y,
                        mDividerPaint);
            }
        }
    }
}