package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.Shape;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;

/**
 * Created by siery on 15/9/14.
 */
public class FlowCell extends TextView {
    private boolean checked = false;

    public FlowCell(Context context, int color) {
        super(context);
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));

        ShapeDrawable pressed = new ShapeDrawable();
        pressed.setShape(new PressShape(color));
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressed);

        ShapeDrawable normal = new ShapeDrawable();
        normal.setShape(new DefaultShape(color));
        drawable.addState(new int[]{}, normal);
        setBackgroundDrawable(drawable);

        int[] colors = new int[]{0xffffffff, color};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_pressed};
        states[1] = new int[]{};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        setTextColor(colorStateList);

        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        setSingleLine(true);
        setGravity(Gravity.CENTER);
        setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32), MeasureSpec.EXACTLY));
    }

    class DefaultShape extends Shape {
        private int color;

        public DefaultShape(int _color) {
            color = _color;
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(color);
            int left = 0;
            int top = 0;
            int right = canvas.getWidth();
            int bottom = canvas.getHeight();
            int r = (bottom - top) / 2;
            canvas.drawRoundRect(new RectF(left, top, right, bottom), r, r, paint);
        }

    }

    class PressShape extends Shape {
        private int color;

        public PressShape(int _color) {
            color = _color;
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            int left = 0;
            int top = 0;
            int right = canvas.getWidth();
            int bottom = canvas.getHeight();
            int r = (bottom - top) / 2;
            canvas.drawRoundRect(new RectF(left, top, right, bottom), r, r, paint);

//            paint.setStrokeWidth(1);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setColor(color);
//            canvas.drawRoundRect(new RectF(left, top, right, bottom), r, r, paint);
        }

    }
}
