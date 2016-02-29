package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.FrameLayoutFixed;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * @author Zhou Lisi
 * @create 16/2/29
 * @description
 */
public class TextDetailInfoCell extends FrameLayoutFixed {

    private TextView textView;
    private TextView valueTextView;
    private ImageView navView;

    private static Paint paint;
    private boolean needDivider;
    private boolean multiline;

    public TextDetailInfoCell(Context context) {
        super(context);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        textView = new TextView(context);
        textView.setTextColor(0xff212121);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(textView);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (Gravity.LEFT) | Gravity.TOP, 17, 10, 40, 0));

        valueTextView = new TextView(context);
        valueTextView.setTextColor(0xff8a8a8a);
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        valueTextView.setGravity(Gravity.LEFT);
        valueTextView.setLines(1);
        valueTextView.setMaxLines(1);
        valueTextView.setSingleLine(true);
        valueTextView.setPadding(0, 0, 0, 0);
        AndroidUtilities.setMaterialTypeface(valueTextView);
        addView(valueTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (Gravity.LEFT) | Gravity.TOP, 17, 35, 40, 0));

        navView = new ImageView(context);
        navView.setScaleType(ImageView.ScaleType.CENTER);
        navView.setImageResource(R.drawable.ic_chevron_right_grey600_24dp);
        navView.setColorFilter(0xffe5e5e5);
        addView(navView, LayoutHelper.createFrame(24, 24, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 8, 0));
    }

    public void setTextSize(int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public void setValueSize(int size) {
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setValueTextColor(int color) {
        valueTextView.setTextColor(color);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!multiline) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        }
    }

    public void setMultilineDetail(boolean value) {
        multiline = value;
        if (value) {
            valueTextView.setLines(0);
            valueTextView.setMaxLines(0);
            valueTextView.setSingleLine(false);
            valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12));
        } else {
            valueTextView.setLines(1);
            valueTextView.setMaxLines(1);
            valueTextView.setSingleLine(true);
            valueTextView.setPadding(0, 0, 0, 0);
        }
    }

    public void setTextAndValue(CharSequence text, CharSequence value, boolean needNav, boolean divider) {
        textView.setText(text);
        valueTextView.setText(value);
        navView.setVisibility(needNav ? View.VISIBLE : View.INVISIBLE);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}
