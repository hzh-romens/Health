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
 * Created by anlc on 2015/10/16.
 */
public class ImgAndValueCell extends LinearLayout {

    private TextView leftTextView;
    private ImageView rightImageView;

    private boolean needDivider = false;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private static Paint paint;

    public ImgAndValueCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);

        rightImageView = new ImageView(context);
        rightImageView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        rightImageView.setImageResource(R.drawable.y);
        LayoutParams imgParams = new LayoutParams(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT);
        imgParams.gravity = Gravity.CENTER_VERTICAL;
        rightImageView.setLayoutParams(imgParams);
        addView(rightImageView);

        leftTextView = new TextView(context);
        leftTextView.setBackgroundColor(Color.TRANSPARENT);
        leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        leftTextView.setSingleLine(true);
        leftTextView.setGravity(Gravity.CENTER_VERTICAL);
        leftTextView.setPadding(AndroidUtilities.dp(0), AndroidUtilities.dp(4), AndroidUtilities.dp(0), AndroidUtilities.dp(4));
        LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        infoViewParams.weight = 1;
        leftTextView.setLayoutParams(infoViewParams);
        addView(leftTextView);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickLinstener != null) {
                    onViewClickLinstener.click();
                }
            }
        });
    }

    public void setInfo(String leftViewText, boolean hideRightImg, boolean needDivider) {
        if (hideRightImg) {
            rightImageView.setVisibility(GONE);
        }
        this.needDivider = needDivider;
        leftTextView.setText(leftViewText);
        setWillNotDraw(!needDivider);
    }

    public void setLeftImgSize(int width, int height) {
        LayoutParams layoutParams = (LayoutParams) rightImageView.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.width = AndroidUtilities.dp(width);
        layoutParams.height = AndroidUtilities.dp(height);
        rightImageView.setLayoutParams(layoutParams);
    }

    public void setLeftImgPadding(int leftAndRight, int topAndBottom) {
        rightImageView.setPadding(AndroidUtilities.dp(leftAndRight), AndroidUtilities.dp(topAndBottom), AndroidUtilities.dp(0), AndroidUtilities.dp(topAndBottom));
    }

    public void setData(int imgResource, String value, boolean needDivider) {
        this.needDivider = needDivider;
        rightImageView.setImageResource(imgResource);
        leftTextView.setText(value);
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

    public void setImgViewSource(int imgViewSource) {
        rightImageView.setImageResource(imgViewSource);
    }

    private OnViewClickLinstener onViewClickLinstener;

    public void setOnViewClickLinstener(OnViewClickLinstener onViewClickLinstener) {
        this.onViewClickLinstener = onViewClickLinstener;
    }

    public interface OnViewClickLinstener {
        void click();
    }
}
