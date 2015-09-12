package com.romens.yjk.health.ui.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.romens.android.ui.widget.button.CircleImageView;
import com.romens.android.ui.widget.button.FabUtil;


public class VoiceProgressRingView extends View implements FabUtil.OnFabValueCallback {

    private Paint progressPaint;
    private int size = 0;
    private int viewRadius;
    private int startRadius = 0;
    private float progress, maxProgress;
    private int progressColor = Color.BLACK;


    // Animation related stuff
    private float actualProgress;
    private ValueAnimator progressAnimator;

    private CircleImageView.OnFabViewListener fabViewListener;

    public VoiceProgressRingView(Context context) {
        super(context);
        init();
    }

    public VoiceProgressRingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoiceProgressRingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        progress = 0f;
        maxProgress = 100f;
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        size = Math.max(w, h);
        viewRadius = size / 2;
    }

    public void setFabViewListener(CircleImageView.OnFabViewListener fabViewListener) {
        this.fabViewListener = fabViewListener;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the arc
        int x = canvas.getWidth() / 2;
        int y = canvas.getHeight() / 2;
        float radius = (viewRadius - startRadius) * progress / maxProgress + startRadius;
        canvas.drawCircle(x, y, radius, progressPaint);
    }


    /**
     * Sets the progress of the progress bar.
     *
     * @param currentProgress the current progress you want to set
     */
    public void setProgress(final float currentProgress) {
        this.progress = currentProgress;
        // Reset the determinate animation to approach the new progress
        if (progressAnimator != null && progressAnimator.isRunning()) {
            progressAnimator.cancel();
        }
        progressAnimator = FabUtil.createProgressAnimator(this, actualProgress, currentProgress, this);
        progressAnimator.start();
        invalidate();

    }

    public void setStartRadius(int radius) {
        startRadius = radius;
    }


    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }


    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        progressPaint.setColor(progressColor);
    }


    /**
     * Starts the progress bar animation.
     * (This is an alias of resetAnimation() so it does the same thing.)
     */
    public void startAnimation() {
        resetAnimation();
    }


    public void stopAnimation(boolean hideProgress) {
        if (progressAnimator != null && progressAnimator.isRunning()) {
            progressAnimator.cancel();
        }
        if (hideProgress) {
            progress = 0;
        } else {
            progress = 0;
        }
        invalidate();
    }

    /**
     * Resets the animation.
     */
    public void resetAnimation() {
        stopAnimation(false);
        actualProgress = 0f;
        progressAnimator = FabUtil.createProgressAnimator(this, actualProgress, progress, this);
        progressAnimator.start();
    }

    @Override
    public void onIndeterminateValuesChanged(float indeterminateSweep, float indeterminateRotateOffset, float startAngle, float progress) {
        if (progress != -1) {
            this.actualProgress = progress;
            if (actualProgress >= 100f && fabViewListener != null) {
                fabViewListener.onProgressCompleted();
            }
        }
    }
}
