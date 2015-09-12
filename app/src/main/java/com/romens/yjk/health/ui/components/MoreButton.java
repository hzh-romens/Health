package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v7.graphics.Palette;
import android.widget.TextView;

/**
 * Created by siery on 15/9/8.
 */
public class MoreButton extends TextView {

    private ShapeDrawable buttonDrawable;

    public MoreButton(Context context) {
        super(context);
    }

    public void setButtonColor(int color) {
        if (buttonDrawable == null) {
            buttonDrawable = new ShapeDrawable();
        }
        buttonDrawable.setShape(new ButtonShape(color));
        setBackgroundDrawable(buttonDrawable);
        invalidate();
    }

    class ButtonShape extends Shape {
        private int color;

        public ButtonShape(int _color) {
            color = _color;
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            // TODO Auto-generated method stub
            paint.setColor(color);
            int left = getPaddingLeft();
            int top = getPaddingTop();
            int right = canvas.getWidth() - left - getPaddingRight();
            int bottom = canvas.getHeight() - top - getPaddingBottom();
            canvas.drawRoundRect(new RectF(left, top, right, bottom), 8, 8, paint);
        }

    }
}
