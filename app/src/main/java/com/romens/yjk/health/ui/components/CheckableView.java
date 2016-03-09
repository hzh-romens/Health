package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class CheckableView extends FrameLayout implements Checkable {
    private ImageView checkView;
    private TextView textView;

    public boolean checked = false;

    private int uncheckedColor = 0xffd9d9d9;
    private int checkedColor = ResourcesConfig.primaryColor;

    public CheckableView(Context context) {
        super(context);
        init(context);
    }

    public CheckableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        setBackgroundResource(R.drawable.list_selector);
        checkView = new ImageView(context);
        checkView.setScaleType(ImageView.ScaleType.CENTER);
        checkView.setImageResource(R.drawable.ic_check_circle_white_24dp);
        addView(checkView, LayoutHelper.createFrame(24, 24, Gravity.CENTER));

        textView = new TextView(context);
        textView.setTextColor(0xff212121);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setVisibility(GONE);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 48, 0, 8, 0));
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        checkView.setColorFilter(checked ? checkedColor : uncheckedColor);
    }

    public void setColor(int color) {
        this.uncheckedColor = color;
        checkView.setColorFilter(checked ? checkedColor : uncheckedColor);
    }

    public void setCheckedColor(int color) {
        this.checkedColor = color;
        checkView.setColorFilter(checked ? checkedColor : uncheckedColor);
    }

    public void shouldText(boolean should) {
        if (should) {
            LayoutParams layoutParams = (LayoutParams) checkView.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            layoutParams.leftMargin = AndroidUtilities.dp(16);
            checkView.setLayoutParams(layoutParams);
            textView.setVisibility(VISIBLE);
        } else {
            LayoutParams layoutParams = (LayoutParams) checkView.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.leftMargin = 0;
            checkView.setLayoutParams(layoutParams);
            textView.setVisibility(GONE);
        }
        requestLayout();
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setTextSize(int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }
}
