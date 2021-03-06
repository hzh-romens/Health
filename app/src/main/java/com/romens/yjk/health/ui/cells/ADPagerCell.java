package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.log.FileLog;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.view.GuideViewPager;
import com.romens.android.ui.view.PagerIndicator;
import com.romens.yjk.health.config.CommonConfig;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by siery on 15/8/13.
 */
public class ADPagerCell extends FrameLayout {
    private GuideViewPager viewPager;
    private PagerIndicator pagerIndicator;

    private Timer timeTimer;
    private final Object timerSync = new Object();
    private int currPagerIndex = 0;

    private static Paint paint;

    public ADPagerCell(Context context) {
        super(context);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        setBackgroundColor(Color.WHITE);
        viewPager = new GuideViewPager(context);
        addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        pagerIndicator = new PagerIndicator(context);
        addView(pagerIndicator, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0, 0, AndroidUtilities.dp(8)));
        viewPager.setPagerIndicator(pagerIndicator);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currPagerIndex = position;
                viewPager.onPagerIndicatorChange(currPagerIndex);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    public void onDetachedFromWindow() {
        destroyTimer();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = (int) (width * CommonConfig.pagerScale);
        if (viewPager.getVisibility() == VISIBLE) {
            viewPager.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
        setMeasuredDimension(width, height);
    }

    public void start() {
        createTimer();
    }

    private void createTimer() {
        if (viewPager == null || viewPager.getAdapter() == null || viewPager.getAdapter().getCount() <= 0) {
            return;
        }
        if (timeTimer != null) {
            return;
        }
        timeTimer = new Timer();
        timeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int count = viewPager.getAdapter().getCount();
                if (currPagerIndex == (count - 1)) {
                    currPagerIndex = 0;
                } else {
                    currPagerIndex++;
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(currPagerIndex, true);
                    }
                });

            }
        }, 3000, 3000);
    }

    private void destroyTimer() {
        try {
            synchronized (timerSync) {
                if (timeTimer != null) {
                    timeTimer.cancel();
                    timeTimer = null;
                }
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    public void setAdapter(PagerAdapter adapter) {
        viewPager.setAdapter(adapter);
        createTimer();
    }
}
