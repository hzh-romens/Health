package com.romens.yjk.health.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.romens.android.AndroidUtilities;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/8/10.
 */
public class ShoppingCartUtils {
    public static Bitmap createShoppingCartIcon(Context context, int resId, int count) {
        Bitmap photo = BitmapFactory.decodeResource(context.getResources(), resId);
        if (count <= 0) {
            return photo;
        }
        int width = photo.getWidth(), height = photo.getHeight();
        Bitmap icon = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); //建立一个空的BItMap
        Canvas canvas = new Canvas(icon);//初始化画布绘制的图像到icon上
        Paint photoPaint = new Paint(); //建立画笔
        photoPaint.setDither(true); //获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);//过滤一些

        Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());//创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, width, height);//创建一个指定的新矩形的坐标
        canvas.drawBitmap(photo, src, dst, photoPaint);//将photo 缩放或则扩大到 dst使用的填充区photoPaint

        int y = AndroidUtilities.dp(10);
        RectF countRect = new RectF(AndroidUtilities.dp(2), y, width - AndroidUtilities.dp(2), height - AndroidUtilities.dp(2));
        // 产生一个红色的圆角矩形 或者任何有色颜色，不能是透明！
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xffe51c23);
        canvas.drawRoundRect(countRect, AndroidUtilities.dp(2), AndroidUtilities.dp(2), paint);

        paint.setTextSize(AndroidUtilities.dp(10));//字体大小
        paint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度
        paint.setColor(Color.WHITE);//采用的颜色

        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float baseline = countRect.top + (countRect.bottom - countRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        paint.setTextAlign(Paint.Align.CENTER);
        String countStr;
        if (count >= 100) {
            countStr = "99+";
        } else {
            countStr = String.valueOf(count);
        }
        canvas.drawText(countStr, countRect.centerX(), baseline, paint);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return icon;
    }
}
