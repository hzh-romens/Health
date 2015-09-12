package com.romens.yjk.health.ui.components;

import android.text.TextPaint;

import com.romens.android.AndroidUtilities;

public class URLSpanNoUnderlineBold extends URLSpanNoUnderline {
    public URLSpanNoUnderlineBold(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        ds.setUnderlineText(false);
    }
}
