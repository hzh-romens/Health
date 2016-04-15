package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.images.ui.CloudImageView;

/**
 * Created by siery on 15/8/28.
 */
public class DiscoveryCell extends FrameLayout {
    public static final int DEFAULT_SIZE = AndroidUtilities.dp(96);
    private CloudImageView iconView;
    private TextView nameView;

    public DiscoveryCell(Context context) {
        super(context);
        setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        iconView = CloudImageView.create(context);
        addView(iconView, LayoutHelper.createFrame(48, 48, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 8, 0, 0));

        nameView = new TextView(context);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameView.setMaxLines(1);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setSingleLine(true);
        nameView.setTextColor(0xff212121);
        nameView.setGravity(Gravity.CENTER);
        addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 64, 0, 0));

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight;
        if (measureWidth >= DEFAULT_SIZE) {
            measureHeight = measureWidth;
        } else {
            measureHeight = DEFAULT_SIZE;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    public void setValue(String iconUrl, Drawable thumb, CharSequence name) {
        iconView.setPlaceholderImage(thumb);
        iconView.setImagePath(iconUrl);
        nameView.setText(name);
    }

    public void setValue(Drawable icon, String name) {
        iconView.setPlaceholderImage(icon);
        nameView.setText(name);
    }

    public void setValue(int resId, String name) {
        iconView.setPlaceholderImage(getResources().getDrawable(resId));
        nameView.setText(name);
    }

    public void setColorFilter(boolean isFilter, int color) {
        if (isFilter) {
            iconView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        } else {
            iconView.clearColorFilter();
        }
    }
}
