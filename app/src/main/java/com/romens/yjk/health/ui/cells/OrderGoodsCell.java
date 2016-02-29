package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.ShoppingHelper;

import java.math.BigDecimal;

/**
 * @author Zhou Lisi
 * @create 16/2/29
 * @description
 */
public class OrderGoodsCell extends FrameLayout {

    private CloudImageView iconView;
    private TextView nameView;
    private TextView descView;
    private TextView priceView;
    private TextView countView;

    private static Paint paint;
    private boolean needDivider = false;

    public OrderGoodsCell(Context context) {
        super(context);
        init(context);
    }

    public OrderGoodsCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderGoodsCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.HORIZONTAL);
        addView(content, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 0, 0, 8));

        iconView = CloudImageView.create(context);
        iconView.setPlaceholderImage(getResources().getDrawable(R.drawable.picture_fail));
        content.addView(iconView, LayoutHelper.createLinear(64, 64, 16, 8, 8, 8));

        LinearLayout descContainer = new LinearLayout(context);
        descContainer.setOrientation(LinearLayout.VERTICAL);
        content.addView(descContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 8, 8, 16, 8));
        nameView = new TextView(context);
        nameView.setTextColor(0xff212121);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameView.setLines(1);
        nameView.setMaxLines(1);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setGravity(Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(nameView);
        descContainer.addView(nameView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 4));


        descView = new TextView(context);
        descView.setTextColor(0xff757575);
        descView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        descView.setLines(2);
        descView.setMaxLines(2);
        descView.setEllipsize(TextUtils.TruncateAt.END);
        AndroidUtilities.setMaterialTypeface(descView);
        descContainer.addView(descView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 8));

        LinearLayout bottom = new LinearLayout(context);
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        descContainer.addView(bottom, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        countView = new TextView(context);
        countView.setTextColor(0xff757575);
        countView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        countView.setSingleLine(true);
        countView.setEllipsize(TextUtils.TruncateAt.END);
        countView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(countView);
        bottom.addView(countView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        priceView = new TextView(context);
        priceView.setTextColor(ResourcesConfig.priceFontColor);
        priceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        priceView.setSingleLine(true);
        priceView.setEllipsize(TextUtils.TruncateAt.END);
        priceView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(priceView);
        bottom.addView(priceView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(0, getHeight() - 1, getWidth() - AndroidUtilities.dp(16), getHeight() - 1, paint);
        }
    }

    public void setValue(String iconPath, CharSequence name, CharSequence desc, BigDecimal price, int count, boolean divider) {
        iconView.setImagePath(iconPath);

        nameView.setText(name);
        descView.setText(desc);
        CharSequence priceText = ShoppingHelper.formatPrice(price);
        priceView.setText(priceText);

        countView.setText(String.format("数量: %d", count));

        needDivider = divider;
        setWillNotDraw(!divider);
    }
}
