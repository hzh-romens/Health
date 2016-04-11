package com.romens.yjk.health.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.FragmentViewPagerAdapter;
import com.romens.android.ui.widget.SlidingFixTabLayout;
import com.romens.yjk.health.R;
import com.romens.yjk.health.common.OrderStatus;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.fragment.OrderFragment;
import com.romens.yjk.health.ui.fragment.OrderServiceFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/9/18.
 * 我的订单页面
 */
public class MyOrderActivity extends DarkActionBarActivity implements AppNotificationCenter.NotificationCenterDelegate {

    private SlidingFixTabLayout slidingFixTabLayout;
    private ViewPager viewPager;
    private OrderPagerAdapter viewPagerAdapter;

    public final static int ORDER_TYPE_ALL = 1;
    public final static int ORDER_TYPE_BEING = 2;
    public final static int ORDER_TYPE_COMPLETE = 3;
    public final static int ORDER_TYPE_EVALUATE = 4;

    private int fragmentInde = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onOrderStateChange);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onOrderDataUpdated);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = actionBarEvent();
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        slidingFixTabLayout = new SlidingFixTabLayout(this);
        slidingFixTabLayout.setBackgroundResource(R.color.theme_primary);
        container.addView(slidingFixTabLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        FrameLayout frameLayout = new FrameLayout(this);
        viewPager = new ViewPager(this);
        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        container.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        setContentView(container, actionBar);

        viewPagerAdapter = new OrderPagerAdapter(getSupportFragmentManager(), initPagerTitle(), initFragment());
        viewPager.setAdapter(viewPagerAdapter);

        slidingFixTabLayout.setCustomTabView(R.layout.widget_tab_indicator, android.R.id.text1);
        slidingFixTabLayout.setTabStripBottomBorderThicknessPadding(AndroidUtilities.dp(2));
        slidingFixTabLayout.setSelectedIndicatorColors(Color.WHITE);
        slidingFixTabLayout.setDistributeEvenly(true);
        slidingFixTabLayout.setViewPager(viewPager);

        fragmentInde = getIntent().getIntExtra("fragmentIndex", 0);
        viewPager.setCurrentItem(fragmentInde);


        OrderServiceFragment.instance(getSupportFragmentManager()).syncOrderData();
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onOrderStateChange);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onOrderDataUpdated);
        ConnectManager.getInstance().destroyInitiator(MyOrderActivity.class);
        super.onDestroy();

    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();
//        fragments.add(new OrderFragment(ORDER_TYPE_ALL));
//        fragments.add(new OrderFragment(ORDER_TYPE_BEING));
//        fragments.add(new OrderFragment(ORDER_TYPE_COMPLETE));
//        fragments.add(new OrderFragment(ORDER_TYPE_EVALUATE));

        OrderFragment orderFragment = new OrderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(OrderFragment.ARGUMENTS_KEY_ORDER_STATUS, OrderStatus.ALL);
        orderFragment.setArguments(bundle);
        fragments.add(orderFragment);

        //
        orderFragment = new OrderFragment();
        bundle = new Bundle();
        bundle.putInt(OrderFragment.ARGUMENTS_KEY_ORDER_STATUS, OrderStatus.NO_COMPLETED);
        orderFragment.setArguments(bundle);
        fragments.add(orderFragment);

        //
        orderFragment = new OrderFragment();
        bundle = new Bundle();
        bundle.putInt(OrderFragment.ARGUMENTS_KEY_ORDER_STATUS, OrderStatus.COMPLETED);
        orderFragment.setArguments(bundle);
        fragments.add(orderFragment);

        //
        orderFragment = new OrderFragment();
        bundle = new Bundle();
        bundle.putInt(OrderFragment.ARGUMENTS_KEY_ORDER_STATUS, OrderStatus.COMMIT);
        orderFragment.setArguments(bundle);
        fragments.add(orderFragment);

        return fragments;
    }

    private List<String> initPagerTitle() {
        List<String> titles = new ArrayList<>();
        titles.add("全部");
        titles.add("处理中");
        titles.add("已完成");
        titles.add("已评价");
        return titles;
    }

    private ActionBar actionBarEvent() {
        ActionBar actionBar = new ActionBar(this);
        actionBar.setTitle("我的订单");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
        return actionBar;
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.onOrderDataUpdated) {
            needHideProgress();
        }else if(i==AppNotificationCenter.onOrderStateChange){
            OrderServiceFragment.instance(getSupportFragmentManager()).syncOrderData();
        }
    }

    class OrderPagerAdapter extends FragmentViewPagerAdapter {
        private final List<String> mPageTitle = new ArrayList<>();

        public OrderPagerAdapter(FragmentManager fragmentManager, List<String> pageTitles, List<Fragment> fragments) {
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
