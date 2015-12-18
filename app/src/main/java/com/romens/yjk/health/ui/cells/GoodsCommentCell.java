package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/12/16.
 */
public class GoodsCommentCell extends LinearLayout {

    private FlexibleRatingBar ratingBar;
    private TextView dateView;

    private TextView contentView;
    private TextView userView;

    private static Paint paint;
    private boolean needDivider;

    public GoodsCommentCell(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        setOrientation(VERTICAL);

        FrameLayout topContainer = new FrameLayout(context);
        addView(topContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT,48));

        userView = new TextView(context);
        userView.setTextColor(ResourcesConfig.bodyText1);
        userView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        userView.setLines(1);
        userView.setMaxLines(3);
        userView.setSingleLine(true);
        userView.setEllipsize(TextUtils.TruncateAt.END);
        userView.setGravity(Gravity.LEFT);
        topContainer.addView(userView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 16, 4, 160, 0));

        dateView = new TextView(context);
        dateView.setTextColor(ResourcesConfig.bodyText3);
        dateView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        dateView.setLines(1);
        dateView.setMaxLines(1);
        dateView.setSingleLine(true);
        dateView.setGravity(Gravity.LEFT);
        topContainer.addView(dateView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.BOTTOM, 16, 0, 16, 4));

        ratingBar = new FlexibleRatingBar(context);
        ratingBar.setEnabled(false);
        ratingBar.setStepSize(0.5f);
        ratingBar.setColorFillOff(0xffffffff);
        ratingBar.setColorFillOn(0xfff9a825);
        ratingBar.setColorFillPressedOff(0xffffffff);
        ratingBar.setColorFillPressedOn(0xfff9a825);
        ratingBar.setColorOutlineOff(0xfff57f17);
        ratingBar.setColorOutlineOn(0xfff57f17);
        ratingBar.setColorOutlinePressed(0xfff57f17);
        ratingBar.setStrokeWidth(AndroidUtilities.dp(2));
        topContainer.addView(ratingBar, LayoutHelper.createFrame(112, 24, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 16, 0, 16, 0));

        contentView = new TextView(context);
        contentView.setTextColor(ResourcesConfig.bodyText1);
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        contentView.setLines(1);
        contentView.setMaxLines(3);
        contentView.setSingleLine(true);
        contentView.setEllipsize(TextUtils.TruncateAt.END);
        contentView.setGravity(Gravity.LEFT);
        addView(contentView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 8, 16, 16));
    }

    public void setValue(int rate, int allCount, String dateTime, String content, String user, boolean divider) {
        ratingBar.setDesiredCount(allCount);
        ratingBar.setRating(rate);
        ratingBar.setDesiredCount(rate);
        dateView.setText(dateTime);
        contentView.setText(content);
        userView.setText(user);
        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(16), getHeight() - 1, getWidth() - AndroidUtilities.dp(16), getHeight() - 1, paint);
        }
    }
}
