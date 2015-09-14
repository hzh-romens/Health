package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/9/13.
 */
public class DrugGroupCell extends FrameLayout {
    private static Paint paint;

    private BackupImageView iconView;
    private TextView titleView;
    private TextView subTitleView;
    private ImageView stateView;
    private AvatarDrawable iconDrawable;

    private boolean needDivider = false;

    public DrugGroupCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        iconView = new BackupImageView(context);
        addView(iconView, LayoutHelper.createFrame(36, 36, Gravity.LEFT | Gravity.CENTER_VERTICAL, 8, 8, 8, 8));

        titleView = new TextView(context);
        titleView.setTextColor(0xde000000);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(titleView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP, 56, 8, 64, 0));
        subTitleView = new TextView(context);
        subTitleView.setTextColor(0x8a000000);
        subTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        subTitleView.setSingleLine(true);
        subTitleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(subTitleView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP, 56, 32, 64, 0));

        stateView = new ImageView(context);
        stateView.setScaleType(ImageView.ScaleType.CENTER);
        addView(stateView, LayoutHelper.createFrame(40, 40, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 8, 8, 8, 8));
        setBackgroundColor(0xfff9f9f9);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void setValue(String title, String subTitle, boolean isExpanded, boolean divider) {
        needDivider = divider;
        
        if (iconDrawable == null) {
            iconDrawable = new AvatarDrawable(true);
        }
        iconDrawable.setInfo(0, title);
        iconDrawable.setColor(isExpanded ? 0xff0f9d58 : 0xff999999);
        iconView.setImageDrawable(iconDrawable);

        titleView.setTextColor(isExpanded ? 0xff0f9d58 : 0xde000000);
        titleView.setText(title);
        subTitleView.setText(subTitle);

        stateView.setImageResource(isExpanded ? R.mipmap.ic_arrow_drop_up_grey600_24dp : R.mipmap.ic_arrow_drop_down_grey600_24dp);

        setWillNotDraw(!divider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(56), getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }
}
