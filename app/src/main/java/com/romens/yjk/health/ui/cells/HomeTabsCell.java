package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/12/21.
 */
public class HomeTabsCell extends LinearLayout implements ViewPager.OnPageChangeListener {
    private static Paint paint;
    private ViewPager viewPager;

    public HomeTabsCell(Context context) {
        super(context);
        init(context);
    }

    public HomeTabsCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeTabsCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(2);
        }

        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64), View.MeasureSpec.EXACTLY));
    }


    public void addView(int iconResId, CharSequence name) {
        addView(iconResId, 0, name);
    }

    public void addView(int iconResId, int selectedIconResId, CharSequence name) {
        HomeTabCell cell = new HomeTabCell(getContext());
        cell.setClickable(true);
        cell.setBackgroundResource(R.drawable.list_selector);
        cell.setTag(getChildCount());
        addView(cell, new LinearLayout.LayoutParams(0, LayoutHelper.WRAP_CONTENT, 1));
        cell.setValue(iconResId, selectedIconResId, name);
        cell.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager != null) {
                    int index = Integer.parseInt(v.getTag().toString());
                    viewPager.setCurrentItem(index);
                    setSelected(index);
                }
            }
        });
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(this);
        setWillNotDraw(false);
    }

    public void setSelected(int index) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            HomeTabCell cell = (HomeTabCell) getChildAt(i);
            if (cell != null) {
                cell.select(i == index);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(0, 1, getWidth(), 1, paint);
    }
}
