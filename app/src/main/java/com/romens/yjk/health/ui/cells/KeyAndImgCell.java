package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/10/16.
 */
public class KeyAndImgCell extends LinearLayout {

    private TextView leftTextView;
    private ImageView rightImageView;

    private boolean needDivider = false;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private static Paint paint;

    public KeyAndImgCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);

        leftTextView = new TextView(context);
        leftTextView.setBackgroundColor(Color.TRANSPARENT);
        leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        leftTextView.setSingleLine(true);
        leftTextView.setGravity(Gravity.CENTER_VERTICAL);
        leftTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        infoViewParams.weight = 1;
        leftTextView.setLayoutParams(infoViewParams);
        addView(leftTextView);

        rightImageView = new ImageView(context);
        rightImageView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        rightImageView.setImageResource(R.drawable.y);
        LayoutParams imgParams = new LayoutParams(AndroidUtilities.dp(24), AndroidUtilities.dp(24));
        imgParams.gravity = Gravity.CENTER_VERTICAL;
        addView(rightImageView, imgParams);
    }

    public void setInfo(String leftViewText, boolean hideRightImg, boolean needDivider) {
        if (hideRightImg) {
            rightImageView.setVisibility(GONE);
        }
        this.needDivider = needDivider;
        leftTextView.setText(leftViewText);
        setWillNotDraw(!needDivider);
    }

    public void setCellBackgroudColor(int backgroudColor) {
        setBackgroundColor(backgroudColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(dividerLeftPadding, getHeight() - 1, getWidth() - dividerRightPadding, getHeight() - 1, paint);
        }
    }

    public void setDivider(boolean divider, int leftPadding, int rightPadding) {
        needDivider = divider;
        dividerLeftPadding = leftPadding;
        dividerRightPadding = rightPadding;
        setWillNotDraw(!divider);
    }
}
