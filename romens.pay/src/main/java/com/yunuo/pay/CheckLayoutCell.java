package com.yunuo.pay;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunuo.pay.wx.CheckableFrameLayout;

/**
 * Created by HZH on 2016/1/13.
 */
public class CheckLayoutCell extends FrameLayout {
    private TextView nameView, detailView;
    private ImageView headIcon;
    private CheckableFrameLayout checkLayout;
    private Context mContext;
    private View line;

    private Paint mPaint;
    private boolean mIsNeedDiver;
    private checkChangeListener mListener;


    public CheckLayoutCell(Context context) {
        super(context);
        this.mContext = context;
        View view = View.inflate(context, R.layout.list_item_apply, null);
        nameView = (TextView) view.findViewById(R.id.tv_name);
        detailView = (TextView) view.findViewById(R.id.tv_detail);
        headIcon = (ImageView) view.findViewById(R.id.iv_head);
        checkLayout = (CheckableFrameLayout) view.findViewById(R.id.checkLayout);
        line = view.findViewById(R.id.line);
        checkLayout.setChecked(false);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        checkLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.stateChange(!checkLayout.isChecked());
            }
        });
    }


    public void setValue(int resId, String name, String detail, boolean isNeedDiver) {
        nameView.setText(name);
        headIcon.setBackgroundDrawable(mContext.getResources().getDrawable(resId));
        detailView.setText(detail);
        if (isNeedDiver) {
            line.setVisibility(VISIBLE);
        } else {
            line.setVisibility(GONE);
        }
    }

    public void setCheckStatus(boolean status) {
        checkLayout.setChecked(status);
    }

    public void setListener(checkChangeListener listener) {
        this.mListener = listener;
    }

    public interface checkChangeListener {
        void stateChange(boolean status);
    }


}
