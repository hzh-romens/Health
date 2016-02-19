package com.yunuo.pay.cells;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunuo.pay.R;

/**
 * Created by HZH on 2016/1/13.
 */
public class CntentCell extends LinearLayout {
    private TextView descriptionView;
    private TextView valueView;

    public CntentCell(Context context) {
        super(context);
        initPaint();
        View view = View.inflate(context, R.layout.list_item_content, null);
        descriptionView = (TextView) view.findViewById(R.id.tv_describtion);
        valueView = (TextView) view.findViewById(R.id.tv_value);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private Paint mPaint;

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xffededed);
    }

    public void setValue(String detail, String price) {
        descriptionView.setText(detail + ":");
        valueView.setText("¥" + price);
    }
    public void setDetail(String detail, String value) {
        descriptionView.setText(detail);
        valueView.setText(value);
    }


}
