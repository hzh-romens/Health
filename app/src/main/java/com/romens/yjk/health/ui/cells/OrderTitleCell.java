package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2016/3/24.
 */
public class OrderTitleCell extends LinearLayout {
    KeyAndViewCell cell;
    ShadowSectionCell lineCell;
    private Context context;

    public OrderTitleCell(Context context) {
        this(context, null);

    }

    public OrderTitleCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderTitleCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        lineCell = new ShadowSectionCell(context);
        addView(lineCell);
        cell = new KeyAndViewCell(context);
        addView(cell);
    }

    public void setInfor(String orderNumber, String orderState, boolean isShowLine) {
        if (isShowLine) {
            lineCell.setVisibility(GONE);
        } else {
            lineCell.setVisibility(VISIBLE);
        }
        cell.setKeyAndRightText("订单编号：" + orderNumber, orderState, true);
        cell.setTextViewColor(context.getResources().getColor(R.color.order_statu_color));
        cell.setKeyTextColor(context.getResources().getColor(R.color.theme_title));
    }
}
