package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;

/**
 * Created by AUSU on 2016/1/2.
 */
public class MemberTextViewCell extends FrameLayout {
    private TextView tv_title, tv_value;

    public MemberTextViewCell(Context context) {
        super(context);
        setBackgroundColor(0xffffffff);
        LinearLayout container = new LinearLayout(context);
        container.setBackgroundColor(0xffffffff);
        tv_title = new TextView(context);
        tv_title.setTextColor(0xff555555);
        tv_title.setTextSize(16);
        tv_value = new TextView(context);
        tv_value.setTextColor(0xff333333);
        tv_value.setTextSize(16);
        tv_value.setGravity(Gravity.CENTER);
        tv_title.setGravity(Gravity.CENTER);
        container.addView(tv_title, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER_VERTICAL));
        container.addView(tv_value, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER_VERTICAL));
        container.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 36, Gravity.NO_GRAVITY, 8, 0, 0, 0));
        addView(container);
    }

    public void setValue(String title, String value) {
        if ("会员积分".equals(title)) {
            tv_value.setTextColor(0xff97bd2c);
        }
        tv_title.setText(title + ": ");
        tv_value.setText(value);
    }
}
