package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by siery on 15/8/13.
 */
public class ChildViewPager extends ViewPager {
    private GestureDetector mGestureDetector;
    private boolean isLast=false;
    public ChildViewPager(Context context) {
        super(context);
        init(context);
    }

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isLast=position==(getAdapter().getCount()-1);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    private void init(Context context) {
        mGestureDetector = new GestureDetector(context, new ChildScrollDetector());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev)&&isLast&& mGestureDetector.onTouchEvent(ev);
    }

    // Return false if we're scrolling in the x direction
    class ChildScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceY) > Math.abs(distanceX);
        }
    }
}
