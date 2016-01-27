package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;

/**
 * Created by AUSU on 2016/1/26.
 */
public class CouponItemCell extends FrameLayout {
    private TextView nameView, descriptionView;
    private ImageView drawable;
    private int drawRes;
    //    private boolean display;
    private boolean mLine;
    private Paint mPaint;

    public CouponItemCell(Context context) {
        super(context);
        setWillNotDraw(false);
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.GRAY);
            mPaint.setStrokeWidth(1);
        }
        FrameLayout container = new FrameLayout(context);
        nameView = new TextView(context);
        nameView.setTextSize(16.0f);
        nameView.setTextColor(0xff333333);
        container.addView(nameView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT|Gravity.CENTER_VERTICAL, 8, 8, 8, 8));
        descriptionView = new TextView(context);
        descriptionView.setTextSize(14);
        descriptionView.setTextColor(0xff333333);
        descriptionView.setEllipsize(TextUtils.TruncateAt.END);
        descriptionView.setMaxLines(1);
        container.addView(descriptionView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT|Gravity.CENTER_VERTICAL, 8, 8, 48, 8));
        drawable = new ImageView(context);
        container.addView(drawable, LayoutHelper.createFrame(48, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT, 8, 8, 0, 8));
        addView(container, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.NO_GRAVITY, 8, 8, 0, 8));
    }

    public void setValue(String name, String description, int resId, boolean line) {
        nameView.setText(name);
        descriptionView.setText(description);
        drawable.setImageResource(resId);
        this.mLine = line;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (mLine) {
            canvas.drawLine(0, canvas.getHeight() - 1, canvas.getWidth(), canvas.getHeight() - 1, mPaint);
        }
    }
}
