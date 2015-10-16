package com.romens.yjk.health.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.FragmentViewPagerAdapter;
import com.romens.android.ui.widget.SlidingFixTabLayout;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.MenuBarCell;
import com.romens.yjk.health.ui.fragment.OrderAllFragment;
import com.romens.yjk.health.ui.fragment.OrderAlreadyCompleteFragment;
import com.romens.yjk.health.ui.fragment.OrderAlreadyEvaluateFragment;
import com.romens.yjk.health.ui.fragment.OrderBeingFragment;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/14.
 */
public class CollectActivity extends BaseActivity {

    private SlidingFixTabLayout tabLayout;
    private ViewPager viewPager;
    private CollectPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);
        actionBarEvent(actionBar);

//        tabLayout = new SlidingFixTabLayout(this);
//        tabLayout.setBackgroundResource(R.color.theme_primary);
//        container.addView(tabLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
//
//        FrameLayout frameLayout = new FrameLayout(this);
//        viewPager = new ViewPager(this);
//        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
//        container.addView(frameLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
//
//        pagerAdapter = new CollectPagerAdapter(getSupportFragmentManager(), initPagerTitle(), initFragment());
//        viewPager.setAdapter(pagerAdapter);
//
//        tabLayout.setCustomTabView(R.layout.widget_tab_indicator, android.R.id.text1);
//        tabLayout.setTabStripBottomBorderThicknessPadding(AndroidUtilities.dp(2));
//        tabLayout.setSelectedIndicatorColors(Color.WHITE);
//        tabLayout.setDistributeEvenly(true);
//        tabLayout.setViewPager(viewPager);
    }

    private void actionBarEvent(ActionBar actionBar) {
        actionBar.setTitle("我的收藏");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                super.onItemClick(i);
                if (i == -1) {
                    finish();
                }
            }
        });
    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new OrderAllFragment());
        fragments.add(new OrderBeingFragment());
        return fragments;
    }

    private List<String> initPagerTitle() {
        List<String> titles = new ArrayList<>();
        titles.add("药品");
        titles.add("药店");
        return titles;
    }

    class CollectPagerAdapter extends FragmentViewPagerAdapter {
        private final List<String> mPageTitle = new ArrayList<>();

        public CollectPagerAdapter(FragmentManager fragmentManager, List<String> pageTitles, List<Fragment> fragments) {
            super(fragmentManager, fragments);
            mPageTitle.clear();
            mPageTitle.addAll(pageTitles);
        }

        @Override
        public String getPageTitle(int position) {
            return mPageTitle.get(position);
        }
    }
}
