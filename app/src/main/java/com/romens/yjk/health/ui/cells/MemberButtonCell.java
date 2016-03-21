package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Color;
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
        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.btn_primary_light));
        TextView textView = new TextView(context);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setBackgroundColor(context.getResources().getColor(R.color.theme_primary));
        linearLayout.addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER, 8, 16, 8, 16));
        addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 64, Gravity.NO_GRAVITY, 8, 16, 8, 16));

    }
}
