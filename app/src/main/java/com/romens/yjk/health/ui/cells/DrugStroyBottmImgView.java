package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.romens.android.ui.Components.LayoutHelper;

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

class BottomImgViewAdapter extends PagerAdapter {

    private List<View> list;

    public BottomImgViewAdapter() {
        list = new ArrayList<>();
    }

    public void setData(List<View> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list.get(position));
        return list.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));
    }
}