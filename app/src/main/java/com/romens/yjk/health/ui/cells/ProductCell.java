package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.FormatHelper;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by siery on 15/8/14.
 */
public class ProductCell extends LinearLayout {
    public static final int defaultSize = 172;
    private BackupImageView iconView;
    private TextView nameView;
    private TextView priceView;
    //private LayoutStyle layoutStyle = LayoutStyle.DEFAULT;

    private ProductCellDelegate cellDelegate;

    private DecimalFormat decimalFormat = new DecimalFormat(FormatHelper.priceFormat);

    public enum LayoutStyle {
        DEFAULT, SMALL
    }

    private Bundle arguments;

    public ProductCell(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);
        setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        iconView = new BackupImageView(context);
        iconView.setRoundRadius(4);
        addView(iconView, LayoutHelper.createLinear(80, 80, Gravity.CENTER_HORIZONTAL));

        nameView = new TextView(context);
        nameView.setTextColor(0xff212121);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setLines(2);
        nameView.setMaxLines(2);
        addView(nameView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 4, 0, 2));
        priceView = new TextView(context);
        priceView.setTextColor(0xff0f9d58);
        priceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        priceView.setSingleLine(true);
        priceView.setEllipsize(TextUtils.TruncateAt.END);
        priceView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        addView(priceView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 2, 0, 0));

        setClickable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cellDelegate != null) {
                    cellDelegate.onCellClick(arguments);
                }
            }
        });
    }

    public void setValue(String iconUrl, CharSequence name, String oldPrice, String price) {
        setValue(iconUrl, name, oldPrice, price, null);
    }

    public void setValue(String iconUrl, CharSequence name, String oldPrice, String price, Bundle arguments) {
        nameView.setText(name);
        CharSequence priceStr = formatPrice(oldPrice, price);
        priceView.setText(priceStr);
        iconView.setImage(iconUrl, null, null);
    }

    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }

    public Bundle getArguments() {
        return this.arguments;
    }

//    public void setLayoutStyle(LayoutStyle style) {
//        layoutStyle = style;
//        int nameTextSize;
//        int descTxtSize;
//        if (layoutStyle == LayoutStyle.SMALL) {
//            nameTextSize = 14;
//            descTxtSize = 12;
//        } else {
//            nameTextSize = 16;
//            descTxtSize = 14;
//        }
//
//        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nameTextSize);
//        priceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, descTxtSize);
//        invalidate();
//    }
//
//    public LayoutStyle getLayoutStyle() {
//        return layoutStyle;
//    }

    private CharSequence formatPrice(String oldPriceStr, String priceStr) {
        if (TextUtils.isEmpty(oldPriceStr)) {
            BigDecimal price = TextUtils.isEmpty(priceStr) ? BigDecimal.ZERO : new BigDecimal(priceStr);
            String formatPrice = decimalFormat.format(price);

            SpannableString spannableString = new SpannableString(formatPrice);
            int length = formatPrice.length();
            spannableString.setSpan(new ForegroundColorSpan(ResourcesConfig.priceFontColor), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            BigDecimal oldPrice = TextUtils.isEmpty(oldPriceStr) ? BigDecimal.ZERO : new BigDecimal(oldPriceStr);
            String formatOlPrice = decimalFormat.format(oldPrice);

            BigDecimal price = TextUtils.isEmpty(priceStr) ? BigDecimal.ZERO : new BigDecimal(priceStr);
            String formatPrice = decimalFormat.format(price);

            SpannableStringBuilder priceSpanBuilder = new SpannableStringBuilder();

            //current price
            SpannableString priceSpan = new SpannableString(formatPrice);
            int length = formatPrice.length();
            priceSpan.setSpan(new ForegroundColorSpan(ResourcesConfig.priceFontColor), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceSpanBuilder.append(priceSpan);
            //divider
            priceSpanBuilder.append(" ");
            //old price
            SpannableString oldPriceSpan = new SpannableString(formatOlPrice);
            oldPriceSpan.setSpan(new StrikethroughSpan(), 0, formatOlPrice.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            oldPriceSpan.setSpan(new ForegroundColorSpan(ResourcesConfig.bodyText3), 0, formatOlPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceSpanBuilder.append(oldPriceSpan);
            return priceSpanBuilder;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(defaultSize), View.MeasureSpec.EXACTLY));
    }
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int measureHeight = measureWidth + AndroidUtilities.dp(76);
//        int count = getChildCount();
//
//        for (int index = 0; index < count; index++) {
//            final View child = getChildAt(index);
//            if (child instanceof BackupImageView) {
//                if (child.getVisibility() != GONE) {
//                    measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth - AndroidUtilities.dp(8), MeasureSpec.EXACTLY),
//                            MeasureSpec.makeMeasureSpec(measureWidth - AndroidUtilities.dp(8), MeasureSpec.EXACTLY));
//                }
//            }
//        }
//        setMeasuredDimension(measureWidth, measureHeight);
//    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//        int totalWidth = getMeasuredWidth();
//        int totalHeight = getMeasuredHeight();
//        int count = getChildCount();
//
//        int y = 0;
//        for (int index = 0; index < count; index++) {
//            View view = getChildAt(index);
//            if (view instanceof BackupImageView) {
//                y = view.getMeasuredHeight() + AndroidUtilities.dp(8);
//                view.layout(AndroidUtilities.dp(4), AndroidUtilities.dp(4), totalWidth - AndroidUtilities.dp(4), y);
//            } else if (view instanceof LinearLayout) {
//                //view.layout(l,t,r,b);
//                view.layout(0, y, totalWidth, totalHeight - AndroidUtilities.dp(8));
//            }
//        }
//    }

    public void setProductCellDelegate(ProductCellDelegate delegate) {
        cellDelegate = delegate;
    }

    public interface ProductCellDelegate {
        void onCellClick(Bundle arguments);
    }
}
