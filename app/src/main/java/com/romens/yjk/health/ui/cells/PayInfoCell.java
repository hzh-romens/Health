package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class PayInfoCell extends LinearLayout {

    protected TextView textView;
    protected TextView valueTextView;
    private ImageView navView;
    private static Paint paint;
    private boolean needDivider;
    private boolean multiline;
    private boolean isSmall = false;

    public PayInfoCell(Context context) {
        super(context);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setMinimumHeight(AndroidUtilities.dp(44));

        textView = new TextView(context);
        textView.setTextColor(0xff212121);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(textView);
        addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 16, 10, 8, 8));

        valueTextView = new TextView(context);
        valueTextView.setTextColor(0xff2f8cc9);
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        valueTextView.setLines(1);
        valueTextView.setMaxLines(1);
        valueTextView.setSingleLine(true);
        valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        valueTextView.setGravity((Gravity.RIGHT) | Gravity.CENTER_VERTICAL);
        valueTextView.setMinWidth(AndroidUtilities.dp(64));
        AndroidUtilities.setMaterialTypeface(valueTextView);
        addView(valueTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 1f, 16, 10, 16, 8));

        navView = new ImageView(context);
        navView.setScaleType(ImageView.ScaleType.CENTER);
        navView.setColorFilter(0xffe5e5e5);
        navView.setImageResource(R.drawable.ic_chevron_right_grey600_24dp);
        navView.setVisibility(GONE);
        addView(navView, LayoutHelper.createLinear(24, 24, 0, 10, 8, 8));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!multiline) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(isSmall ? 36 : 44) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(isSmall ? 36 : 48) + (needDivider ? 1 : 0));
//
//        int availableWidth;
//        if (navView.getVisibility() == VISIBLE) {
//            availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(58);
//            navView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24), MeasureSpec.EXACTLY));
//        } else {
//            availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34);
//        }
//        int width = (availableWidth * 2) / 3;
//        if (valueTextView.getVisibility() == VISIBLE) {
//            valueTextView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
//            width = availableWidth - valueTextView.getMeasuredWidth() - AndroidUtilities.dp(8);
//        } else {
//            width = availableWidth;
//        }
//        textView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
//    }

    public void setTextColor() {
        textView.setTextColor(0xff212121);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setValueTextColor(int color) {
        valueTextView.setTextColor(color);
    }

    public void setValueTextColor() {
        valueTextView.setTextColor(0xff2f8cc9);
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


    public void setText(CharSequence text, boolean divider) {
        textView.setText(text);
        valueTextView.setVisibility(INVISIBLE);
        navView.setVisibility(GONE);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValue(CharSequence text, CharSequence value, boolean divider) {
        textView.setText(text);
        if (value != null) {
            valueTextView.setText(value);
            valueTextView.setVisibility(VISIBLE);
        } else {
            valueTextView.setVisibility(INVISIBLE);
        }
        navView.setVisibility(GONE);
        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    public void setTextAndValueAndIcon(CharSequence text, CharSequence value, int iconResId, boolean divider) {
        textView.setText(text);
        if (value != null) {
            valueTextView.setText(value);
            valueTextView.setVisibility(VISIBLE);
        } else {
            valueTextView.setVisibility(INVISIBLE);
        }
        navView.setVisibility(VISIBLE);
        navView.setImageResource(iconResId);
        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    public void setSmall(boolean small) {
        this.isSmall = small;
        setMinimumHeight(AndroidUtilities.dp(isSmall ? 36 : 44));
        requestLayout();
    }

    public void setTextSize(int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public void setValueTextSize(int size) {
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}
