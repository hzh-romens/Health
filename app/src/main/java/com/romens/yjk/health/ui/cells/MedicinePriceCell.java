package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.FormatHelper;
import com.romens.yjk.health.helper.ShoppingHelper;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by siery on 15/12/15.
 */
public class MedicinePriceCell extends LinearLayout {

    private TextView saleCountView;
    private TextView marketPriceView;
    private TextView userPriceView;
    private static Paint paint;
    private boolean needDivider;

    public MedicinePriceCell(Context context) {
        super(context);
        init(context);
    }

    public MedicinePriceCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MedicinePriceCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);


        userPriceView = new TextView(context);
        userPriceView.setTextColor(ResourcesConfig.bodyText2);
        userPriceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        userPriceView.setLines(1);
        userPriceView.setMaxLines(1);
        userPriceView.setSingleLine(true);
        userPriceView.setGravity(Gravity.LEFT);
        addView(userPriceView, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) userPriceView.getLayoutParams();
        layoutParams.weight = 1;
        userPriceView.setLayoutParams(layoutParams);

        saleCountView = new TextView(context);
        saleCountView.setTextColor(0xff8a8a8a);
        saleCountView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        saleCountView.setLines(1);
        saleCountView.setMaxLines(1);
        saleCountView.setSingleLine(true);
        saleCountView.setGravity(Gravity.LEFT);
        addView(saleCountView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 16, 0, 16, 0));


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
    }

    public void setValue(BigDecimal marketPrice, BigDecimal userPrice, int saleCount, boolean divider) {

        SpannableStringBuilder priceText = new SpannableStringBuilder();
        CharSequence userPriceStr = ShoppingHelper.formatPrice(userPrice);
        priceText.append(userPriceStr);
        if (userPrice.compareTo(marketPrice) != 0) {
            priceText.append("  ");
            CharSequence marketPriceStr = ShoppingHelper.formatPrice(marketPrice, false);
            SpannableString marketPriceSpan = new SpannableString(marketPriceStr);
            StrikethroughSpan span = new StrikethroughSpan();
            marketPriceSpan.setSpan(span, 0, marketPriceStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceText.append(marketPriceSpan);
        }
        userPriceView.setText(priceText);
        String saleCountStr = String.format("已售 %d", saleCount);
        saleCountView.setText(saleCountStr);
        saleCountView.setVisibility(saleCount <= 0 ? View.INVISIBLE : View.VISIBLE);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}
