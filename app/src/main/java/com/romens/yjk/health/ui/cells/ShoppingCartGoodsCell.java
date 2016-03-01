package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.ui.components.AddSubView;
import com.romens.yjk.health.ui.components.CheckableView;

import java.math.BigDecimal;

/**
 * @author Zhou Lisi
 * @create 16/2/25
 * @description
 */
public class ShoppingCartGoodsCell extends FrameLayout implements AddSubView.AddSubDelegate {

    private CheckableView checkableView;
    private CloudImageView iconView;
    private TextView nameView;
    private TextView descView;
    private TextView priceView;

    private AddSubView addSubView;

    private static Paint paint;
    private boolean needDivider = false;
    private Delegate delegate;

    public interface Delegate {
        void onCheckableClick(boolean checked);

        void onGoodsCountChanged(int number);

        void onNeedInputGoodsCount();

        void onItemClick();

        void onDelete();
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

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.HORIZONTAL);
        addView(content, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 0, 0, 56));

        checkableView = new CheckableView(context);
        content.addView(checkableView, LayoutHelper.createLinear(56, 64, Gravity.CENTER_VERTICAL));
        checkableView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delegate != null) {
                    delegate.onCheckableClick(!checkableView.isChecked());
                }
            }
        });
        iconView = CloudImageView.create(context);
        iconView.setPlaceholderImage(getResources().getDrawable(R.drawable.picture_fail));
        content.addView(iconView, LayoutHelper.createLinear(64, 64, 0, 8, 8, 8));

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

        priceView = new TextView(context);
        priceView.setTextColor(ResourcesConfig.priceFontColor);
        priceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        priceView.setSingleLine(true);
        priceView.setEllipsize(TextUtils.TruncateAt.END);
        priceView.setGravity((Gravity.LEFT) | Gravity.TOP);
        AndroidUtilities.setMaterialTypeface(priceView);
        descContainer.addView(priceView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        LinearLayout bottom = new LinearLayout(context);
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        bottom.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        addView(bottom, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM, 16, 0, 16, 8));

        final TextView deleteBtn = new TextView(context);
        deleteBtn.setTextColor(0xff757575);
        deleteBtn.setClickable(true);
        deleteBtn.setBackgroundResource(R.drawable.border_grey_selector);
        deleteBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        deleteBtn.setSingleLine(true);
        deleteBtn.setGravity(Gravity.CENTER);
        deleteBtn.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(2), AndroidUtilities.dp(16), AndroidUtilities.dp(2));
        deleteBtn.setText("删除");
        AndroidUtilities.setMaterialTypeface(deleteBtn);
        bottom.addView(deleteBtn, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 32, 0, 0, 16, 0));

        addSubView = new AddSubView(context);
        addSubView.setShouldZero(false);
        addSubView.setAddSubDelegate(this);
        bottom.addView(addSubView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(delegate!=null){
                    delegate.onDelete();
                }
            }
        });
    }

    public void setDelegate(Delegate d) {
        delegate = d;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(136) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(0, getHeight() - 1, getWidth() - AndroidUtilities.dp(16), getHeight() - 1, paint);
        }
    }

    public void setValue(boolean checked, String iconPath, CharSequence name, CharSequence desc, BigDecimal price, BigDecimal oldPrice, int count, boolean divider) {
        checkableView.setChecked(checked);
        iconView.setImagePath(iconPath);

        nameView.setText(name);
        descView.setText(desc);
        CharSequence priceText = ShoppingHelper.createPriceCompare(oldPrice, price);
        priceView.setText(priceText);

        addSubView.setValue(count);

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
    public void onNumberChanged(int number) {
        if (number > 200) {
            number = 200;
            addSubView.setValue(number);
        }
        if (delegate != null) {
            delegate.onGoodsCountChanged(number);
        }
    }
}
