package com.romens.yjk.health.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.adapter.FragmentViewPagerAdapter;
import com.romens.android.ui.widget.SlidingFixTabLayout;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.fragment.CompleteFragment;
import com.romens.yjk.health.ui.fragment.WaittingEvaluateFragment;
import com.romens.yjk.health.ui.fragment.WaittingHandleFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AUSU on 2015/9/9.
 * 订单管理页面
 */
public class MyOrderActivity extends BaseActivity{
    private SlidingFixTabLayout slidingFixTabLayout;
    private ViewPager viewPager;
    private OrderAdapter orderAdapter;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order, R.id.action_bar);
        initView();
    }

    private void initView() {
        slidingFixTabLayout= (SlidingFixTabLayout) findViewById(R.id.sliding_tabs);
        viewPager= (ViewPager) findViewById(R.id.viewpager);
        slidingFixTabLayout.setBackgroundResource(R.color.theme_primary);
        slidingFixTabLayout.setCustomTabView(R.layout.widget_tab_indicator, android.R.id.text1);
        slidingFixTabLayout.setTabStripBottomBorderThicknessPadding(AndroidUtilities.dp(2));
        slidingFixTabLayout.setSelectedIndicatorColors(Color.WHITE);
        slidingFixTabLayout.setDistributeEvenly(true);
        orderAdapter=new OrderAdapter(getSupportFragmentManager(),initFragment(),initPagerTitle());
        viewPager.setAdapter(orderAdapter);
        slidingFixTabLayout.setViewPager(viewPager);

        actionBar = getMyActionBar();
        actionBar.setTitle("我的订单");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 0) {
                }
            }
        });
    }

    private List<String> initPagerTitle() {
        List<String> titles = new ArrayList<>();
        titles.add("已完成");
        titles.add("待处理");
        titles.add("待评价");
        return titles;
    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CompleteFragment());
        fragments.add(new WaittingHandleFragment());
        fragments.add(new WaittingEvaluateFragment());
        return fragments;
    }

     class OrderAdapter extends FragmentViewPagerAdapter {
      private List<String> mPageTitles=new ArrayList<String>();

        public OrderAdapter(FragmentManager fragmentManager, List<Fragment> list,List<String> pageTitles) {
            super(fragmentManager, list);
            mPageTitles.clear();
            mPageTitles.addAll(pageTitles);
        }
        public String getPageTitle(int position){
            return mPageTitles.get(position);
        }
    }
}
