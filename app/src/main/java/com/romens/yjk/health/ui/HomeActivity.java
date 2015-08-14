package com.romens.yjk.health.ui;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.FragmentViewPagerAdapter;
import com.romens.android.ui.widget.SlidingFixTabLayout;
import com.romens.yjk.health.R;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.ui.fragment.HomeDiscoveryFragment;
import com.romens.yjk.health.ui.fragment.HomeFocusFragment;
import com.romens.yjk.health.ui.fragment.HomeMyFragment;
import com.romens.yjk.health.ui.fragment.HomeStoreFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements AppNotificationCenter.NotificationCenterDelegate {

    private SlidingFixTabLayout slidingFixTabLayout;
    private ViewPager viewPager;
    private HomePagerAdapter pagerAdapter;
    private ActionBarMenuItem shoppingCartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        slidingFixTabLayout = new SlidingFixTabLayout(this);
        slidingFixTabLayout.setBackgroundResource(R.color.theme_primary);
        content.addView(slidingFixTabLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        FrameLayout frameLayout = new FrameLayout(this);
        content.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        viewPager = new ViewPager(this);
        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        setContentView(content, actionBar);


        pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), initPagerTitle(), initFragment());
        viewPager.setAdapter(pagerAdapter);

        slidingFixTabLayout.setCustomTabView(R.layout.widget_tab_indicator, android.R.id.text1);
        slidingFixTabLayout.setTabStripBottomBorderThicknessPadding(AndroidUtilities.dp(2));
        slidingFixTabLayout.setSelectedIndicatorColors(Color.WHITE);
        slidingFixTabLayout.setDistributeEvenly(true);
        slidingFixTabLayout.setViewPager(viewPager);

        actionBar.setTitle("要健康");
        actionBar.setBackButtonImage(R.drawable.ic_wallet_giftcard_white_36dp);
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.ic_menu_search);
        shoppingCartItem = actionBarMenu.addItem(1, R.drawable.ic_shopping_cart_white_24dp);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                } else if (id == 0) {
                }
            }
        });
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.shoppingCartCountChanged);
        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, 99);
            }
        }, 3000);
    }

    private List<String> initPagerTitle() {
        List<String> titles = new ArrayList<>();
        titles.add("焦点");
        titles.add("健康");
        titles.add("探索");
        titles.add("我");
        return titles;
    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFocusFragment());
        fragments.add(new HomeStoreFragment());
        fragments.add(new HomeDiscoveryFragment());
        fragments.add(new HomeMyFragment());
        return fragments;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == AppNotificationCenter.shoppingCartCountChanged) {
            int count = (int) args[0];
            updateShoppingCartCount(count);
        }
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.shoppingCartCountChanged);
        super.onDestroy();
    }

    private void updateShoppingCartCount(int count) {
        Bitmap shoppingCartCount = ShoppingCartUtils.createShoppingCartIcon(this, R.drawable.ic_shopping_cart_white_24dp, count);
        shoppingCartItem.setIcon(shoppingCartCount);
    }

    class HomePagerAdapter extends FragmentViewPagerAdapter {
        private final List<String> mPageTitle = new ArrayList<>();

        public HomePagerAdapter(FragmentManager fragmentManager, List<String> pageTitles, List<Fragment> fragments) {
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
