package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;

/**
 * Created by anlc on 2015/9/28.
 */
public class KeyAndValueCell extends RelativeLayout {

    private TextView leftTextView;
    private TextView rightTextView;

    public KeyAndValueCell(Context context) {
        super(context);
        leftTextView = new TextView(context);
        leftTextView.setBackgroundColor(Color.TRANSPARENT);
        leftTextView.setTextColor(0xff333333);
        leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        leftTextView.setSingleLine(true);
        leftTextView.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(8), AndroidUtilities.dp(20), AndroidUtilities.dp(8));
        LayoutParams leftViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(leftTextView, leftViewParams);

        rightTextView = new TextView(context);
        rightTextView.setBackgroundColor(Color.TRANSPARENT);
        rightTextView.setTextColor(0xff333333);
        rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        rightTextView.setSingleLine(true);
        rightTextView.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(8), AndroidUtilities.dp(20), AndroidUtilities.dp(8));
        LayoutParams rightViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(rightTextView, rightViewParams);
        rightViewParams = (LayoutParams) rightTextView.getLayoutParams();
        rightViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
    }

    public void setKeyAndValue(String keyText, String valueText, boolean needDiver) {
        leftTextView.setText(keyText);
        rightTextView.setText(valueText);
    }

    public void setValueTextColor(int valueTextColor) {
        rightTextView.setTextColor(valueTextColor);
    }
}
