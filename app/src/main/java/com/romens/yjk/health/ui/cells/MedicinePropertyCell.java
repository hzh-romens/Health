package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/12/15.
 */
public class MedicinePropertyCell extends LinearLayout {
    private TextView textView;
    private TextView valueTextView;
    private static Paint paint;
    private boolean needDivider;
    private boolean multiline;
    private boolean smallStyle=false;

    public MedicinePropertyCell(Context context) {
        super(context);
        init(context);
    }

    public MedicinePropertyCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MedicinePropertyCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        setMinimumHeight(AndroidUtilities.dp(44));
        setGravity(Gravity.TOP);
        textView = new TextView(context);
        textView.setTextColor(ResourcesConfig.bodyText3);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.LEFT);
        addView(textView, LayoutHelper.createLinear(56, LayoutHelper.WRAP_CONTENT, 17, 8, 4, 8));

        valueTextView = new TextView(context);
        valueTextView.setTextColor(0xff616161);
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        valueTextView.setGravity(Gravity.LEFT);
        valueTextView.setLines(1);
        valueTextView.setMaxLines(1);
        valueTextView.setSingleLine(true);
        addView(valueTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 4, 8, 17, 8));
    }

    public void setValueTextColor(){
        valueTextView.setTextColor(0xff616161);
    }

    public void setValueTextColor(int color){
        valueTextView.setTextColor(color);
    }


    public void setSmallStyle(boolean small){
        smallStyle=small;
        setMinimumHeight(AndroidUtilities.dp(small?36:44));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!multiline) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(smallStyle?36:44) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setMultilineValue(boolean value) {
        multiline = value;
        if (value) {
            valueTextView.setLines(0);
            valueTextView.setMaxLines(0);
            valueTextView.setSingleLine(false);
        } else {
            valueTextView.setLines(1);
            valueTextView.setMaxLines(1);
            valueTextView.setSingleLine(true);
        }
    }

    public void setTextAndValue(CharSequence text, CharSequence value, boolean divider) {
        textView.setText(text);
        valueTextView.setText(value);
        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}
