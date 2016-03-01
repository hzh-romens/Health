package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.ui.components.CheckableView;

/**
 * @author Zhou Lisi
 * @create 16/3/1
 * @description
 */
public class DoubleTextCheckCell extends FrameLayout {

    private TextView nameTextView;
    private TextView descTextView;
    private CheckableView checkBox;

    private static Paint paint;
    private boolean needDivider = false;

    public DoubleTextCheckCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        nameTextView = new TextView(context);
        nameTextView.setTextColor(0xff212121);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        nameTextView.setLines(1);
        nameTextView.setMaxLines(1);
        nameTextView.setSingleLine(true);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 16, 10, 48, 0));


        descTextView = new TextView(context);
        descTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        descTextView.setLines(1);
        descTextView.setMaxLines(1);
        descTextView.setSingleLine(true);
        descTextView.setEllipsize(TextUtils.TruncateAt.END);
        descTextView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(descTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 16, 33, 48, 0));

        checkBox = new CheckableView(context);
        checkBox.setClickable(false);
        addView(checkBox, LayoutHelper.createFrame(24, 24, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
    }

    public void setValue(CharSequence name, CharSequence desc, boolean checked, boolean divider) {
        nameTextView.setText(name);
        descTextView.setText(desc);
        checkBox.setChecked(checked);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }
}
