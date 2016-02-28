package com.yunuo.pay.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yunuo.pay.R;

/**
 * Created by HZH on 2016/2/19.
 */
public class ButtonCells extends FrameLayout {
    private TextView sureButton;

    public ButtonCells(Context context) {
        super(context);
        initView(context);
    }


    public ButtonCells(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ButtonCells(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.list_item_button, null);
        sureButton = (TextView) view.findViewById(R.id.button);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    public void setValue(String value) {
        if ("支付成功".equals(value)) {
            sureButton.setText("保存到手机相册");
        } else {
            sureButton.setText("继续支付");
        }

    }
}
