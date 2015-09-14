package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by AUSU on 2015/9/14.
 */
public class StoreLayout extends View {
    private Paint paint;
    public StoreLayout(Context context) {
        super(context);
        paint=new Paint();
        paint.setStyle(Paint.Style.STROKE); //空心
        paint.setAntiAlias(true);//无锯齿
    }
    public StoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
    public void DrawText(Canvas c,String text){

    }
    public void DrawBitmap(Canvas c,Context context, int resId){
        Bitmap photo = BitmapFactory.decodeResource(context.getResources(), resId);
        int width = photo.getWidth(), height = photo.getHeight();
        Bitmap icon = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); //建立一个空的BItMap
        Canvas canvas = new Canvas(icon);//初始化画布绘制的图像到icon上
        Paint photoPaint = new Paint(); //建立画笔
        photoPaint.setDither(true); //获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);//过滤一些
        Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());//创建一个指定的新矩形的坐标,图片本来的宽高
        Rect dst = new Rect(0, 0, width, height);//创建一个指定的新矩形的坐标,用来显示需要显示的宽高
        canvas.drawBitmap(photo, src, dst, photoPaint);//将photo 缩放或则扩大到 dst使用的填充区photoPaint
    }
    public void DrawCircle(Canvas c,int count){

    }
}
