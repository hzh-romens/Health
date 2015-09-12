package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.widget.button.CircleImageView;


/**
 * Created by kurt on 21 02 2015 .
 */
public class VoiceFabButton extends FrameLayout implements CircleImageView.OnFabViewListener {

    private VoiceCircleImageView circle;
    private VoiceProgressRingView ring;
    private float ringWidthRatio = 0.14f; //of a possible 1f;
    private boolean mIsProgress = false;

    public VoiceFabButton(Context context) {
        super(context);
        init(context);
    }

    public VoiceFabButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceFabButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    protected void init(Context context) {

        ring = new VoiceProgressRingView(context);
        addView(ring, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        int circleSize = 80;
        circle = new VoiceCircleImageView(context);
        addView(circle, LayoutHelper.createFrame(circleSize, circleSize, Gravity.CENTER));

        ring.setStartRadius(AndroidUtilities.dp(circleSize / 2));
        circle.setFabViewListener(this);
        ring.setFabViewListener(this);
        int color = Color.BLACK;
        int progressColor = Color.BLACK;
        int icon = -1;
        float maxProgress = 0;
        float progress = 0;

        circle.setColor(color);
        ring.setProgressColor(progressColor);
        ring.setProgress(progress);
        ring.setMaxProgress(maxProgress);
        circle.setRingWidthRatio(ringWidthRatio);
        if (icon != -1) {
            circle.setIcon(icon);
        }
    }

    public void setMaxProgress(int max) {
        ring.setMaxProgress(max);
    }

    public void setCircleColor(int color) {
        circle.setColor(color);
    }

    public void setProgressColor(int progressColor) {
        ring.setProgressColor(progressColor);
    }

    public void setRingWidth(float value) {
        ringWidthRatio = value;
        circle.setRingWidthRatio(ringWidthRatio);
    }

    public void setIcon(int resource) {
        circle.setIcon(resource);
    }

    public void setOnClickListener(OnClickListener listener) {
        ring.setOnClickListener(listener);
        circle.setOnClickListener(listener);
    }

    @Override
    public void setOnTouchListener(OnTouchListener listener) {
        circle.setOnTouchListener(listener);
    }

    /**
     * shows the animation ring
     *
     * @param show shows animation ring when set to true
     */
    public void showProgress(boolean show) {
        if (show) {
            circle.showRing(true);
        } else {
            circle.showRing(false);
        }
    }

    /**
     * sets current progress
     *
     * @param progress the current progress to set value too
     */
    public void setProgress(float progress) {
        ring.setProgress(progress);
    }

    @Override
    public void onProgressVisibilityChanged(boolean visible) {
        mIsProgress = visible;
        if (mIsProgress) {
            ring.setVisibility(View.VISIBLE);
            ring.startAnimation();
        } else {
            ring.stopAnimation(true);
            ring.setVisibility(View.GONE);
        }
    }

    public boolean isProgress() {
        return mIsProgress;
    }

    @Override
    public void onProgressCompleted() {
        circle.showCompleted(true);
    }
}
