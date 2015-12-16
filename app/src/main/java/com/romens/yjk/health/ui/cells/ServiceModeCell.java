package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/12/16.
 */
public class ServiceModeCell extends LinearLayout {

    private ImageView iconView;
    private TextView textView;

    public ServiceModeCell(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        iconView = new ImageView(context);
        iconView.setScaleType(ImageView.ScaleType.CENTER);
        addView(iconView, LayoutHelper.createLinear(20, 20));

        textView = new TextView(context);
        textView.setTextColor(ResourcesConfig.bodyText2);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.LEFT);
        addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 4, 0, 4, 0));
    }

    public void setValue(int iconResId, int color, String text) {
        iconView.setImageResource(iconResId);
        if (color == 0) {
            iconView.clearColorFilter();
        } else {
            iconView.setColorFilter(color);
        }
        textView.setText(text);
    }
}
