package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.ui.components.AddSubView;
import com.romens.yjk.health.ui.components.CheckableFrameLayout;

import java.math.BigDecimal;

/**
 * @author Zhou Lisi
 * @create 16/2/25
 * @description
 */
public class ShoppingCartGoodsCell extends FrameLayout implements AddSubView.AddSubDelegate {

    private CheckableFrameLayout checkableView;
    private CloudImageView iconView;
    private TextView nameView;
    private TextView descView;
    private TextView priceView;

    private AddSubView addSubView;
    private TextView sumView;

    private static Paint paint;
    private boolean needDivider = false;
    private Delegate delegate;

    public interface Delegate {
        void onCheckableClick();

        void onGoodsCountChanged(int number);

        void onNeedInputGoodsCount();

        void onItemClick();
    }

    public ShoppingCartGoodsCell(Context context) {
        super(context);
        init(context);
    }

    public ShoppingCartGoodsCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShoppingCartGoodsCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setClickable(true);
        setBackgroundResource(R.drawable.list_selector);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delegate != null) {
                    delegate.onItemClick();
                }
            }
        });

        FrameLayout checkableContainer = new FrameLayout(context);
        checkableContainer.setClickable(true);
        addView(checkableContainer, LayoutHelper.createFrame(48, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.CENTER_VERTICAL));
        checkableView = new CheckableFrameLayout(context);
        checkableView.setBackgroundResource(R.drawable.btn_allcheck_selector);
        checkableContainer.addView(checkableView, LayoutHelper.createFrame(24, 24, Gravity.CENTER));
        checkableContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delegate != null) {
                    delegate.onCheckableClick();
                }
            }
        });
        iconView = CloudImageView.create(context);
        iconView.setPlaceholderImage(getResources().getDrawable(R.drawable.picture_fail));
        addView(iconView, LayoutHelper.createFrame(64, 64, Gravity.LEFT | Gravity.TOP, 56, 16, 8, 16));

        nameView = new TextView(context);
        nameView.setTextColor(0xff212121);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        nameView.setLines(1);
        nameView.setMaxLines(1);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setGravity((Gravity.LEFT) | Gravity.TOP);
        addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 136, 10, 16, 2));

        descView = new TextView(context);
        descView.setTextColor(0xff757575);
        descView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        descView.setLines(2);
        descView.setMaxLines(2);
        descView.setEllipsize(TextUtils.TruncateAt.END);
        addView(descView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 136, 32, 16, 2));

        priceView = new TextView(context);
        priceView.setTextColor(ResourcesConfig.priceFontColor);
        priceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        priceView.setSingleLine(true);
        priceView.setEllipsize(TextUtils.TruncateAt.END);
        priceView.setGravity((Gravity.LEFT) | Gravity.TOP);
        addView(priceView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 136, 0, 16, 48));

        FrameLayout bottom = new FrameLayout(context);
        addView(bottom, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 44, Gravity.BOTTOM | Gravity.LEFT, 136, 0, 16,0));
        addSubView = new AddSubView(context);
        addSubView.setShouldZero(false);
        bottom.addView(addSubView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL));

        sumView = new TextView(context);
        sumView.setTextColor(ResourcesConfig.priceFontColor);
        sumView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        sumView.setSingleLine(true);
        sumView.setEllipsize(TextUtils.TruncateAt.START);
        sumView.setGravity((Gravity.LEFT) | Gravity.TOP);
        bottom.addView(sumView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 0, 0, 144, 0));
    }

    public void setDelegate(Delegate d) {
        delegate = d;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(144) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(16), getHeight() - 1, getWidth() - AndroidUtilities.dp(16), getHeight() - 1, paint);
        }
    }

    public void setValue(boolean checked, String iconPath, CharSequence name, CharSequence desc, BigDecimal price, BigDecimal oldPrice, int count, boolean divider) {
        checkableView.setChecked(checked);
        iconView.setImagePath(iconPath);

        nameView.setText(name);
        descView.setText(desc);
        CharSequence priceText = ShoppingHelper.createPriceCompare(oldPrice, price);
        priceView.setText(priceText);

        addSubView.setVisibility(count);

        BigDecimal sum = price.multiply(new BigDecimal(count));
        SpannableStringBuilder sumText = new SpannableStringBuilder();
        sumText.append("小计:");
        sumText.append(ShoppingHelper.formatPrice(sum));
        sumView.setText(sumText);

        needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    public void onNumSelect() {
        if (delegate != null) {
            delegate.onNeedInputGoodsCount();
        }
    }

    @Override
    public boolean onNumberChanged(int number) {
        if (number <= 200) {
            if (delegate != null) {
                delegate.onGoodsCountChanged(number);
            }
            return true;
        }
        return false;
    }
}
