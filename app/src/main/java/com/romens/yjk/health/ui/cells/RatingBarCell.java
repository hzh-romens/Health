package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;

/**
 * @author Zhou Lisi
 * @create 2016-04-06 14:15
 * @description
 */
public class RatingBarCell extends FrameLayout {
    private static Paint paint;
    private boolean needDivider = false;

    private TextView textView;
    private FlexibleRatingBar ratingBar;

    public RatingBarCell(Context context) {
        super(context);
        init(context);
    }

    public RatingBarCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RatingBarCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        textView = new TextView(context);
        textView.setTextColor(0xff212121);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(3);
        textView.setMaxLines(3);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutHelper.createFrame(144, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 0, 0, 0));


        ratingBar = new FlexibleRatingBar(context);
        ratingBar.setColorFillOff(0xffffffff);
        ratingBar.setColorFillOn(0xfff9a825);
        ratingBar.setColorFillPressedOff(0xffffffff);
        ratingBar.setColorFillPressedOn(0xfff9a825);
        ratingBar.setColorOutlineOff(0xfff57f17);
        ratingBar.setColorOutlineOn(0xfff57f17);
        ratingBar.setColorOutlinePressed(0xfff57f17);
        ratingBar.setStrokeWidth(AndroidUtilities.dp(2));
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1f);
        addView(ratingBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 32, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 144, 0, 16, 0));

    }

    public void setValue(String text,float value, boolean divider) {
        textView.setText(text);
        needDivider = divider;
        ratingBar.setRating(value);
        setWillNotDraw(!divider);
    }

    public float getValue(){
        return ratingBar.getRating();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }
}
