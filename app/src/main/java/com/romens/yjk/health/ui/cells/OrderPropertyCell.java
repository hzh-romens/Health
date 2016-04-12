package com.romens.yjk.health.ui.cells;

import android.content.Context;

import com.romens.yjk.health.config.ResourcesConfig;

/**
 * @author Zhou Lisi
 * @create 2016-04-12 16:45
 * @description
 */
public class OrderPropertyCell extends PayInfoCell {
    public OrderPropertyCell(Context context) {
        super(context);
        textView.setTextColor(ResourcesConfig.bodyText3);
        valueTextView.setTextColor(0xff212121);
    }

    @Override
    public void setTextColor() {
        textView.setTextColor(ResourcesConfig.bodyText3);
    }

    @Override
    public void setValueTextColor() {
        valueTextView.setTextColor(0xff212121);
    }
}
