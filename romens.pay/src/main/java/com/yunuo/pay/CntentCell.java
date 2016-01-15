package com.yunuo.pay;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by HZH on 2016/1/13.
 */
public class CntentCell extends LinearLayout {
    private TextView detailView;
    private TextView priceView;

    public CntentCell(Context context) {
        super(context);
        initPaint();
        View view = View.inflate(context, R.layout.list_item_content, null);
        detailView = (TextView) view.findViewById(R.id.tv_detail);
        priceView = (TextView) view.findViewById(R.id.tv_price);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private Paint mPaint;

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xffededed);
    }

    public void setValue(String detail, String price) {
        detailView.setText("订单详情：" + detail);
        priceView.setText("订单金额：¥" + price);
    }


}
