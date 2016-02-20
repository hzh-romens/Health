package com.yunuo.pay.cells;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunuo.pay.R;

/**
 * Created by HZH on 2016/2/19.
 */
public class StatusCell extends FrameLayout {
    private ImageView statusIcon;
    private TextView statusValue, msgView;
    private Context mContext;

    public StatusCell(Context context) {
        super(context);
        this.mContext = context;
        View view = View.inflate(context, R.layout.list_item_status, null);
        statusIcon = (ImageView) view.findViewById(R.id.icon_status);
        statusValue = (TextView) view.findViewById(R.id.statusValue);
        msgView = (TextView) view.findViewById(R.id.errorMsg);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setValue(String status, String msg) {
        if ("支付成功".equals(status)) {
            statusIcon.setBackgroundResource(R.drawable.ic_icon_succees);
            // statusValue.setTextColor(mContext.getResources().getColor(android.R.attr.colorPrimary));
            statusValue.setTextColor(android.R.attr.colorPrimary);
            statusValue.setText("支付成功");
            msgView.setVisibility(GONE);
        } else if ("支付失败".equals(status)) {
            statusIcon.setBackgroundResource(R.drawable.ic_icon_error);
            statusValue.setTextColor(0xff666666);
            statusValue.setText("支付失败");
            msgView.setVisibility(VISIBLE);
            msgView.setText(msg);
        }
    }
}
