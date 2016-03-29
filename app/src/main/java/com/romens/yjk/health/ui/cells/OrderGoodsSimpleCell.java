package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;

/**
 * @author Zhou Lisi
 * @create 2016-03-29 15:09
 * @description
 */
public class OrderGoodsSimpleCell extends LinearLayout {

    private CloudImageView iconView;
    private TextView nameView;
    private TextView descView;

    private static Paint paint;
    private boolean needDivider = false;

    public OrderGoodsSimpleCell(Context context) {
        super(context);
        init(context);
    }

    public OrderGoodsSimpleCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderGoodsSimpleCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(LinearLayout.HORIZONTAL);

        iconView = CloudImageView.create(context);
        iconView.setRound(AndroidUtilities.dp(4));
        iconView.setPlaceholderImage(getResources().getDrawable(R.drawable.picture_fail));
        addView(iconView, LayoutHelper.createLinear(48, 48, 16, 8, 8, 8));

        LinearLayout descContainer = new LinearLayout(context);
        descContainer.setOrientation(LinearLayout.VERTICAL);
        addView(descContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 8, 8, 16, 8));
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(0, getHeight() - 1, getWidth() - AndroidUtilities.dp(16), getHeight() - 1, paint);
        }
    }

    public void setValue(String iconPath, CharSequence name, CharSequence desc, boolean divider) {
        iconView.setImagePath(iconPath);

        nameView.setText(name);
        descView.setText(desc);

        needDivider = divider;
        setWillNotDraw(!divider);
    }
}
