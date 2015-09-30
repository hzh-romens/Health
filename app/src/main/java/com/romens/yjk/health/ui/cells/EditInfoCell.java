package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/9/19.
 */
public class EditInfoCell extends LinearLayout {

    private TextView liftInfoView;
    private EditText editInfoView;
    private ImageView rightImgView;

    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private boolean needDivider = false;

    private static Paint paint;

    public EditInfoCell(Context context) {
        super(context);
        setOrientation(HORIZONTAL);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        liftInfoView = new TextView(context);
        liftInfoView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        liftInfoView.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams liftViewParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        liftViewParams.setMargins(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        addView(liftInfoView, liftViewParams);

        editInfoView = new EditText(context);
        editInfoView.setBackgroundColor(Color.TRANSPARENT);
        editInfoView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        editInfoView.setSingleLine(true);
        editInfoView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams editViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        editViewParams.weight = 1;
        editViewParams.gravity=Gravity.CENTER;
        addView(editInfoView, editViewParams);

        rightImgView = new ImageView(context);
        rightImgView.setVisibility(GONE);
        addView(rightImgView, LayoutHelper.createLinear(20, 20, Gravity.CENTER_VERTICAL, 8, 8, 8, 8));
    }

    public void setViewText(String liftInfo, boolean needDivider) {
        this.needDivider = needDivider;
        liftInfoView.setText(liftInfo);
    }

    public void setRightImgView(int rightImgResource) {
        rightImgView.setVisibility(VISIBLE);
        rightImgView.setImageResource(rightImgResource);
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

    public String getEditTextValue(){
        return editInfoView.getText().toString();
    }

}
