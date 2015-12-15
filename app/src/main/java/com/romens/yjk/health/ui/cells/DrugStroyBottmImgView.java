package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.ui.adapter.BottomImgViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/20.
 */
public class DrugStroyBottmImgView extends LinearLayout {

    private ViewPager viewPager;
    private BottomImgViewAdapter adapter;

    public DrugStroyBottmImgView(Context context) {
        super(context);
        viewPager = new ViewPager(context);
        addView(viewPager, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        adapter = new BottomImgViewAdapter();
        viewPager.setAdapter(adapter);
    }

    public void setData(List<View> viewList) {
        adapter.setData(viewList);
    }
}

