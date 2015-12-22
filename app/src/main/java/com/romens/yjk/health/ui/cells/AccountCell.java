package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/12/21.
 */
public class AccountCell extends FrameLayout {

    private boolean needDivider = false;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private static Paint paint;

    private TextView keyTxtView;
    private TextView valueTxtView;
    private ImageView rightImgView;
//    private EditText editValueView;

    public AccountCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        keyTxtView = new TextView(context);
        keyTxtView.setSingleLine(true);
        keyTxtView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        keyTxtView.setTextColor(getResources().getColor(R.color.theme_title));
        keyTxtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        keyTxtView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        keyTxtView.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT));
        addView(keyTxtView);

        rightImgView = new ImageView(context);
        rightImgView.setScaleType(ImageView.ScaleType.CENTER);
        rightImgView.setPadding(AndroidUtilities.dp(0), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        LayoutParams params = LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(40));
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        rightImgView.setLayoutParams(params);
        addView(rightImgView);

        valueTxtView = new TextView(context);
        valueTxtView.setSingleLine(true);
        valueTxtView.setRight(AndroidUtilities.dp(48));
        valueTxtView.setGravity(Gravity.CENTER_VERTICAL);
        valueTxtView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(48), AndroidUtilities.dp(4));
        valueTxtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        valueTxtView.setTextColor(getResources().getColor(R.color.theme_sub_title));
        LayoutParams valueParams = LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT);
        valueParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        valueTxtView.setLayoutParams(valueParams);
        addView(valueTxtView);

//        editValueView = new EditText(context);

//        hideView("");
    }

    private void hideView(String key) {
        keyTxtView.setText(key);
        valueTxtView.setVisibility(GONE);
//        editValueView.setVisibility(GONE);
        rightImgView.setVisibility(GONE);
        initKeyTxtColor();
    }

    private void showView(String key) {
        keyTxtView.setText(key);
        valueTxtView.setVisibility(VISIBLE);
//        editValueView.setVisibility(VISIBLE);
        rightImgView.setVisibility(VISIBLE);
        initKeyTxtColor();
    }

    public void setKeyAndValue(String key, String value, int imgResource, boolean needDivider) {
        this.needDivider = needDivider;
        showView(key);
        if (value == null) {
            value = "";
        }
        valueTxtView.setText(value);
        rightImgView.setImageResource(imgResource);
        setWillNotDraw(!needDivider);
    }

    public void setKeyAndValue(String key, String value, boolean needDivider) {
        this.needDivider = needDivider;
        hideView(key);
        valueTxtView.setVisibility(VISIBLE);
        valueTxtView.setText(value);
        setWillNotDraw(!needDivider);
    }

    public void setKey(String key, boolean needDivider) {
        hideView(key);
        initKeyTxtColor();
        this.needDivider = needDivider;
        setWillNotDraw(!needDivider);
    }

    public void setKeyTxtColor(int color) {
        keyTxtView.setTextColor(getResources().getColor(color));
    }

    public void initKeyTxtColor() {
        keyTxtView.setTextColor(getResources().getColor(R.color.theme_title));
    }

    public void setKeyAndValue(String key, int imgResource, boolean needDivider) {
        this.needDivider = needDivider;
        hideView(key);
        rightImgView.setImageResource(imgResource);
        rightImgView.setVisibility(VISIBLE);
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

}
