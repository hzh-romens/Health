package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/12/21.
 */
public class HomeTabCell extends FrameLayout {
    private ImageView iconView;
    private TextView nameView;

    public HomeTabCell(Context context) {
        super(context);
        init(context);
    }

    public HomeTabCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeTabCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        iconView = new ImageView(context);
        iconView.setScaleType(ImageView.ScaleType.CENTER);
        addView(iconView, LayoutHelper.createFrame(24, 24, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 16, 8, 16, 1));

        nameView = new TextView(context);
        nameView.setTextColor(0xff424242);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        nameView.setLines(1);
        nameView.setMaxLines(1);
        nameView.setSingleLine(true);
        nameView.setGravity(Gravity.CENTER);
        addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 16, 1, 16, 8));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64), View.MeasureSpec.EXACTLY));
    }

    public void setValue(int iconResId, CharSequence name) {
        iconView.setImageResource(iconResId);
        nameView.setText(name);
    }

    public void select(boolean isSelected) {
        if (isSelected) {
            iconView.setColorFilter(ResourcesConfig.primaryColor);
            nameView.setTextColor(ResourcesConfig.primaryColor);
        } else {
            iconView.clearColorFilter();
            nameView.setTextColor(0xff424242);
        }
    }
}
