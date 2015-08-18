package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;


public class LocationLoadingCell extends FrameLayout {

    private ProgressBar progressBar;
    private TextView textView;

    public LocationLoadingCell(Context context) {
        super(context);

        progressBar = new ProgressBar(context);
        addView(progressBar, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        textView = new TextView(context);
        textView.setTextColor(0xff999999);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setText("无数据");
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) (AndroidUtilities.dp(56) * 2.5f), MeasureSpec.EXACTLY));
    }

    public void setLoading(boolean value) {
        progressBar.setVisibility(value ? VISIBLE : INVISIBLE);
        textView.setVisibility(value ? INVISIBLE : VISIBLE);
    }
}
