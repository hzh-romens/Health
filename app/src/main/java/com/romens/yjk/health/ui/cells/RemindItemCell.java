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
import com.romens.android.ui.Components.Switch;

/**
 * Created by anlc on 2015/10/30.
 */
public class RemindItemCell extends LinearLayout {

    private ImageView leftImageView;
    private TextView titleTextView;
    private Switch aSwitch;

    private boolean needDivider = false;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private static Paint paint;

    private onSwitchClickLinstener onSwitchClickLinstener;

    public void setOnSwitchClickLinstener(RemindItemCell.onSwitchClickLinstener onSwitchClickLinstener) {
        this.onSwitchClickLinstener = onSwitchClickLinstener;
    }

    public RemindItemCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        leftImageView = new ImageView(context);
        leftImageView.setScaleType(ImageView.ScaleType.CENTER);
        leftImageView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams imgParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        imgParams.gravity = Gravity.CENTER_VERTICAL;
        leftImageView.setLayoutParams(imgParams);
        addView(leftImageView);

        titleTextView = new TextView(context);
        titleTextView.setBackgroundColor(Color.TRANSPARENT);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        titleTextView.setTextColor(0xff666666);
        titleTextView.setSingleLine(true);
        titleTextView.setGravity(Gravity.CENTER_VERTICAL);
        titleTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        infoViewParams.weight = 1;
        infoViewParams.gravity = Gravity.CENTER_VERTICAL;
        addView(titleTextView, infoViewParams);

        aSwitch = new Switch(context);
        aSwitch.setDuplicateParentStateEnabled(false);
        aSwitch.setFocusable(false);
        aSwitch.setFocusableInTouchMode(false);
        aSwitch.setClickable(false);
        LayoutParams layoutParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        aSwitch.setLayoutParams(layoutParams);
        addView(aSwitch);

        aSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSwitchClickLinstener != null) {
                    onSwitchClickLinstener.onSwitchClick();
                }
            }
        });
    }

    public void setData(int leftImageResource, String titleText, boolean needDivider) {
        this.needDivider = needDivider;
        leftImageView.setImageResource(leftImageResource);
        titleTextView.setText(titleText);
        setWillNotDraw(!needDivider);
    }

    public void setCheck(boolean flag) {
        aSwitch.setChecked(flag);
    }

    public interface onSwitchClickLinstener {
        void onSwitchClick();
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
