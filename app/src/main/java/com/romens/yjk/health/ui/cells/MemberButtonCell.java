package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/3/21.
 */
public class MemberButtonCell extends FrameLayout {
    public MemberButtonCell(Context context) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.theme_primary));
        drawable.setCornerRadius(8);
        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.btn_primary_light));
        TextView textView = new TextView(context);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setText("确定");
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(context.getResources().getColor(R.color.theme_primary));
        textView.setBackgroundDrawable(drawable);
        linearLayout.addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER, 8, 16, 8, 16));
        addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.NO_GRAVITY, 8, 16, 8, 16));

    }
}
