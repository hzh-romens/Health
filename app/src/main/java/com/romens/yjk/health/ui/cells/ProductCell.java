package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;

/**
 * Created by siery on 15/8/14.
 */
public class ProductCell extends CardView {
    private BackupImageView iconView;

    private LinearLayout content;
    private TextView nameView;
    private TextView priceView;
    private LayoutStyle layoutStyle = LayoutStyle.DEFAULT;

    private ProductCellDelegate cellDelegate;

    public enum LayoutStyle {
        DEFAULT, SMALL
    }

    public ProductCell(Context context) {
        super(context);
        setRadius(4);
        setCardBackgroundColor(Color.WHITE);
        setPadding(AndroidUtilities.dp(4), AndroidUtilities.dp(4), AndroidUtilities.dp(4), AndroidUtilities.dp(4));
        iconView = new BackupImageView(context);
        iconView.setRoundRadius(4);
        addView(iconView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));


        content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        addView(content, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        nameView = new TextView(context);
        nameView.setTextColor(0xff212121);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setLines(2);
        nameView.setMaxLines(2);
        content.addView(nameView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        priceView = new TextView(context);
        priceView.setTextColor(0xff0f9d58);
        priceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        priceView.setSingleLine(true);
        priceView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        content.addView(priceView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        setClickable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cellDelegate != null) {
                    cellDelegate.onCellClick();
                }
            }
        });
    }

    public void setValue(String iconUrl, CharSequence name, String oldPrice, String price) {
        nameView.setText(name);
        CharSequence priceStr = formatPrice(oldPrice, price);
        priceView.setText(priceStr);
        iconView.setImage(iconUrl, null, null);
    }

    public void setLayoutStyle(LayoutStyle style) {
        layoutStyle = style;
        int nameTextSize;
        int descTxtSize;
        if (layoutStyle == LayoutStyle.SMALL) {
            nameTextSize = 14;
            descTxtSize = 12;
        } else {
            nameTextSize = 16;
            descTxtSize = 14;
        }

        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nameTextSize);
        priceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, descTxtSize);
        invalidate();
    }

    public LayoutStyle getLayoutStyle() {
        return layoutStyle;
    }

    private CharSequence formatPrice(String oldPrice, String price) {
        if (TextUtils.isEmpty(oldPrice)) {
            return String.format("￥%s", price);
        } else {
            String oldPriceStr = String.format("￥%s", oldPrice);
            String priceStr = String.format("￥%s", price);
            String priceTemp = String.format("%s %s", oldPriceStr, priceStr);
            final int oldPriceLength = oldPriceStr.length();
            SpannableString spannableString = new SpannableString(priceTemp);
            ForegroundColorSpan span = new ForegroundColorSpan(0x60000000);
            spannableString.setSpan(span, 0, oldPriceLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StrikethroughSpan(), 0, oldPriceLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = measureWidth + AndroidUtilities.dp(76);
        int count = getChildCount();

        for (int index = 0; index < count; index++) {
            final View child = getChildAt(index);
            if (child instanceof BackupImageView) {
                if (child.getVisibility() != GONE) {
                    measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth - AndroidUtilities.dp(8), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(measureWidth - AndroidUtilities.dp(8), MeasureSpec.EXACTLY));
                }
            }
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int totalWidth = getMeasuredWidth();
        int totalHeight = getMeasuredHeight();
        int count = getChildCount();

        int y = 0;
        for (int index = 0; index < count; index++) {
            View view = getChildAt(index);
            if (view instanceof BackupImageView) {
                y = view.getMeasuredHeight() + AndroidUtilities.dp(8);
                view.layout(AndroidUtilities.dp(4), AndroidUtilities.dp(4), totalWidth - AndroidUtilities.dp(4), y);
            } else if (view instanceof LinearLayout) {
                //view.layout(l,t,r,b);
                view.layout(0, y, totalWidth, totalHeight - AndroidUtilities.dp(8));
            }
        }
    }

    public void setProductCellDelegate(ProductCellDelegate delegate) {
        cellDelegate = delegate;
    }

    public interface ProductCellDelegate {
        void onCellClick();
    }
}
