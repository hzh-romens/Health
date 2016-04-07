package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.helper.ShoppingHelper;

/**
 * @author Zhou Lisi
 * @create 2016-03-29 13:12
 * @description
 */
public class OrderCell extends LinearLayout {
    private TextView orderNoTextView;
    private TextView orderDateTextView;

    private TextView orderStatusTextView;

    private OrderGoodsSimpleCell goodsCell;

    private TextView goodsCountTextView;

    private TextView orderInfoTextView;

    private TextView btn1View;
    private TextView btn2View;

    private Action btn1Action = Action.NONE;
    private Action btn2Action = Action.NONE;

    private OrderEntity currOrderEntity;
    private Delegate delegate;

    private enum Action {
        NONE, CANCEL, BUY_AGAIN, COMMIT, LOOK_COMMIT, RETRY, COMPLETED
    }

    public interface Delegate {
        void onCompleted(OrderEntity entity);

        void onBuyAgain(OrderEntity entity);

        void onCancel(OrderEntity entity);

        void onCommit(OrderEntity entity);

        void onLookCommit(OrderEntity entity);

        void onRetry(OrderEntity entity);
    }

    public OrderCell(Context context) {
        super(context);
        init(context);
    }

    public OrderCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        FrameLayout header = new FrameLayout(context);
        addView(header, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));


        orderNoTextView = new TextView(context);
        orderNoTextView.setTextColor(0xff212121);
        orderNoTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        orderNoTextView.setLines(1);
        orderNoTextView.setMaxLines(1);
        orderNoTextView.setSingleLine(true);
        orderNoTextView.setEllipsize(TextUtils.TruncateAt.END);
        orderNoTextView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(orderNoTextView);
        header.addView(orderNoTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 16, 8, 96, 0));

        orderDateTextView = new TextView(context);
        orderDateTextView.setTextColor(0xff757575);
        orderDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        orderDateTextView.setLines(1);
        orderDateTextView.setMaxLines(1);
        orderDateTextView.setSingleLine(true);
        orderDateTextView.setEllipsize(TextUtils.TruncateAt.END);
        orderDateTextView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(orderDateTextView);
        header.addView(orderDateTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.BOTTOM, 16, 0, 96, 6));


        orderStatusTextView = new TextView(context);
        orderStatusTextView.setTextColor(0xff212121);
        orderStatusTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        orderStatusTextView.setLines(1);
        orderStatusTextView.setMaxLines(1);
        orderStatusTextView.setSingleLine(true);
        orderStatusTextView.setEllipsize(TextUtils.TruncateAt.END);
        orderStatusTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(orderStatusTextView);
        header.addView(orderStatusTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));

        DividerCell divider = new DividerCell(context);
        addView(divider, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 0));

        goodsCell = new OrderGoodsSimpleCell(context);
        addView(goodsCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        goodsCountTextView = new TextView(context);
        goodsCountTextView.setTextColor(0xff757575);
        goodsCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        goodsCountTextView.setLines(1);
        goodsCountTextView.setMaxLines(1);
        goodsCountTextView.setSingleLine(true);
        goodsCountTextView.setEllipsize(TextUtils.TruncateAt.END);
        goodsCountTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(goodsCountTextView);
        addView(goodsCountTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 32, 16, 0, 16, 0));

        divider = new DividerCell(context);
        addView(divider, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 0));

        LinearLayout footer = new LinearLayout(context);
        footer.setOrientation(HORIZONTAL);
        footer.setGravity(Gravity.CENTER_VERTICAL);
        addView(footer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));

        orderInfoTextView = new TextView(context);
        orderInfoTextView.setTextColor(0xff212121);
        orderInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        orderInfoTextView.setLines(1);
        orderInfoTextView.setMaxLines(1);
        orderInfoTextView.setSingleLine(true);
        orderInfoTextView.setEllipsize(TextUtils.TruncateAt.END);
        orderInfoTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(orderInfoTextView);
        footer.addView(orderInfoTextView, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, 16, 0, 16, 0));


        btn1View = new TextView(context);
        btn1View.setTextColor(0xff212121);
        btn1View.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        btn1View.setLines(1);
        btn1View.setMaxLines(1);
        btn1View.setSingleLine(true);
        btn1View.setEllipsize(TextUtils.TruncateAt.END);
        btn1View.setGravity(Gravity.CENTER);
        btn1View.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        AndroidUtilities.setMaterialTypeface(btn1View);
        footer.addView(btn1View, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 32, 8, 0, 8, 0));

        btn2View = new TextView(context);
        btn2View.setTextColor(0xff212121);
        btn2View.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        btn2View.setLines(1);
        btn2View.setMaxLines(1);
        btn2View.setSingleLine(true);
        btn2View.setEllipsize(TextUtils.TruncateAt.END);
        btn2View.setGravity(Gravity.CENTER);
        btn2View.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        AndroidUtilities.setMaterialTypeface(btn2View);
        footer.addView(btn2View, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 32, 8, 0, 16, 0));

        ShadowSectionCell cell = new ShadowSectionCell(context);
        addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    public void setValue(OrderEntity orderEntity, Delegate delegate) {
        this.currOrderEntity = orderEntity;
        this.delegate = delegate;
        orderNoTextView.setText(String.format("订单编号: %s", orderEntity.orderNo));
        orderDateTextView.setText(String.format("%s 下单", orderEntity.createDate));

        boolean isNoPayOrder = false;
        final String orderStatus = orderEntity.orderStatusStr;
        int color = 0xff212121;
        if (TextUtils.equals("未付款", orderStatus)) {
            isNoPayOrder = true;
            btn1Action = Action.NONE;
            btn2Action = Action.CANCEL;
            color = getResources().getColor(R.color.md_red_500);
        } else if (TextUtils.equals("交易取消", orderStatus)) {
            btn1Action = Action.NONE;
            btn2Action = Action.RETRY;
            color = getResources().getColor(R.color.md_grey_400);
        } else if (TextUtils.equals("交易完成", orderStatus)) {
            btn1Action = Action.BUY_AGAIN;
            btn2Action = Action.COMMIT;
            color = 0xff212121;
        } else if (TextUtils.equals("已评价", orderStatus)) {
            btn1Action = Action.NONE;
            btn2Action = Action.NONE;// Action.LOOK_COMMIT;
            color = 0xff212121;
        } else {
            color = 0xff2baf2b;
            btn1Action = Action.CANCEL;
            btn2Action = Action.COMPLETED;
        }
        SpannableString statusSpan = new SpannableString(orderStatus);
        statusSpan.setSpan(new ForegroundColorSpan(color), 0, orderStatus.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        orderStatusTextView.setText(statusSpan);

        final int goodsCount = orderEntity.goodsList.size();
        OrderEntity.OrderGoodsEntity goodsEntity = goodsCount > 0 ? orderEntity.goodsList.get(0) : null;
        goodsCell.setValue(goodsEntity == null ? "" : goodsEntity.getIcon(), goodsEntity == null ? "" : goodsEntity.getName(), "", false);

        goodsCountTextView.setText(String.format("共 %d 个商品", goodsCount));

        SpannableStringBuilder orderInfoText = new SpannableStringBuilder();
        if (isNoPayOrder) {
            orderInfoText.append("应付款:");
        } else {
            orderInfoText.append("金额:");
        }
        CharSequence priceText = ShoppingHelper.formatPrice(orderEntity.orderPrice, false);
        orderInfoText.append(priceText);
        orderInfoTextView.setText(orderInfoText);
        formatBtn(btn1View, btn1Action);
        formatBtn(btn2View, btn2Action);
    }

    private void formatBtn(TextView btn, final Action action) {
        int textColor = 0xff757575;
        int backgroundResId = R.drawable.border_grey_selector;
        if (action == Action.COMPLETED) {
            textColor = ResourcesConfig.textPrimary;
            backgroundResId = R.drawable.btn_primary_border;
        }
        btn.setTextColor(textColor);
        btn.setBackgroundResource(backgroundResId);
        btn.setVisibility(action == Action.NONE ? View.GONE : View.VISIBLE);
        String text;
        if (action == Action.COMPLETED) {
            text = "确认收货";
        } else if (action == Action.BUY_AGAIN) {
            text = "再来一单";
        } else if (action == Action.CANCEL) {
            text = "取消订单";
        } else if (action == Action.COMMIT) {
            text = "评价";
        } else if (action == Action.LOOK_COMMIT) {
            text = "查看评价";
        } else if (action == Action.RETRY) {
            text = "重新购买";
        } else {
            text = "";
        }
        btn.setText(text);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delegate == null) {
                    return;
                }
                if (action == Action.COMPLETED) {
                    delegate.onCompleted(currOrderEntity);
                } else if (action == Action.BUY_AGAIN) {
                    delegate.onBuyAgain(currOrderEntity);
                } else if (action == Action.CANCEL) {
                    delegate.onCancel(currOrderEntity);
                } else if (action == Action.COMMIT) {
                    delegate.onCommit(currOrderEntity);
                } else if (action == Action.LOOK_COMMIT) {
                    delegate.onLookCommit(currOrderEntity);
                } else if (action == Action.RETRY) {
                    delegate.onRetry(currOrderEntity);
                }
            }
        });

    }
}
