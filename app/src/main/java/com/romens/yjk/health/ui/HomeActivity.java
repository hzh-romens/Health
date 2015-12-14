package com.romens.yjk.health.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.FrameLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.FragmentViewPagerAdapter;
import com.romens.android.ui.widget.SlidingFixTabLayout;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.CollectHelper;
import com.romens.yjk.health.core.LocationAddressHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.activity.LoginActivity;
import com.romens.yjk.health.ui.fragment.HomeDiscoveryFragment;
import com.romens.yjk.health.ui.fragment.HomeFocusFragment;
import com.romens.yjk.health.ui.fragment.HomeHealthFragment;
import com.romens.yjk.health.ui.fragment.HomeMyFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends BaseActivity implements AppNotificationCenter.NotificationCenterDelegate {

    private SlidingFixTabLayout slidingFixTabLayout;
    private ViewPager viewPager;
    private HomePagerAdapter pagerAdapter;
    private ActionBarMenuItem shoppingCartItem;

    private int sumCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);

        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        slidingFixTabLayout = new SlidingFixTabLayout(this);
        slidingFixTabLayout.setBackgroundResource(R.color.theme_primary);
        // slidingFixTabLayout.setBackgroundResource(R.color.new_grey);
        content.addView(slidingFixTabLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        FrameLayout frameLayout = new FrameLayout(this);
        content.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        viewPager = new ViewPager(this);
        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        setContentView(content, actionBar);

        pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), initPagerTitle(), initFragment());
        viewPager.setAdapter(pagerAdapter);

        slidingFixTabLayout.setCustomTabView(R.layout.widget_tab_indicator, android.R.id.text1);
        //slidingFixTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingFixTabLayout.setTabStripBottomBorderThicknessPadding(AndroidUtilities.dp(2));
        slidingFixTabLayout.setSelectedIndicatorColors(Color.WHITE);
        //slidingFixTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.themecolor));
        slidingFixTabLayout.setDistributeEvenly(true);
        slidingFixTabLayout.setViewPager(viewPager);

        actionBar.setTitle("要健康");
        actionBar.setBackButtonImage(R.drawable.yaojk_logo);
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.ic_menu_search);
        shoppingCartItem = actionBarMenu.addItem(1, R.drawable.ic_shopping_cart_white_24dp);

//        ActionBarMenuItem debugMenu = actionBarMenu.addItem(1, R.drawable.ic_ab_other);
//        debugMenu.addSubItem(2, "测试促销详情", 0);
//        debugMenu.addSubItem(3, "测试附近药店", 0);
//        debugMenu.addSubItem(4, "我的订单", 0);
//        debugMenu.addSubItem(5, "地址管理", 0);
//        debugMenu.addSubItem(6, "药店详情", 0);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
               /* if (id == -1) {
                    startActivity(new Intent(HomeActivity.this, FamilyDrugGroupActivity.class));
                } else*/
                if (id == 0) {
                    UIOpenHelper.openSearchActivity(HomeActivity.this);
                    //startActivity(new Intent(HomeActivity.this, SearchActivityNew.class));
                } else if (id == 1) {
                    if (UserConfig.isClientLogined()) {
                        startActivity(new Intent(HomeActivity.this, ShopCarActivity.class));
                    } else {
                        //跳转至登录页面
                        //Toast.makeText(HomeActivity.this, "请您先登录", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    }
                }/*  else if (key == 2) {
                    startActivity(new Intent(HomeActivity.this, SalesPromotionActivity.class));
                } else if (key == 3) {
                    startActivity(new Intent(HomeActivity.this, LocationActivity.class));
                } else if (key == 4) {
                    startActivity(new Intent(HomeActivity.this, MyOrderActivity.class));
                } else if (key == 5) {
                    startActivity(new Intent(HomeActivity.this, ControlAddressActivity.class));
                } else if (key == 6) {
                    startActivity(new Intent(HomeActivity.this, DrugStoryDetailActivity.class));
                }*/
            }
        });
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.shoppingCartCountChanged);
        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, 0);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.collectAddChange);
        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.collectAddChange, 0);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.collectDelChange);
        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.collectDelChange, 0);
        requestShopCarCountData();
        setupConfig();
    }

    private void setupConfig() {
        LocationAddressHelper.syncServerLocationAddress(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private List<String> initPagerTitle() {
        List<String> titles = new ArrayList<>();
        titles.add("首页");
        titles.add("健康");
        titles.add("发现");
        titles.add("我的");
        return titles;
    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFocusFragment());
//        fragments.add(new HomeFocusFragment());
        fragments.add(new HomeHealthFragment());
        fragments.add(new HomeDiscoveryFragment());
        fragments.add(new HomeMyFragment());
        return fragments;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == AppNotificationCenter.shoppingCartCountChanged) {
            int count = (int) args[0];
            sumCount = sumCount + count;
            //updateShoppingCartCount(count);
            if (sumCount < 0) {
                sumCount = 0;
            }
            updateShoppingCartCount(sumCount);
        } else if (id == AppNotificationCenter.collectAddChange) {
            CollectHelper.getInstance().addCollect(this);
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

    //获取购物车数量
    private void requestShopCarCountData() {
        if (UserConfig.isClientLogined()) {
            Log.i("用户guid", UserConfig.getClientUserEntity().getGuid());
            Map<String, String> args = new FacadeArgs.MapBuilder()
                    .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).build();
            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetBuyCarCount", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol).build();
            FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {

                @Override
                public void onTokenTimeout(Message msg) {
                    needHideProgress();
                    Log.e("GetBuyCarCount", "ERROR");
                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    needHideProgress();
                    if (errorMsg == null) {
                        ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                        try {
                            JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                            String buycount = jsonObject.getString("BUYCOUNT");
                            //shoppingCartItem.setIcon(Integer.parseInt(buycount));
                            sumCount = Integer.parseInt(buycount);
                            updateShoppingCartCount(sumCount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("GetBuyCarCount", "ERROR");
                    }
                }
            });
        } else {

        }
    }
}
