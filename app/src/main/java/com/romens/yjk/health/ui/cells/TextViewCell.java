package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2016/1/12.
 */
public class TextViewCell extends FrameLayout {

    private TextView textView;
    private ImageView imageView;

    private boolean needDivider = false;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private static Paint paint;

    public TextViewCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(getResources().getColor(R.color.title_background_grey));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
        }
        imageView = new ImageView(context);
        imageView.setBackgroundResource(R.color.theme_primary);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(4), LayoutHelper.MATCH_PARENT);
        imgParams.gravity = Gravity.LEFT;
        imageView.setLayoutParams(imgParams);
        imageView.setVisibility(GONE);
        addView(imageView);

        textView = new TextView(context);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xff757575);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setIncludeFontPadding(false);
        FrameLayout.LayoutParams params = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        addView(textView, params);
    }

    public void setText(String text, boolean needDivider) {
        this.needDivider = needDivider;
        textView.setText(text);
        setWillNotDraw(!needDivider);
    }

    public void hiddenImageView() {
        imageView.setVisibility(GONE);
    }

    public void showImageView() {
        imageView.setVisibility(VISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(dividerLeftPadding, getHeight() - 4, getWidth() - dividerRightPadding, getHeight() - 4, paint);
        }
    }

    public void setDivider(boolean divider, int leftPadding, int rightPadding) {
        needDivider = divider;
        dividerLeftPadding = leftPadding;
        dividerRightPadding = rightPadding;
        setWillNotDraw(!divider);
    }
}
