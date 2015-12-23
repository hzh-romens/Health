package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/12/18.
 */
public class ADProductGroupCell extends FrameLayout {

    private TextView textView;
    private TextView valueTextView;
    private ImageView selectView;
    private static Paint paint;
    private boolean needDivider;

    public ADProductGroupCell(Context context) {
        super(context);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        textView = new TextView(context);
        textView.setTextColor(ResourcesConfig.bodyText1);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (Gravity.LEFT) | Gravity.TOP, 17, 0, 17, 0));

        valueTextView = new TextView(context);
        valueTextView.setTextColor(ResourcesConfig.bodyText3);
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        valueTextView.setLines(1);
        valueTextView.setMaxLines(1);
        valueTextView.setSingleLine(true);
        valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        valueTextView.setGravity((Gravity.RIGHT) | Gravity.CENTER_VERTICAL);
        addView(valueTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, (Gravity.RIGHT) | Gravity.TOP, 17, 0, 24, 0));

        selectView = new ImageView(context);
        selectView.setScaleType(ImageView.ScaleType.CENTER);
        selectView.setVisibility(INVISIBLE);
        selectView.setImageResource(R.drawable.ic_chevron_right_grey600_18dp);
        addView(selectView, LayoutHelper.createFrame(18, 18, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 8, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(36) + (needDivider ? 1 : 0));

        if (selectView.getVisibility() == VISIBLE) {
            selectView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(17), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(17), MeasureSpec.EXACTLY));
        }
        int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34);
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

    public void setText(String text, boolean isSelect, boolean divider) {
        textView.setText(text);
        valueTextView.setVisibility(INVISIBLE);
        selectView.setVisibility(isSelect ? VISIBLE : INVISIBLE);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValue(String text, String value, boolean isSelect, boolean divider) {
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanString);
        selectView.setVisibility(isSelect ? VISIBLE : INVISIBLE);
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
