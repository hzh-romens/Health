package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    public CuoponCardCell(Context context) {
        super(context);
        if (mPaint == null) {
            mPaint = new Paint();
        }
        setWillNotDraw(false);
        cardView = new CardView(context);
        image = new ImageView(context);
        cardView.addView(image, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 8, Gravity.TOP));

        nameView = new TextView(context);
        nameView.setTextColor(context.getResources().getColor(R.color.theme_primary));
        nameView.setText("优惠券");
        nameView.setTextSize(16);
        cardView.addView(nameView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 16, 16, 8, 16));

        addressView = new TextView(context);
        addressView.setTextSize(16);
        addressView.setMaxLines(1);
        addressView.setEllipsize(TextUtils.TruncateAt.END);
        addressView.setTextColor(Color.BLACK);
        cardView.addView(addressView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.RIGHT, 64, 16, 8, 8));

        priceView = new TextView(context);
        priceView.setTextColor(context.getResources().getColor(R.color.theme_primary));
        cardView.addView(priceView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 48, Gravity.LEFT, 16, 56, 16, 16));

        conditionView = new TextView(context);
        conditionView.setTextColor(Color.BLACK);
        conditionView.setTextSize(16);
        cardView.addView(conditionView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT, 16, 80, 16, 64));

        timeView = new TextView(context);
        timeView.setTextSize(14);
        timeView.setTextColor(0xff666666);
        cardView.addView(timeView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 8, 16, 8));

        statusView = new TextView(context);
        statusView.setTextSize(14);
        statusView.setTextColor(0xff666666);
        cardView.addView(statusView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT, 8, 8, 16, 8));

        statusIcon = new ImageView(context);
        cardView.addView(statusIcon,LayoutHelper.createFrame(72,64,Gravity.RIGHT,16,24,16,0));

        addView(cardView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    public void setStatus(String flag) {
        if ("未使用".equals(flag)) {
            statusView.setText("已使用");
        } else if ("已使用".equals(flag)) {
            statusView.setText("已使用");
        } else {
            statusView.setText("已过期");
        }
    }

    public void setStatusImage(int resId) {
        statusIcon.setImageResource(resId);
        image.setImageResource(resId);
    }

    public void setPriceColor() {

    }

    public void setValue(String price) {
        SpannableString span = new SpannableString(price);
        span.setSpan(new AbsoluteSizeSpan(22), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new AbsoluteSizeSpan(18), 1, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        priceView.setText(span);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
