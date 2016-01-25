package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.ui.components.RoundDrawable;

/**
 * Created by siery on 16/1/15.
 */
public class DrugMenuChildCell extends FrameLayout {

    private TextView nameTextView;
    private CloudImageView iconView;

    private RoundDrawable iconDrawable;

    public DrugMenuChildCell(Context context) {
        super(context);

        nameTextView = new TextView(context);
        nameTextView.setTextColor(ResourcesConfig.bodyText1);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameTextView.setLines(2);
        nameTextView.setMaxLines(2);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 16, 10, 16, 0));

        iconView = CloudImageView.create(context);
        iconView.setRound(AndroidUtilities.dp(4));
        addView(iconView, LayoutHelper.createFrame(48, 48, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 16, 0, 16, 10));
        iconDrawable = new RoundDrawable();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(112), MeasureSpec.EXACTLY));
    }

    public void setValue(String name, String icon) {
        nameTextView.setText(name);
        iconDrawable.setInfo(name, 0xffe5e5e5);
        iconView.setPlaceholderImage(iconDrawable);
        if (icon != null) {
            iconView.setImagePath(icon);
        }
    }
}
