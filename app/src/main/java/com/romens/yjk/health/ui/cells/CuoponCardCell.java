package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/1/27.
 */
public class CuoponCardCell extends FrameLayout {
    private CardView cardView;
    private TextView nameView, addressView, priceView, conditionView, timeView, statusView;
    private Paint mPaint;
    private ImageView image, statusIcon;
    private Context mContext;

    public CuoponCardCell(Context context) {
        super(context);
        if (mPaint == null) {
            mPaint = new Paint();
        }
        this.mContext = context;
        setWillNotDraw(false);
        cardView = new CardView(context);
        cardView.setRadius(10.0f);
        cardView.setCardElevation(4);
        image = new ImageView(context);
        cardView.addView(image, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 8, Gravity.TOP));

        nameView = new TextView(context);
        nameView.setTextColor(context.getResources().getColor(R.color.theme_primary));
        nameView.setText("优惠券");
        nameView.setTextSize(18);
        cardView.addView(nameView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 16, 16, 8, 16));

        addressView = new TextView(context);
        addressView.setTextSize(18);
        addressView.setMaxLines(1);
        addressView.setEllipsize(TextUtils.TruncateAt.END);
        addressView.setTextColor(Color.BLACK);
        cardView.addView(addressView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT, 64, 16, 8, 8));

        priceView = new TextView(context);
        priceView.setTextColor(context.getResources().getColor(R.color.theme_primary));
        priceView.setTextSize(30);
        cardView.addView(priceView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 48, Gravity.LEFT, 16, 56, 16, 16));

        conditionView = new TextView(context);
        conditionView.setTextColor(Color.BLACK);
        conditionView.setTextSize(16);
        cardView.addView(conditionView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT, 16, 80, 16, 64));

        timeView = new TextView(context);
        timeView.setTextSize(14);
        timeView.setTextColor(0xff666666);
        timeView.setMaxLines(1);
        timeView.setEllipsize(TextUtils.TruncateAt.END);
        cardView.addView(timeView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 8, 48, 8));

        statusView = new TextView(context);
        statusView.setTextSize(14);
        statusView.setTextColor(0xff666666);
        cardView.addView(statusView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT, 8, 8, 16, 8));

        statusIcon = new ImageView(context);
        cardView.addView(statusIcon, LayoutHelper.createFrame(96, 96, Gravity.RIGHT, 16, 24, 16, 0));

        addView(cardView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.NO_GRAVITY, 16, 8, 16, 8));
    }

    public void setStatus(String flag) {
        //未使用字体颜色:#aaaaaa
        if ("1".equals(flag)) {
            statusView.setText("已使用");
            setStatusImage(R.drawable.ic_cuopon_used);
            setHistoryTextColor();
        } else if ("0".equals(flag)) {
            statusView.setText("未使用");
            setTextColor();
        } else {
            statusView.setText("已过期");
            setStatusImage(R.drawable.ic_coupon_expired);
            setHistoryTextColor();
        }
    }

    public void setStatusImage(int resId) {
        // statusIcon.setImageResource(resId);
        statusIcon.setBackgroundResource(resId);
        //image.setImageResource(resId);
    }


    private String mIsused, mName, mEnddate, mLimitamount, mPrice;

    public void setValue(String isused, String name, String enddate, String limitamount, String shuoming, String amount, String startDate) {
        SpannableString span = new SpannableString("¥" + amount);
        span.setSpan(new AbsoluteSizeSpan(30), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        priceView.setText(span);
        nameView.setText(name);
        if (TextUtils.isEmpty(shuoming))
            addressView.setText(shuoming);
        conditionView.setText("满" + limitamount + "可用");
        timeView.setText("有效期" + startDate + "-" + enddate);
        this.mIsused = isused;
        this.mEnddate = enddate;
        this.mName = name;
        this.mLimitamount = limitamount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setTextColor() {
        nameView.setTextColor(mContext.getResources().getColor(R.color.theme_primary));
        priceView.setTextColor(mContext.getResources().getColor(R.color.theme_primary));
        addressView.setTextColor(Color.BLACK);
        conditionView.setTextColor(Color.BLACK);
    }

    public void setHistoryTextColor() {
        nameView.setTextColor(0xff666666);
        priceView.setTextColor(0xff666666);
        addressView.setTextColor(0xff666666);
        conditionView.setTextColor(0xff666666);
    }

    public void setShapeColor(boolean choice) {
        if (choice) {
            GradientDrawable dra = new GradientDrawable();
            dra.setCornerRadius(10.f);
            dra.setStroke(4, mContext.getResources().getColor(R.color.theme_primary));
            cardView.setBackground(dra);
            cardView.setCardElevation(4);
        } else {
            cardView.setCardElevation(4);
        }
    }
}
