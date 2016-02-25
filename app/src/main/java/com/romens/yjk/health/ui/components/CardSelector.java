package com.romens.yjk.health.ui.components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.view.View;

import com.romens.android.AndroidUtilities;

/**
 * @author Zhou Lisi
 * @create 16/2/24
 * @description
 */
public class CardSelector {
    public static void set(View view, Mode mode) {
        StateListDrawable drawable = new StateListDrawable();
        //如果要设置莫项为false，在前面加负号 ，比如android.R.attr.state_focesed标志true，-android.R.attr.state_focesed就标志false
        drawable.addState(new int[]{android.R.attr.state_pressed}, new ShapeDrawable(new CardPressShape()));
        drawable.addState(new int[]{}, new ShapeDrawable(new CardShape(mode)));//默认
        view.setBackgroundDrawable(drawable);
    }

//    public static void set(View view, Mode mode) {
//        StateListDrawable drawable = new StateListDrawable();
//        //如果要设置莫项为false，在前面加负号 ，比如android.R.attr.state_focesed标志true，-android.R.attr.state_focesed就标志false
//        drawable.addState(new int[]{android.R.attr.state_pressed}, new ShapeDrawable(new CardPressShape()));
//        if(mode==Mode.BEGIN)
//        drawable.addState(new int[]{}, new ShapeDrawable(new CardShape(mode)));//默认
//        view.setBackgroundDrawable(drawable);
//    }

    private static ShapeDrawable createBegin() {
        int r = AndroidUtilities.dp(2);
        float[] outerR = new float[]{r, r, r, r, 0, 0, 0, 0};
        // 内部矩形与外部矩形的距离
        RectF inset = new RectF(1, 1, 1, 0);
        // 内部矩形弧度
        float[] innerRadii = new float[]{r, r, r, r, 0, 0, 0, 0};

        RoundRectShape rr = new RoundRectShape(outerR, inset, innerRadii);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(Color.WHITE);
        drawable.getPaint().setStyle(Paint.Style.FILL);
        return drawable;
    }

    private static ShapeDrawable createMid() {
        int r = AndroidUtilities.dp(2);
        float[] outerR = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
        // 内部矩形与外部矩形的距离
        RectF inset = new RectF(1, 0, 1, 0);
        // 内部矩形弧度
        float[] innerRadii = new float[]{0, 0, 0, 0, 0, 0, 0, 0};

        RoundRectShape rr = new RoundRectShape(outerR, inset, innerRadii);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(Color.WHITE);
        drawable.getPaint().setStyle(Paint.Style.FILL);
        return drawable;
    }

    private static ShapeDrawable createEnd() {
        int r = AndroidUtilities.dp(2);
        float[] outerR = new float[]{0, 0, 0, 0, r, r, r, r};
        // 内部矩形与外部矩形的距离
        RectF inset = new RectF(1, 0, 1, 1);
        // 内部矩形弧度
        float[] innerRadii = new float[]{0, 0, 0, 0, r, r, r, r};

        RoundRectShape rr = new RoundRectShape(outerR, inset, innerRadii);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(Color.WHITE);
        drawable.getPaint().setStyle(Paint.Style.FILL);
        return drawable;
    }

    public enum Mode {
        BEGIN, MIDDLE, END
    }


    static class CardShape extends Shape {
        private final int radio = AndroidUtilities.dp(2);
        private Mode mode;


        private RectF rect = new RectF();
        private Path path;

        public CardShape(Mode m) {
            super();
            this.mode = m;
            path = new Path();
        }


        @Override
        protected void onResize(float w, float h) {
            super.onResize(w, h);
            rect = new RectF(0, 0, w, h);
            path.reset();
            if (mode == Mode.MIDDLE) {
                path.moveTo(0, 0);
                path.lineTo(0, h);
                path.moveTo(w, h);
                path.lineTo(w, 0);
            } else if (mode == Mode.BEGIN) {
                path.moveTo(0, h);
                path.lineTo(0, radio);
                path.addArc(new RectF(0, 0, radio, radio), 180, 90);
                path.moveTo(radio, 0);
                path.lineTo(radio, w - radio);
                path.addArc(new RectF(w - radio, 0, w, radio), 270, 90);
                path.moveTo(w, radio);
                path.lineTo(w, h);
            } else if (mode == Mode.END) {
                path.lineTo(0, h - radio);
                path.addArc(new RectF(0, h - radio, radio, h), 180, -90);
                path.moveTo(radio, h);
                path.lineTo(radio, w - radio);
                path.addArc(new RectF(w - radio, 0, w, h - radio), 0, 90);
                path.moveTo(w, h - radio);
            }
        }


        @Override
        public void draw(Canvas canvas, Paint paint) {
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(0xffffffff);
            canvas.drawRect(rect, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xffd9d9d9);
            canvas.drawPath(path, paint);

        }
    }

    static class CardPressShape extends Shape {

        private RectF rect = new RectF();

        @Override
        protected void onResize(float w, float h) {
            super.onResize(w, h);
            rect = new RectF(0, 0, w, h);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(0xffd9d9d9);
            canvas.drawRect(rect, paint);
        }
    }

}
