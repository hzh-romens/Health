package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/10/14.
 */
public class IsSelectCell extends LinearLayout {

    private ImageView isSelectImgView;
    private TextView infoTextView;
    private boolean needDivider = false;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private static Paint paint;
    private boolean flag;
    private TextView rightView;
    private boolean needTopDivier;

    private SelectClick click;
    private onViewClick viewClick;

    public void setViewClick(onViewClick viewClick) {
        this.viewClick = viewClick;
    }

    public void setClick(SelectClick click) {
        this.click = click;
    }

    public interface onViewClick {
        void onImageViewClick();

        void rightBtnClick();
    }

    public interface SelectClick {
        void onClick();
    }

    public IsSelectCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);

        isSelectImgView = new ImageView(context);
        isSelectImgView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        isSelectImgView.setImageResource(R.drawable.control_address_undeafult);
        isSelectImgView.setVisibility(GONE);
        flag = false;
        LayoutParams imgParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imgParams.gravity = Gravity.CENTER_VERTICAL;
        addView(isSelectImgView, imgParams);

        isSelectImgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click != null) {
                    click.onClick();
                }
                if (viewClick != null) {
                    viewClick.onImageViewClick();
                }
            }
        });

        infoTextView = new TextView(context);
        infoTextView.setBackgroundColor(Color.TRANSPARENT);
        infoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        infoTextView.setSingleLine(true);
        infoTextView.setGravity(Gravity.CENTER_VERTICAL);
        infoTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        infoViewParams.weight = 1;
        infoTextView.setLayoutParams(infoViewParams);
        addView(infoTextView);

        rightView = new TextView(context);
        rightView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        rightView.setSingleLine(true);
        rightView.setVisibility(GONE);
        rightView.setTextColor(Color.WHITE);
        rightView.setBackgroundResource(R.drawable.btn_primary_default);
        rightView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        rightView.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams rightViewParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        rightViewParams.setMargins(AndroidUtilities.dp(0), AndroidUtilities.dp(0), AndroidUtilities.dp(8), AndroidUtilities.dp(0));
        rightView.setLayoutParams(rightViewParams);
        addView(rightView);

        rightView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewClick != null) {
                    viewClick.rightBtnClick();
                }
            }
        });

    }

    public void setInfo(String infoTxt, boolean needDivider) {
        this.needDivider = needDivider;
        infoTextView.setText(infoTxt);
        setWillNotDraw(!needDivider);
    }

    public void needTopDivider(boolean needTopDivider) {
        this.needTopDivier = needTopDivider;
        setWillNotDraw(!needTopDivider);
    }

    public boolean changeSelect() {
        if (flag) {
            isSelectImgView.setImageResource(R.drawable.control_address_undeafult);
            flag = false;
            return flag;
        }
        flag = true;
        isSelectImgView.setImageResource(R.drawable.control_address_deafult);
        return flag;
    }

    public void setRightBtnTxt(String infoTxt) {
        rightView.setVisibility(VISIBLE);
        rightView.setText(infoTxt);
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
        if (needTopDivier) {
            canvas.drawLine(dividerLeftPadding, 1, getWidth() - dividerRightPadding, 1, paint);
        }
    }

    public void setDivider(boolean divider, int leftPadding, int rightPadding) {
        needDivider = divider;
        dividerLeftPadding = leftPadding;
        dividerRightPadding = rightPadding;
        setWillNotDraw(!divider);
    }
}
