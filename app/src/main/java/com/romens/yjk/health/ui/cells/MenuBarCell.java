package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/10/14.
 */
public class MenuBarCell extends LinearLayout implements View.OnClickListener {

    private TextView firstView;
    private TextView secondView;
    private TextView threeView;
    private LinearLayout fourView;
    private TextView subleftView;

    private boolean needDivider;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private static Paint paint;

    private OnMenuClickListentr onMenuClickListentr;

    public void setOnMenuClickListentr(OnMenuClickListentr onMenuClickListentr) {
        this.onMenuClickListentr = onMenuClickListentr;
    }

    public MenuBarCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        firstView = new TextView(context);
        firstView.setBackgroundColor(Color.TRANSPARENT);
        firstView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        firstView.setSingleLine(true);
        firstView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams firstViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        firstViewParams.weight = 1;
        addView(firstView, firstViewParams);
        firstView.setOnClickListener(this);

        secondView = new TextView(context);
        secondView.setBackgroundColor(Color.TRANSPARENT);
        secondView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        secondView.setSingleLine(true);
        secondView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams secondViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        secondViewParams.weight = 1;
        addView(secondView, secondViewParams);
        secondView.setOnClickListener(this);

        threeView = new TextView(context);
        threeView.setBackgroundColor(Color.TRANSPARENT);
        threeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        threeView.setSingleLine(true);
        threeView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams threeViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        threeViewParams.weight = 1;
        addView(threeView, threeViewParams);
        threeView.setOnClickListener(this);

        fourView = new LinearLayout(context);
        fourView.setOrientation(HORIZONTAL);
        LayoutParams fourViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        fourViewParams.weight = 1;
        addView(fourView, fourViewParams);
        fourView.setOnClickListener(this);

        subleftView = new TextView(context);
        subleftView.setBackgroundColor(Color.TRANSPARENT);
        subleftView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        subleftView.setSingleLine(true);
        subleftView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams subleftViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        subleftViewParams.gravity = Gravity.CENTER | Gravity.LEFT;
        fourView.addView(subleftView, subleftViewParams);

        ImageView subRightImgView = new ImageView(context);
        subRightImgView.setImageResource(R.drawable.check_blue);
        subRightImgView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams imgParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imgParams.gravity = Gravity.CENTER | Gravity.RIGHT;
        fourView.addView(subRightImgView, imgParams);
    }

    public void setMenuInfo(String firstInfo, String secondInfo, String threeInfo, String fourInfo, boolean needDivider) {
        this.needDivider = needDivider;
        firstView.setText(firstInfo);
        secondView.setText(secondInfo);
        threeView.setText(threeInfo);
        subleftView.setText(fourInfo);
        setWillNotDraw(!needDivider);
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

    @Override
    public void onClick(View v) {
        if (onMenuClickListentr != null) {
            if (v == firstView) {
                onMenuClickListentr.firstViewClick();
            } else if (v == secondView) {
                onMenuClickListentr.secondViewClick();
            } else if (v == threeView) {
                onMenuClickListentr.threeViewClick();
            } else if (v == fourView) {
                onMenuClickListentr.fourViewClick();
            }
        }
    }

    public interface OnMenuClickListentr {
        void firstViewClick();

        void secondViewClick();

        void threeViewClick();

        void fourViewClick();
    }
}
