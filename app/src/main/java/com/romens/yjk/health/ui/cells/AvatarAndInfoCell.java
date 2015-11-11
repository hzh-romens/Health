package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by anlc on 2015/11/9.
 */
public class AvatarAndInfoCell extends LinearLayout {

    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private TextView titleTextView;
    private TextView subTitleTextView;

    private ImageView rightImage;

    private static Paint paint;
    private boolean needDivider = false;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;

    public AvatarAndInfoCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        avatarImage = new BackupImageView(context);
        avatarImage.setRoundRadius(AndroidUtilities.dp(15));
        addView(avatarImage, LayoutHelper.createLinear(40, 40, Gravity.CENTER_VERTICAL, 8, 8, 8, 8));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(VERTICAL);
        LayoutParams layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        layoutParams.weight = 1;
        linearLayout.setLayoutParams(layoutParams);
        addView(linearLayout);

        titleTextView = new TextView(context);
        titleTextView.setSingleLine(true);
        titleTextView.setTextColor(getResources().getColor(R.color.theme_primary));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        LayoutParams titleParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        titleTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(0));
        titleTextView.setLayoutParams(titleParams);
        linearLayout.addView(titleTextView);

        subTitleTextView = new TextView(context);
        subTitleTextView.setSingleLine(true);
        subTitleTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        subTitleTextView.setTextColor(getResources().getColor(R.color.theme_sub_title));
        subTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        LayoutParams subTitleParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        subTitleTextView.setLayoutParams(subTitleParams);
        linearLayout.addView(subTitleTextView);

        rightImage = new ImageView(context);
        LayoutParams imgParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        imgParams.gravity = Gravity.CENTER;
        rightImage.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        rightImage.setLayoutParams(imgParams);
        rightImage.setVisibility(GONE);
        addView(rightImage);
    }

    public void setTitleAndSubTitle(String titleStr, String subTitleStr, boolean needDivider) {
        this.needDivider = needDivider;
        titleTextView.setText(titleStr);
        subTitleTextView.setText(subTitleStr);
        if (avatarDrawable == null) {
            avatarDrawable = new AvatarDrawable(true);
        }
        avatarDrawable.setInfo(0, titleStr);
        avatarDrawable.setColor(ResourcesConfig.primaryColor);
        avatarImage.setImageDrawable(avatarDrawable);
        setWillNotDraw(!needDivider);
    }

    public void setRightImgResource(int imgResource) {
        rightImage.setVisibility(VISIBLE);
        rightImage.setImageResource(imgResource);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
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
