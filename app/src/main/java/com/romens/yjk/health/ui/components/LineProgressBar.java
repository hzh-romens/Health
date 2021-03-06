package com.romens.yjk.health.ui.components;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.romens.yjk.health.R;

@SuppressLint("Recycle")
public class LineProgressBar extends View {
    private Paint paint;
    private int progress;
    private int max;
    private int start = 0;
    private int mWidth;
    private int mHeight;


    public LineProgressBar(Context context) {
        this(context, null);
    }

    public LineProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.LineProgressBar);
        //max = typedArray.getInteger(R.styleable.LineProgressBar_max, 100);
        typedArray.recycle();
    }

    @SuppressLint({"DrawAllocation", "ResourceAsColor"})
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(getResources().getColor(R.color.progress_bgc));
        paint.setAntiAlias(true);
        int round = 0;
        int mWidths = mWidth - 10;
        RectF progressBgc = new RectF(10, 0, mWidths, mHeight);
        canvas.drawRoundRect(progressBgc, round, round, paint);

        paint.setColor(getResources().getColor(R.color.themecolor));
        float current = (float) progress / max;
        RectF currentBgc = new RectF(10, 0, mWidths * current, mHeight);
        canvas.drawRoundRect(currentBgc, round, round, paint);

        if (progress == max) {
            paint.setColor(getResources().getColor(R.color.themecolor));

        } else {
            paint.setColor(getResources().getColor(R.color.progress_bgc));

        }
        RectF oval2 = new RectF(mWidths - 10, 0, mWidths + 10, mHeight);
        canvas.drawArc(oval2, 270, 180, true, paint);
        // 画开头那个小半圆
        if (progress == 0) {
            paint.setColor(getResources().getColor(R.color.progress_bgc));
        }
        if (progress != 0) {
            paint.setColor(getResources().getColor(R.color.themecolor));
        }

        RectF oval = new RectF(0, 0, 20, mHeight);
        canvas.drawArc(oval, 90, 180, true, paint);
        String percentValue = progress + "/" + max;
        Rect rect = new Rect();
        paint.setColor(Color.WHITE);
        paint.setTextSize(24);
        paint.getTextBounds(percentValue, 0, percentValue.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2);
        canvas.drawText(percentValue, x, y, paint);

    }

    public synchronized int getMax() {
        return max;
    }

    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY
                || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        if (heightSpecMode == MeasureSpec.EXACTLY
                || heightSpecMode == MeasureSpec.AT_MOST) {
            mHeight = dipToPx(10);

        } else {
            mHeight = heightSpecSize;
        }

    }

    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /***
     * 设置当前的进度值
     *
     * @param progress
     */

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        this.progress = progress > max ? max : progress;
        //invalidate();
        postInvalidate();
    }

    public synchronized int getProgress() {
        return progress;
    }


}
