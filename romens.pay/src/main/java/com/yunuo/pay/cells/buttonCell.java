package com.yunuo.pay.cells;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yunuo.pay.R;

/**
 * Created by HZH on 2016/2/19.
 */
public class ButtonCell extends FrameLayout {
    private TextView sureButton;

    public ButtonCell(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.list_item_button, null);
        sureButton = (TextView) view.findViewById(R.id.button);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setValue(String value) {
        if ("支付成功".equals(value)) {
            sureButton.setText("保存到手机相册");
        } else {
            sureButton.setText("返回首页");
        }

    }
}
