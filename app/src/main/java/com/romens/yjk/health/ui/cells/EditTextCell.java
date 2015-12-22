package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/8/20.
 */
public class EditTextCell extends LinearLayout {
    private MaterialEditText editText;
    private static Paint paint;
    private boolean needDivider;

    public EditTextCell(Context context) {
        super(context);
        init(context);
    }

    public EditTextCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditTextCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(VERTICAL);
        editText = new MaterialEditText(context);
        editText.setBaseColor(0x8a000000);
        editText.setPrimaryColor(ResourcesConfig.primaryColor);
        editText.setHideUnderline(true);
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText.setFloatingLabelTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 17, 8, 17, 4));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(UIUtils.dp(90) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), MeasureSpec.UNSPECIFIED));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }

    public void setNeedDivider(boolean value) {
        needDivider = value;
        setWillNotDraw(!needDivider);
        requestLayout();
    }

    public void setInputType(int inputType) {
        editText.setInputType(inputType);
    }

    public void setLabelText(CharSequence text) {
        editText.setFloatingLabelText(text);
    }

    public void setLabelTextAlwaysShown(boolean value) {
        editText.setFloatingLabelAlwaysShown(value);
    }

    public void setBaseColor(int color) {
        editText.setBaseColor(color);
    }

    public void setPrimaryColor(int color) {
        editText.setPrimaryColor(color);
    }

    public void setHelperText(CharSequence text) {
        setHelperText(text, 0x60000000);
    }

    public void setHelperText(CharSequence text, int color) {
        editText.setHelperText(text);
        editText.setHelperTextColor(color);
    }

    public void setErrorText(CharSequence text) {
        setErrorText(text, 0xffe51c23);
    }

    public void setErrorText(CharSequence text, int color) {
        editText.setError(text);
        editText.setErrorColor(color);
    }

    public String getEditText() {
        return editText.getText().toString().trim();
    }
}
