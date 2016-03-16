package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by anlc on 2016/1/12.
 */
public class DrugGroupMenuCell extends FrameLayout {

    private TextView textView;

    private boolean isSelected = false;
    private static Paint paint;

    public DrugGroupMenuCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2);
        }

        textView = new TextView(context);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ResourcesConfig.bodyText1);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setIncludeFontPadding(false);
        FrameLayout.LayoutParams params = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER, 8, 0, 8, 0);
        addView(textView, params);
    }

    public void setValue(String text, boolean isSelected) {
        this.isSelected = isSelected;
        setBackgroundColor(isSelected ? 0xfff0f0f0 : 0xffffffff);
        textView.setText(text);
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isSelected) {
            paint.setColor(ResourcesConfig.primaryColor);
            canvas.drawRect(0, 0, AndroidUtilities.dp(4), getHeight(), paint);
        } else {
            paint.setColor(0xfff0f0f0);
            canvas.drawLine(AndroidUtilities.dp(4), getHeight() - 2, getWidth(), getHeight() - 2, paint);
        }
    }
}
