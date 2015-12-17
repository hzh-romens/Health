package com.romens.yjk.health.helper;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.romens.yjk.health.config.ResourcesConfig;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by siery on 15/12/17.
 */
public class ShoppingHelper {
    public static SpannableString formatPrice(BigDecimal price) {
        DecimalFormat decimalFormat = new DecimalFormat("￥ #.00");
        String priceStr = decimalFormat.format(price);
        SpannableString spannableString = new SpannableString(priceStr);
        int length = priceStr.length();
        spannableString.setSpan(new ForegroundColorSpan(ResourcesConfig.priceFontColor), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static CharSequence createPriceInfo(BigDecimal marketPrice, BigDecimal userPrice) {
        if (marketPrice.compareTo(userPrice) == 0) {
            return null;
        }
        BigDecimal value = marketPrice.subtract(userPrice);
        if (value.compareTo(BigDecimal.ZERO) == 1) {
            value = value.divide(marketPrice, 2, BigDecimal.ROUND_HALF_EVEN);
            int valueInt = (int) value.doubleValue() * 100;
            String text = "价格下降了";
            int start = text.length();
            text = String.format("%s %d%%", text, valueInt);
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new ForegroundColorSpan(ResourcesConfig.priceFontColor), start, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
        return null;
    }

    public static CharSequence createMemberPriceInfo(BigDecimal price) {
        SpannableStringBuilder spannable = new SpannableStringBuilder();
        spannable.append("会员价格 ");
        spannable.append(formatPrice(price));
        return spannable;
    }
}
