package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/8/14.
 */
public class ADGroupCell extends FrameLayout {
    private TextView nameView;
    private TextView moreButton;
    private int topPadding = 0;

    public ADGroupCell(Context context) {
        super(context);
        setPadding(0, topPadding, 0, 0);
        nameView = new TextView(context);
        nameView.setTextColor(0xff212121);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        nameView.setLines(1);
        nameView.setMaxLines(1);
        nameView.setSingleLine(true);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);

        addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 8, 72, 8));

        moreButton = new TextView(context);
        moreButton.setBackgroundResource(R.drawable.btn_primary_default);
        moreButton.setTextColor(0xffffffff);
        moreButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        moreButton.setLines(1);
        moreButton.setMaxLines(1);
        moreButton.setSingleLine(true);
        moreButton.setText("更多");
        moreButton.setGravity(Gravity.CENTER);

        addView(moreButton, LayoutHelper.createFrame(48, 32, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 8, 8, 16, 8));

    }

    public void setName(String name) {
        nameView.setText(name);
    }

    public void setMoreButton(boolean enable, int resId) {
        moreButton.setBackgroundResource(resId);
        moreButton.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    public void setTopPadding(int padding) {
        topPadding = padding;
        setPadding(0, topPadding, 0, 0);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + topPadding, MeasureSpec.EXACTLY));
    }
}
