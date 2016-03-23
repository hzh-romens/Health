package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/3/23.
 */
public class CuoponEmptyCell extends LinearLayout {

    public CuoponEmptyCell(Context context) {
        super(context);
        init(context);
    }

    public CuoponEmptyCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CuoponEmptyCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.ic_cuopon);
        addView(imageView, LayoutHelper.createLinear(64, 64, Gravity.CENTER_HORIZONTAL, 0, 32, 0, 0));
        TextView helpView = new TextView(context);
        helpView.setTextColor(0xff808080);
        helpView.setTextSize(20);
        helpView.setLineSpacing(AndroidUtilities.dp(4), 1.0f);
        helpView.setGravity(Gravity.CENTER);
        SpannableString helperSpan = new SpannableString("您暂时没有可以使用的优惠卷");
        Drawable shoppingCartIcon = getResources().getDrawable(R.drawable.ic_shopping_cart_grey600_24dp);
        shoppingCartIcon.setColorFilter(0xff808080, PorterDuff.Mode.SRC_ATOP);
        helperSpan.setSpan(new ImageSpan(shoppingCartIcon), 0, 1, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        helpView.setText(helperSpan);
        addView(helpView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 32, 16, 32, 16));
    }
}