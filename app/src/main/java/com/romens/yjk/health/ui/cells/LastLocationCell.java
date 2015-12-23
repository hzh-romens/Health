package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.FrameLayoutFixed;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/12/21.
 */
public class LastLocationCell extends FrameLayoutFixed {
    private TextView locationAddressView;

    public LastLocationCell(Context context) {
        super(context);
        init(context);
    }

    public LastLocationCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LastLocationCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        locationAddressView = new TextView(context);
        locationAddressView.setTextColor(0xffffffff);
        locationAddressView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        locationAddressView.setLines(1);
        locationAddressView.setMaxLines(1);
        locationAddressView.setSingleLine(true);
        locationAddressView.setEllipsize(TextUtils.TruncateAt.START);
        addView(locationAddressView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 8, 0, 32, 0));

        ImageView iconView = new ImageView(context);
        iconView.setScaleType(ImageView.ScaleType.CENTER);
        iconView.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
        addView(iconView, LayoutHelper.createFrame(24, 24, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 8, 0));
    }

    public void setValue(CharSequence location) {
        locationAddressView.setText(location);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), View.MeasureSpec.EXACTLY));
//    }
}
