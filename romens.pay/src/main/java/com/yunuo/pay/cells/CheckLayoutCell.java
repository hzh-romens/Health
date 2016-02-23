package com.yunuo.pay.cells;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunuo.pay.R;

/**
 * Created by HZH on 2016/1/13.
 */
public class CheckLayoutCell extends FrameLayout {
    private TextView nameView, detailView;
    private ImageView headIcon;
    private CheckableFrameLayout checkLayout;
    private Context mContext;
    private View line;

    private boolean mIsNeedDiver;
    private checkChangeListener mListener;

    public void setCheckStatus(boolean status) {
        checkLayout.setChecked(status);
    }

    public void setListener(checkChangeListener listener) {
        this.mListener = listener;
    }

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
//                String name = nameView.getText().toString();
//                if (getResources().getString(R.string.wx).equals(name)) {
//                    mListener.stateChange(ListItemType.wxFlag, !checkLayout.isChecked());
//                } else if (getResources().getString(R.string.apply).equals(name)) {
//                    mListener.stateChange(ListItemType.applyFlag, !checkLayout.isChecked());
//                }
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


    public interface checkChangeListener {
        void stateChange(int flag, boolean status);
    }
}
