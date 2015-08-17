package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.FrameLayoutFixed;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;

/**
 * Created by siery on 15/8/13.
 */
public class ADOnlyImageCell extends FrameLayout {
    private BackupImageView adImageView;


    public ADOnlyImageCell(Context context) {
        super(context);
        //setPadding(AndroidUtilities.dp(8),AndroidUtilities.dp(4),AndroidUtilities.dp(8),AndroidUtilities.dp(4));
        adImageView = new BackupImageView(context);
        //adImageView.setRoundRadius(AndroidUtilities.dp(4));
        addView(adImageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.CENTER));
    }

    public void setImage(String httpUrl, String filter, Drawable thumb) {
        adImageView.setImage(httpUrl, filter, thumb);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = measureWidth / 2;
        int count = getChildCount();

        for (int index = 0; index < count; index++) {
            final View child = getChildAt(index);
            if (child.getVisibility() != GONE) {
                measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth-getPaddingLeft()-getPaddingRight(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(measureHeight-getPaddingTop()-getPaddingBottom(), MeasureSpec.EXACTLY));
            }
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }
}
