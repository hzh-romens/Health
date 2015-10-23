package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/10/16.
 */
public class TitleAndBodyCell extends LinearLayout {

    private TextView titleTextView;
    private TextView bodyTextView;

    private boolean needDivider = false;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private static Paint paint;

    public TitleAndBodyCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(VERTICAL);
        titleTextView = new TextView(context);
        titleTextView.setBackgroundColor(Color.WHITE);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        titleTextView.setGravity(Gravity.CENTER_VERTICAL);
        titleTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LinearLayout.LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        titleTextView.setLayoutParams(infoViewParams);
        addView(titleTextView);

        bodyTextView = new TextView(context);
        bodyTextView.setBackgroundColor(Color.WHITE);
        bodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        bodyTextView.setGravity(Gravity.CENTER_VERTICAL);
        bodyTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LinearLayout.LayoutParams answerViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        bodyTextView.setLayoutParams(answerViewParams);
        addView(bodyTextView);
    }

    public void setTitleAndBodyInfo(String titleText, String bodyText, boolean needDivider) {
        this.needDivider = needDivider;
        titleTextView.setText(titleText);
        bodyTextView.setText(bodyText);
        setWillNotDraw(!needDivider);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
//    }

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
