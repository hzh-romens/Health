package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;

/**
 * Created by siery on 15/8/26.
 */
public class NewsTopCell extends FrameLayout {

    private TextView nameView;
    private BackupImageView iconView;

    private static Paint paint;
    private boolean needDivider = false;

    public NewsTopCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));

        iconView = new BackupImageView(context);
        iconView.setRoundRadius(2);
        addView(iconView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 140, Gravity.BOTTOM, 8, 0, 8, 0));

        FrameLayout nameContainer = new FrameLayout(context);
        nameContainer.setBackgroundColor(0x80000000);
        addView(nameContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM, 8, 0, 8, 0));
        nameView = new TextView(context);
        nameView.setTextColor(0xffffffff);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameView.setLines(2);
        nameView.setMaxLines(2);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setGravity((Gravity.LEFT) | Gravity.TOP);
        nameContainer.addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 8, 8, 8, 8));
    }

    public void setValue(String info, String iconUrl, boolean divider) {
        nameView.setText(info);
        iconView.setImage(iconUrl, null, null);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(156) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(16), getHeight() - 1, getWidth() - AndroidUtilities.dp(16), getHeight() - 1, paint);
        }
    }
}
