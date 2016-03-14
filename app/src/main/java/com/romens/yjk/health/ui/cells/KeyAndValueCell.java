package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;

/**
 * Created by anlc on 2015/9/28.
 */
public class KeyAndValueCell extends FrameLayout {

    private TextView leftTextView;
    private TextView rightTextView;

    public KeyAndValueCell(Context context) {
        super(context);
        leftTextView = new TextView(context);
        leftTextView.setBackgroundColor(Color.TRANSPARENT);
        leftTextView.setTextColor(0xff333333);
        leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        leftTextView.setSingleLine(true);
        addView(leftTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT,
                Gravity.LEFT, 16, 8, 16, 8));

        rightTextView = new TextView(context);
        rightTextView.setBackgroundColor(Color.TRANSPARENT);
        rightTextView.setTextColor(0xff333333);
        rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        rightTextView.setSingleLine(true);
        rightTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(rightTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT,
                Gravity.RIGHT, 68, 8, 16, 8));
    }

    public void setKeyAndValue(String keyText, String valueText, boolean needDiver) {
        leftTextView.setText(keyText);
        rightTextView.setText(valueText);
    }

    public void setValueTextColor(int valueTextColor) {
        rightTextView.setTextColor(valueTextColor);
    }
}
