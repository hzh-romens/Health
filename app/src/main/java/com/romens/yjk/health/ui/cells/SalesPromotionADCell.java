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

/**
 * Created by siery on 15/8/17.
 */
public class SalesPromotionADCell extends FrameLayout {
    private BackupImageView adImageView;
    private ImageLoadCallback imageLoadCallback;


    public SalesPromotionADCell(Context context) {
        super(context);
        adImageView = new BackupImageView(context);
        adImageView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            @Override
            public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
                if (imageLoadCallback != null && set) {
                    imageLoadCallback.onFinished(imageReceiver.getBitmap());
                }
            }
        });
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
                measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(measureHeight - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
            }
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    public void setImageLoadCallback(ImageLoadCallback callback) {
        this.imageLoadCallback = callback;
    }

    public interface ImageLoadCallback {
        void onFinished(Bitmap bitmap);
    }


}
