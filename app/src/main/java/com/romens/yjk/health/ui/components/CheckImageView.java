package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class CheckImageView extends ImageView implements Checkable {
    public boolean checked = false;

    private int uncheckedColor = 0xffd9d9d9;
    private int checkedColor = ResourcesConfig.primaryColor;

    public CheckImageView(Context context) {
        super(context);
        init(context);
    }

    public CheckImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.CENTER);
        setImageResource(R.drawable.ic_check_circle_white_24dp);
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        setColorFilter(checked ? checkedColor : uncheckedColor);
    }

    public void setColor(int color) {
        this.uncheckedColor = color;
        setColorFilter(checked ? checkedColor : uncheckedColor);
    }

    public void setCheckedColor(int color) {
        this.checkedColor = color;
        setColorFilter(checked ? checkedColor : uncheckedColor);
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
