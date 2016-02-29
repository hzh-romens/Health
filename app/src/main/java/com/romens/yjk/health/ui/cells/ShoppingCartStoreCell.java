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
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.components.CheckableView;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class ShoppingCartStoreCell extends FrameLayout {
    private static Paint paint;
    private boolean needDivider = false;

    private CheckableView checkableView;
    private TextView nameView;
    private Delegate delegate;

    public interface Delegate {
        void onCheckableClick(boolean checked);
    }

    public ShoppingCartStoreCell(Context context) {
        super(context);
        init(context);
    }

    public ShoppingCartStoreCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShoppingCartStoreCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setDelegate(Delegate d) {
        delegate = d;
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        checkableView = new CheckableView(context);
        addView(checkableView, LayoutHelper.createFrame(56, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.CENTER_VERTICAL));
        checkableView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delegate != null) {
                    delegate.onCheckableClick(!checkableView.isChecked());
                }
            }
        });

        ImageView iconView = new ImageView(context);
        iconView.setScaleType(ImageView.ScaleType.CENTER);
        iconView.setImageResource(R.drawable.ic_store_mall_directory_grey600_24dp);
        addView(iconView, LayoutHelper.createFrame(24, 24, Gravity.LEFT | Gravity.CENTER_VERTICAL, 56, 0, 0, 0));

        nameView = new TextView(context);
        nameView.setTextColor(0xff212121);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameView.setLines(1);
        nameView.setMaxLines(1);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setGravity(Gravity.CENTER_VERTICAL);
        addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 104, 0, 16, 0));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }

    public void setValue(boolean checked, String name, boolean divider) {
        checkableView.setChecked(checked);
        nameView.setText(name);

        needDivider = divider;
        setWillNotDraw(!divider);
    }
}
