package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.model.MedicineServiceModeEntity;

import java.util.List;

/**
 * Created by siery on 15/12/16.
 */
public class MedicineServiceModesCell extends LinearLayout {
    private static Paint paint;
    private boolean needDivider;

    public MedicineServiceModesCell(Context context) {
        super(context);
        init(context);
    }

    public MedicineServiceModesCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MedicineServiceModesCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));

    }

    public void setValue(List<MedicineServiceModeEntity> value, boolean divider) {
        removeAllViews();
        for (MedicineServiceModeEntity item : value) {
            addItemCell(item);
        }

        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    private void addItemCell(MedicineServiceModeEntity item) {
        ServiceModeCell cell = new ServiceModeCell(getContext());
        addView(cell, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        cell.setValue(item.icon, item.color, item.text);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }
}
