package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.helper.ShoppingHelper;

import java.math.BigDecimal;

/**
 * @author Zhou Lisi
 * @create 16/3/1
 * @description 提交订单，订单概要Cell
 */
public class OrderInfoCell extends LinearLayout {
    private TextView deliveryView;
    private TextView goodsAmountView;
    private TextView shippingView;
    private TextView couponView;
    private TextView orderAmountView;

    public OrderInfoCell(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        deliveryView = new TextView(context);
        deliveryView.setTextColor(0xff8a8a8a);
        deliveryView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        deliveryView.setLines(2);
        deliveryView.setMaxLines(2);
        deliveryView.setEllipsize(TextUtils.TruncateAt.END);
        deliveryView.setGravity(Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(deliveryView);
        addView(deliveryView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 8, 16, 0));

        //商品合计
        FrameLayout goodsAmountContainer = new FrameLayout(context);
        addView(goodsAmountContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 32, 16, 0, 16, 0));

        TextView goodsAmountCaptionView = new TextView(context);
        goodsAmountCaptionView.setTextColor(0xff8a8a8a);
        goodsAmountCaptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        goodsAmountCaptionView.setLines(1);
        goodsAmountCaptionView.setMaxLines(1);
        goodsAmountCaptionView.setSingleLine(true);
        goodsAmountCaptionView.setEllipsize(TextUtils.TruncateAt.END);
        goodsAmountCaptionView.setGravity(Gravity.CENTER_VERTICAL);
        goodsAmountCaptionView.setText("商品金额");
        AndroidUtilities.setMaterialTypeface(goodsAmountCaptionView);
        goodsAmountContainer.addView(goodsAmountCaptionView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL));

        goodsAmountView = new TextView(context);
        goodsAmountView.setTextColor(0xff212121);
        goodsAmountView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        goodsAmountView.setLines(1);
        goodsAmountView.setMaxLines(1);
        goodsAmountView.setSingleLine(true);
        goodsAmountView.setEllipsize(TextUtils.TruncateAt.END);
        goodsAmountView.setGravity(Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(goodsAmountView);
        goodsAmountContainer.addView(goodsAmountView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL));


        //配送费
        FrameLayout shippingContainer = new FrameLayout(context);
        addView(shippingContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 32, 16, 0, 16, 0));

        TextView shippingCaptionView = new TextView(context);
        shippingCaptionView.setTextColor(0xff8a8a8a);
        shippingCaptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        shippingCaptionView.setLines(1);
        shippingCaptionView.setMaxLines(1);
        shippingCaptionView.setSingleLine(true);
        shippingCaptionView.setEllipsize(TextUtils.TruncateAt.END);
        shippingCaptionView.setGravity(Gravity.CENTER_VERTICAL);
        shippingCaptionView.setText("配送费 +");
        AndroidUtilities.setMaterialTypeface(shippingCaptionView);
        shippingContainer.addView(shippingCaptionView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL));

        shippingView = new TextView(context);
        shippingView.setTextColor(0xff212121);
        shippingView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        shippingView.setLines(1);
        shippingView.setMaxLines(1);
        shippingView.setSingleLine(true);
        shippingView.setEllipsize(TextUtils.TruncateAt.END);
        shippingView.setGravity(Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(shippingView);
        shippingContainer.addView(shippingView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL));



        //优惠
        FrameLayout couponContainer = new FrameLayout(context);
        addView(couponContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 32, 16, 0, 16, 0));

        TextView couponCaptionView = new TextView(context);
        couponCaptionView.setTextColor(0xff8a8a8a);
        couponCaptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        couponCaptionView.setLines(1);
        couponCaptionView.setMaxLines(1);
        couponCaptionView.setSingleLine(true);
        couponCaptionView.setEllipsize(TextUtils.TruncateAt.END);
        couponCaptionView.setGravity(Gravity.CENTER_VERTICAL);
        couponCaptionView.setText("优惠金额 -");
        AndroidUtilities.setMaterialTypeface(couponCaptionView);
        couponContainer.addView(couponCaptionView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL));

        couponView = new TextView(context);
        couponView.setTextColor(0xff212121);
        couponView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        couponView.setLines(1);
        couponView.setMaxLines(1);
        couponView.setSingleLine(true);
        couponView.setEllipsize(TextUtils.TruncateAt.END);
        couponView.setGravity(Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(couponView);
        couponContainer.addView(couponView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL));


        View amountDivider = new View(context);
        amountDivider.setBackgroundColor(0xffd9d9d9);
        addView(amountDivider, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 1, 16, 0, 16, 0));

        //优惠
        FrameLayout orderAmountContainer = new FrameLayout(context);
        addView(orderAmountContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 40, 16, 0, 16, 0));

        TextView orderAmountCaptionView = new TextView(context);
        orderAmountCaptionView.setTextColor(0xff212121);
        orderAmountCaptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        orderAmountCaptionView.setLines(1);
        orderAmountCaptionView.setMaxLines(1);
        orderAmountCaptionView.setSingleLine(true);
        orderAmountCaptionView.setEllipsize(TextUtils.TruncateAt.END);
        orderAmountCaptionView.setGravity(Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(orderAmountCaptionView);
        orderAmountCaptionView.setText("订单合计");
        orderAmountContainer.addView(orderAmountCaptionView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL));

        orderAmountView = new TextView(context);
        orderAmountView.setTextColor(0xff212121);
        orderAmountView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        orderAmountView.setLines(1);
        orderAmountView.setMaxLines(1);
        orderAmountView.setSingleLine(true);
        orderAmountView.setEllipsize(TextUtils.TruncateAt.END);
        orderAmountView.setGravity(Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(orderAmountView);
        orderAmountContainer.addView(orderAmountView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL));

    }

    public void setValue(String deliveryType, String name, String address, BigDecimal goodsAmount,BigDecimal shippingAmount, BigDecimal couponAmount,BigDecimal orderAmount) {
        //配送
        SpannableStringBuilder deliveryString = new SpannableStringBuilder();
        deliveryString.append("配送: ");
        SpannableString deliveryTypeSpan = new SpannableString(deliveryType);
        deliveryTypeSpan.setSpan(new ForegroundColorSpan(0xff2baf2b), 0, deliveryTypeSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        deliveryTypeSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, deliveryTypeSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        deliveryString.append(deliveryTypeSpan);
        deliveryString.append(" ");

        if (!TextUtils.isEmpty(name)) {
            String addressString = String.format("%s,%s", name, address);
            SpannableString addressSpan = new SpannableString(addressString);
            addressSpan.setSpan(new ForegroundColorSpan(0xff212121), 0, addressSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            addressSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, addressSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            deliveryString.append(addressSpan);
        }

        deliveryView.setText(deliveryString);
        //商品
        goodsAmountView.setText(ShoppingHelper.formatPrice(goodsAmount, false));
        //配送费
        shippingView.setText(ShoppingHelper.formatPrice(shippingAmount,false));
        //优惠
        couponView.setText(ShoppingHelper.formatPrice(couponAmount, "-￥", false));

        orderAmountView.setText(ShoppingHelper.formatPrice(orderAmount));
    }
}
