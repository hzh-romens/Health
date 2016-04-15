package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.romens.android.core.ImageReceiver;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.images.ui.CloudImageView;

/**
 * Created by siery on 15/8/17.
 */
public class SalesPromotionADCell extends FrameLayout {
    private CloudImageView adImageView;

    public SalesPromotionADCell(Context context) {
        super(context);
        adImageView = CloudImageView.create(context);
        addView(adImageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.CENTER));
    }

    public void setImage(String httpUrl, Drawable thumb) {
        adImageView.setPlaceholderImage(thumb);
        adImageView.setImagePath(httpUrl);
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
                measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(measureHeight - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
            }
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

}
