package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.pay.PayState;

/**
 * @author Zhou Lisi
 * @create 16/3/4
 * @description 支付状态Cell
 */
public class PayStateCell extends FrameLayout {
    private ImageView stateImgView;
    private TextView stateTextView;

    private static Paint paint;
    private boolean needDivider = false;

    public PayStateCell(Context context) {
        super(context);

        stateImgView = new ImageView(context);
        stateImgView.setScaleType(ImageView.ScaleType.CENTER);
        addView(stateImgView, LayoutHelper.createFrame(48, 48, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 0, 16, 0));

        stateTextView = new TextView(context);
        stateTextView.setTextColor(0xff212121);
        stateTextView.setSingleLine(true);
        stateTextView.setGravity(Gravity.CENTER);
        stateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        addView(stateTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 64, 0, 16, 0));
    }

    public void setValue(PayState state, boolean divider) {
        int stateColor;
        String stateText;
        if (state == PayState.SUCCESS) {
            stateColor=ResourcesConfig.primaryColor;
            stateText = "支付成功";
        } else if (state == PayState.PROCESSING) {
            stateColor=ResourcesConfig.primaryColor;
            stateText = "支付处理中...";
        } else {
            stateColor=ResourcesConfig.primaryColor;
            stateText = "支付失败";
        }
        stateImgView.setColorFilter(stateColor);
        stateTextView.setTextColor(stateColor);
        stateTextView.setText(stateText);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(76), getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }
}
