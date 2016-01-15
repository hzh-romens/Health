package com.romens.yjk.health.ui.components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.romens.android.AndroidUtilities;
import com.romens.android.log.FileLog;

public class RoundDrawable extends Drawable {

    private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static TextPaint namePaint;
    private static TextPaint namePaintSmall;

    private int color;
    private StaticLayout textLayout;
    private float textWidth;
    private float textHeight;
    private float textLeft;
    private boolean smallStyle;
    private StringBuilder stringBuilder = new StringBuilder(5);

    public RoundDrawable() {
        super();

        if (namePaint == null) {
            namePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            namePaint.setColor(0xffffffff);
            namePaint.setTextSize(AndroidUtilities.dp(20));

            namePaintSmall = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            namePaintSmall.setColor(0xffffffff);
            namePaintSmall.setTextSize(AndroidUtilities.dp(14));
        }
    }

    public void setSmallStyle(boolean value) {
        smallStyle = value;
    }

    public void setColor(int value) {
        color = value;
    }

    public void setInfo(String name, int drawColor) {
        color = drawColor;

        stringBuilder.setLength(0);
        if (name != null && name.length() > 0) {
            stringBuilder.append(name.substring(0, 1));
        }
        if (name != null && name.length() > 0) {
            for (int a = name.length() - 1; a >= 0; a--) {
                if (name.charAt(a) == ' ') {
                    if (a != name.length() - 1 && name.charAt(a + 1) != ' ') {
                        if (Build.VERSION.SDK_INT >= 16) {
                            stringBuilder.append("\u200C");
                        }
                        stringBuilder.append(name.substring(a + 1, a + 2));
                        break;
                    }
                }
            }
        }

        if (stringBuilder.length() > 0) {
            String text = stringBuilder.toString().toUpperCase();
            try {
                textLayout = new StaticLayout(text, (smallStyle ? namePaintSmall : namePaint), AndroidUtilities.dp(100), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (textLayout.getLineCount() > 0) {
                    textLeft = textLayout.getLineLeft(0);
                    textWidth = textLayout.getLineWidth(0);
                    textHeight = textLayout.getLineBottom(0);
                }
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        } else {
            textLayout = null;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds == null) {
            return;
        }
        int size = bounds.width();
        paint.setColor(color);
        canvas.save();
        canvas.translate(bounds.left, bounds.top);
        RectF rect = new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom);
        canvas.drawRoundRect(rect, AndroidUtilities.dp(4),  AndroidUtilities.dp(4), paint);

        if (textLayout != null) {
            canvas.translate((size - textWidth) / 2 - textLeft, (size - textHeight) / 2);
            textLayout.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public int getIntrinsicWidth() {
        return 0;
    }

    @Override
    public int getIntrinsicHeight() {
        return 0;
    }

}
