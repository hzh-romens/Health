package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * @author Zhou Lisi
 * @create 2016-04-07 14:58
 * @description
 */
public class TextInfoCell extends FrameLayout {

    private TextView textView;
    private TextView valueTextView;
    private ImageView selectView;
    private static Paint paint;
    private boolean needDivider;

    public TextInfoCell(Context context) {
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
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (Gravity.LEFT) | Gravity.TOP, 17, 0, 17, 0));

        valueTextView = new TextView(context);
        valueTextView.setTextColor(0xff2f8cc9);
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        valueTextView.setLines(1);
        valueTextView.setMaxLines(1);
        valueTextView.setSingleLine(true);
        valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        valueTextView.setGravity((Gravity.RIGHT) | Gravity.CENTER_VERTICAL);
        addView(valueTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, (Gravity.RIGHT) | Gravity.TOP, 17, 0, 40, 0));

        selectView = new ImageView(context);
        selectView.setScaleType(ImageView.ScaleType.CENTER);
        selectView.setVisibility(INVISIBLE);
        selectView.setImageResource(R.drawable.ic_chevron_right_grey600_24dp);
        selectView.setColorFilter(0xffe5e5e5);
        addView(selectView, LayoutHelper.createFrame(24, 24, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 8, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(48) + (needDivider ? 1 : 0));

        if (selectView.getVisibility() == VISIBLE) {
            selectView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(17), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(17), MeasureSpec.EXACTLY));
        }
        int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(57);
        int width = availableWidth / 2;
        if (valueTextView.getVisibility() == VISIBLE) {
            valueTextView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
            width = availableWidth - valueTextView.getMeasuredWidth() - AndroidUtilities.dp(8);
        } else {
            width = availableWidth;
        }
        textView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setValueTextColor(int color) {
        valueTextView.setTextColor(color);
    }

    public void setText(CharSequence text, boolean isSelect, boolean divider) {
        textView.setText(text);
        valueTextView.setVisibility(INVISIBLE);
        selectView.setVisibility(isSelect ? VISIBLE : INVISIBLE);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValue(CharSequence text, CharSequence value, boolean isSelect, boolean divider) {
        textView.setText(text);
        selectView.setVisibility(isSelect ? VISIBLE : INVISIBLE);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) valueTextView.getLayoutParams();
        layoutParams.rightMargin = isSelect ? AndroidUtilities.dp(40) : AndroidUtilities.dp(17);
        valueTextView.setLayoutParams(layoutParams);
        if (value != null) {
            valueTextView.setText(value);
            valueTextView.setVisibility(VISIBLE);
        } else {
            valueTextView.setVisibility(INVISIBLE);
        }
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
