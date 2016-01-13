package com.yunuo.pay;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by HZH on 2016/1/13.
 */
public class TextViewCell extends FrameLayout {
    public TextViewCell(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.list_item_title, null);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
