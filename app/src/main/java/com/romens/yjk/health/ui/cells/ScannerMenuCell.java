package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;

/**
 * Created by siery on 15/11/5.
 */
public class ScannerMenuCell extends LinearLayout {

    private ImageView iconView;
    private TextView nameView;

    private int defaultColor;
    private int highlightColor;
    private boolean checked = false;

    public ScannerMenuCell(Context context) {
        super(context);
        init(context);
    }

    public ScannerMenuCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScannerMenuCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        defaultColor = 0xffffffff;
        highlightColor = 0xff2baf2b;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        iconView = new ImageView(context);
        iconView.setColorFilter(defaultColor);
        addView(iconView, LayoutHelper.createLinear(48, 48));

        nameView = new TextView(context);
        nameView.setTextColor(defaultColor);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameView.setLines(1);
        nameView.setMaxLines(1);
        nameView.setSingleLine(true);
        nameView.setGravity(Gravity.CENTER);
        addView(nameView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 8, 4, 8, 4));
    }

    public void setValue(int iconResId, String name) {
        iconView.setImageResource(iconResId);
        nameView.setText(name);
    }

    public void setHighlightColor(int color) {
        this.highlightColor = color;
        updated();
    }

    public void setDefaultColor(int color) {
        defaultColor = color;
        updated();
    }

    public void setChecked(boolean value) {
        checked = value;
        updated();
    }

    private void updated() {
        if (checked) {
            iconView.setColorFilter(highlightColor);
            nameView.setTextColor(highlightColor);
        } else {
            iconView.setColorFilter(defaultColor);
            nameView.setTextColor(defaultColor);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100), View.MeasureSpec.EXACTLY));
    }

}
