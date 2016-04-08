package com.romens.yjk.health.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by siery on 15/12/18.
 */
public class FormatHelper {
    public static final String priceFormat = "￥#,##0.00";

    public static String formatPrice(BigDecimal price) {
        DecimalFormat decimalFormat = new DecimalFormat(priceFormat);
        String priceStr = decimalFormat.format(price);
        return priceStr;
    }

    public static String format(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String valueStr = decimalFormat.format(value);
        return valueStr;
    }

    public static List<String> stringToList(String value) {
        if (value == null || value.length() <= 0) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(value);
        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }
}
