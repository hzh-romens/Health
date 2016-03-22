package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;

/**
 * Created by AUSU on 2016/1/25.
 */
public class MemberDetailCell extends FrameLayout {
    private TextView Level, Point, Balance;
    private Paint paint;

    public MemberDetailCell(Context context) {
        super(context);
        if(paint==null){
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setWillNotDraw(false);
        int left = AndroidUtilities.dp(16);
        int top = AndroidUtilities.dp(8);

        Level = new TextView(context);
        Level.setTextSize(16);
        Level.setTextColor(Color.BLACK);
        Level.setGravity(Gravity.CENTER);
        addView(Level, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, left, top, left, top));


        Point = new TextView(context);
        Point.setTextSize(16);
        Point.setTextColor(Color.BLACK);
        Point.setGravity(Gravity.CENTER);
        addView(Point, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, left, top, left, top));

        Balance = new TextView(context);
        Balance.setTextSize(16);
        Balance.setTextColor(Color.BLACK);
        Balance.setGravity(Gravity.CENTER);
        addView(Balance, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT, left, top, left, top));
    }

    public void setValue(String level, String point, String balance) {
        Level.setText(level + "\n钻石会员");
        Point.setText(point + "\n积分");
        Balance.setText(balance + "\n余额");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //  if (needDivider) {
        canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);
        //}
    }

}
