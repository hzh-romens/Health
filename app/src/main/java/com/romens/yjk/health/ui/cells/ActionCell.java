package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class ActionCell extends FrameLayout {
    private TextView button;

    public ActionCell(Context context) {
        super(context);
        init(context);
    }

    public ActionCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ActionCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        button = new TextView(context);
        button.setBackgroundResource(R.drawable.btn_primary);
        button.setTextColor(0xffffffff);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        button.setLines(1);
        button.setMaxLines(1);
        button.setSingleLine(true);
        button.setEllipsize(TextUtils.TruncateAt.END);
        button.setGravity(Gravity.CENTER);
        addView(button, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 40, Gravity.LEFT, 16, 8, 16, 8));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56), MeasureSpec.EXACTLY));
    }

    public void setValue(String text) {
        button.setText(text);
    }
}
