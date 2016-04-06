package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/11/4.
 */
public class TextInputNoLineCell extends LinearLayout {

    private TextView textView;
    private MaterialEditText valueTextView;
    private static Paint paint;
    private boolean needDivider;
    private boolean multiline;
    private boolean isMustInput = false;

    public TextInputNoLineCell(Context context) {
        super(context);
        init(context);
    }

    public TextInputNoLineCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextInputNoLineCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(VERTICAL);
        textView = new TextView(context);
        textView.setTextColor(0x80000000);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(Gravity.LEFT | Gravity.TOP);
        addView(textView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 17, 8, 17, 8));

        valueTextView = new MaterialEditText(context);
        valueTextView.setHideUnderline(true);
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        valueTextView.setBaseColor(0xff212121);
        valueTextView.setPrimaryColor(ResourcesConfig.textPrimary);
        valueTextView.setMaxLines(1);
        valueTextView.setLines(1);
        valueTextView.setSingleLine(true);
        valueTextView.setGravity(Gravity.LEFT);
        valueTextView.setInputType(InputType.TYPE_CLASS_TEXT);
        valueTextView.setTypeface(Typeface.DEFAULT);
        addView(valueTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 17, 0, 17, 8));
        valueTextView.setMinHeight(AndroidUtilities.dp(48));
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
        requestLayout();
    }

    public void setOnCellClickListener(OnClickListener listener) {
//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        setClickable(true);
//        setOnClickListener(listener);
        valueTextView.setOnClickListener(listener);
    }

    public void setInputType(int type) {
        valueTextView.setInputType(type);
    }

    public void setInputEnable(boolean enable) {
        valueTextView.setFocusable(enable);
        valueTextView.setFocusableInTouchMode(enable);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setValueTextColor(int color) {
        valueTextView.setTextColor(color);
    }

    public void setText(String text, boolean divider) {
        textView.setText(text);
        valueTextView.setVisibility(INVISIBLE);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValue(String text, String value, String valueHint, boolean isMust, boolean divider) {
        isMustInput = isMust;
        if (isMust) {
            SpannableString caption = new SpannableString("* " + text);
            caption.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(caption);
        } else {
            textView.setText(text);
        }
        valueTextView.setHint(valueHint);
        valueTextView.setText(value);
        valueTextView.setVisibility(VISIBLE);
        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    public boolean isMustInput() {
        return isMustInput;
    }

    public String getValue() {
        return valueTextView.getText().toString();
    }

    public void changeValue(String value) {
        valueTextView.setText(value);
        valueTextView.setVisibility(VISIBLE);
    }

    public void setEditable(boolean value) {
        valueTextView.setEnabled(false);
        valueTextView.setFocusable(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}
