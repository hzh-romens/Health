package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * @author Zhou Lisi
 * @create 16/2/27
 * @description
 */
public class ShoppingCartEmptyCell extends LinearLayout {

    public interface Delegate {
        void onNeedSearchGoods();
    }

    private Delegate delegate;

    public ShoppingCartEmptyCell(Context context) {
        super(context);
        init(context);
    }

    public ShoppingCartEmptyCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShoppingCartEmptyCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        TextView helperTextView = new TextView(context);
        helperTextView.setTextColor(0xff808080);
        helperTextView.setTextSize(20);
        helperTextView.setLineSpacing(AndroidUtilities.dp(4), 1.0f);
        helperTextView.setGravity(Gravity.CENTER);
        SpannableString helperSpan = new SpannableString("# 购物车是空的，您可以");
        Drawable shoppingCartIcon = getResources().getDrawable(R.drawable.ic_shopping_cart_grey600_24dp);
        shoppingCartIcon.setColorFilter(0xff808080, PorterDuff.Mode.SRC_ATOP);
        helperSpan.setSpan(new ImageSpan(shoppingCartIcon), 0, 1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        helperTextView.setText(helperSpan);
        addView(helperTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 32, 48, 32, 16));


        TextView searchView = new TextView(context);
        searchView.setBackgroundResource(R.drawable.border_grey_selector);
        searchView.setTextColor(0xff757575);
        searchView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        searchView.setLines(1);
        searchView.setMaxLines(1);
        searchView.setEllipsize(TextUtils.TruncateAt.END);
        searchView.setGravity(Gravity.CENTER);
        AndroidUtilities.setMaterialTypeface(searchView);
        searchView.setClickable(true);
        searchView.setText("添加商品");
        addView(searchView, LayoutHelper.createLinear(96, 40, 32, 16, 32, 32));
        searchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delegate != null) {
                    delegate.onNeedSearchGoods();
                }
            }
        });


    }

    public void setDelegate(Delegate d) {
        delegate = d;
    }
}
