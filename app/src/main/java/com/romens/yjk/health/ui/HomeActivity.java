package com.romens.yjk.health.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.amap.api.location.AMapLocation;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.FragmentViewPagerAdapter;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.LocationAddressHelper;
import com.romens.yjk.health.core.LocationHelper;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.helper.BugHelper;
import com.romens.yjk.health.helper.MonitorHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.activity.BaseLocationActivity;
import com.romens.yjk.health.ui.cells.HomeTabsCell;
import com.romens.yjk.health.ui.cells.LastLocationCell;
import com.romens.yjk.health.ui.fragment.HomeDiscoveryFragment;
import com.romens.yjk.health.ui.fragment.HomeFocusFragment;
import com.romens.yjk.health.ui.fragment.HomeHealthNewFragment;
import com.romens.yjk.health.ui.fragment.HomeMyNewFragment;
import com.romens.yjk.health.ui.fragment.ShoppingCartFragment;
import com.romens.yjk.health.ui.fragment.ShoppingServiceFragment;
import com.romens.yjk.health.wx.mta.MTAManager;
import com.romens.yjk.health.wx.push.PushManager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseLocationActivity implements AppNotificationCenter.NotificationCenterDelegate {

    private LastLocationCell lastLocationCell;
    private ViewPager viewPager;
    private HomePagerAdapter pagerAdapter;

    private ActionBarMenuItem otherMenu;

//    private DrawerLayout drawerLayout;
//    private HomeMyNewFragment homeMyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserConfig.getInstance().checkAppAccount();
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginSuccess);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onShoppingCartChanged);

        ShoppingServiceFragment.instance(getSupportFragmentManager());

//        drawerLayout = new DrawerLayout(this);

//        final AccountSettingCell accountSettingCell = new AccountSettingCell(this);
//        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        params.gravity = Gravity.RIGHT;
//        accountSettingCell.setLayoutParams(params);
//        drawerLayout.addView(accountSettingCell);

//        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                accountSettingCell.setFocusable(true);
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//
//            }
//
//            @Override
//            public void onDrawerStateChanged(int newState) {
//
//            }
//        });

//        accountSettingCell.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 2) {
//                    Toast.makeText(HomeActivity.this, "click", Toast.LENGTH_SHORT).show();
//                    UserConfig.clearUser();
//                    UserConfig.clearConfig();
//                    FacadeToken.getInstance().expired();
//                    homeMyFragment.setUserEntity(null);
//                    homeMyFragment.updateData();
//                    AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, -100000);
//                }
//            }
//        });

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
//        drawerLayout.addView(content, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        final ActionBar actionBar = new ActionBar(this);

        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        FrameLayout frameLayout = new FrameLayout(this);
        content.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        viewPager = new ViewPager(this);
        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 0, 0, 64));

        HomeTabsCell tabsCell = new HomeTabsCell(this);
        frameLayout.addView(tabsCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
        tabsCell.setViewPager(viewPager);
        setContentView(content, actionBar);

        //actionBar.setTitle(getString(R.string.app_name));
        actionBar.setBackButtonImage(R.drawable.ic_app_icon);

        final ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.ic_menu_search);
        //   shoppingCartItem = actionBarMenu.addItem(1, R.drawable.ic_shopping_cart_white_24dp);
        otherMenu = actionBarMenu.addItem(1, R.drawable.ic_more_vert_white_24dp);
        otherMenu.addSubItem(5, "我的消息", R.drawable.ic_message_read);
        otherMenu.addSubItem(3, "修改密码", R.drawable.ic_change_password);
        otherMenu.addSubItem(4, "退出登录", R.drawable.ic_exit);
        otherMenu.addSubItem(2, "关于我们", R.drawable.ic_about);


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
                } else if (id == 2) {
                    UIOpenHelper.openAbout(HomeActivity.this);
                } else if (id == 3) {
                    UIOpenHelper.openChangedPasswordActivity(HomeActivity.this);
                } else if (id == 4) {
                    needLogout();
                } else if (id == 5) {
                    UIOpenHelper.openPushMessagesActivity(HomeActivity.this);
                }
//                else if (id == 1) {
//                    if (UserConfig.isClientLogined()) {
//                        //startActivity(new Intent(HomeActivity.this, ShopCarActivity.class));
//                        UIOpenHelper.openShoppingCartActivity(HomeActivity.this);
//                        //startActivity(new Intent(HomeActivity.this, CuoponActivity.class));
//                        //startActivity(new Intent(HomeActivity.this, BindMemberActivity.class));
//                    }
//                    else {
//                        //跳转至登录页面
//                        //Toast.makeText(HomeActivity.this, "请您先登录", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
//                    }
//                }
                /*  else if (key == 2) {
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

//        final ImageView accountSettingIcon = new ImageView(HomeActivity.this);
//        accountSettingIcon.setVisibility(View.GONE);
//        accountSettingIcon.setImageResource(R.drawable.ic_setting);
//        accountSettingIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawerLayout.openDrawer(Gravity.RIGHT);
//            }
//        });
//        actionBar.addView(accountSettingIcon, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
//                Gravity.CENTER_VERTICAL | Gravity.RIGHT, 8, 0, 48, 0));

        //定位
        lastLocationCell = new LastLocationCell(this);
        lastLocationCell.setClickable(true);
        lastLocationCell.setBackgroundResource(R.drawable.list_selector);
        lastLocationCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIOpenHelper.openUserLocationActivity(HomeActivity.this);
            }
        });
        actionBar.addView(lastLocationCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.CENTER_VERTICAL | Gravity.LEFT, 56, 0, 104, 0));

        tabsCell.addView(R.drawable.ic_tab_home, R.drawable.ic_tab_home_pressed, "首页");
        tabsCell.addView(R.drawable.ic_tab_fl, R.drawable.ic_tab_fl_pressed, "分类");
        tabsCell.addView(R.drawable.ic_tab_fx, R.drawable.ic_tab_fx_pressed, "发现");
        tabsCell.addView(R.drawable.ic_tab_shopping, R.drawable.ic_tab_shopping_pressed, "购物车");
        tabsCell.addView(R.drawable.ic_tab_my, R.drawable.ic_tab_my_pressed, "我的");
        pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), initPagerTitle(), initFragment());
        viewPager.setAdapter(pagerAdapter);
        tabsCell.setSelected(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    lastLocationCell.setVisibility(View.VISIBLE);
                    getMyActionBar().setTitle("");
                } else {
                    lastLocationCell.setVisibility(View.GONE);
                    getMyActionBar().setTitle(getString(R.string.app_name));
                }
//                if (position == 4) {
//                    accountSettingIcon.setVisibility(View.VISIBLE);
//                } else {
//                    accountSettingIcon.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        BugHelper.updateBugUser(this);
        //requestShopCarCountData();
        setupConfig();
        onLoginStateChanged();
        startLocation();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                UIOpenHelper.syncFavorites(HomeActivity.this);
                MonitorHelper.checkUpdate(HomeActivity.this);
            }
        });

        MTAManager.testConnectServerSpeed();
        PushManager.doUpload(this);
    }

    /**
     * 登录状态变化
     */
    private void onLoginStateChanged() {
        if (UserConfig.isClientLogined()) {
            otherMenu.showSubItem(3);
            otherMenu.showSubItem(4);
        } else {
            otherMenu.hideSubItem(3);
            otherMenu.hideSubItem(4);
        }
    }

    /**
     * 退出登录
     */
    private void needLogout() {
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage("是否确定退出应用?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        UserSession.getInstance().needLoginOut();
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }

    private void setupConfig() {
        LocationAddressHelper.syncServerLocationAddress(this);
    }


    private List<String> initPagerTitle() {
        List<String> titles = new ArrayList<>();
        titles.add("首页");
        titles.add("健康");
        titles.add("发现");
        titles.add("购物车");
        titles.add("我的");
        return titles;
    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFocusFragment());
        fragments.add(new HomeHealthNewFragment());
//        fragments.add(new HomeHealthFragment());
        fragments.add(new HomeDiscoveryFragment());
//        ShopCarFragment shopCarFragment = new ShopCarFragment();
//        shopCarFragment.setTitleView(false);
        fragments.add(new ShoppingCartFragment());
//        fragments.add(new HomeMyFragment());
        fragments.add(new HomeMyNewFragment());
        return fragments;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == AppNotificationCenter.loginSuccess) {
            onLoginStateChanged();
            UIOpenHelper.syncFavorites(HomeActivity.this);
            BugHelper.updateBugUser(this);
        } else if (id == AppNotificationCenter.loginOut) {
            onLoginStateChanged();
        } else if (id == AppNotificationCenter.onLastLocationChanged) {
            updateLastLocation();
        } else if (id == AppNotificationCenter.onReceivePushMessage) {
            //boolean hasUnRead = DBInterface.instance().hasUnReadPushMessage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLastLocation();
    }

    @Override
    protected String getActivityName() {
        return "首页";
    }

    @Override
    public void onDestroy() {
        MonitorHelper.unregisterUpdate();
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.loginSuccess);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onShoppingCartChanged);
        super.onDestroy();
    }

    @Override
    protected void onLocationSuccess(AMapLocation aMapLocation) {
        LocationHelper.updateLastLocation(HomeActivity.this, aMapLocation);
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

    private void updateLastLocation() {
        AMapLocation location = LocationHelper.getLastLocation(HomeActivity.this);
        String address = location == null ? null : location.getAddress();
        if (TextUtils.isEmpty(address)) {
            address = "无法获取当前位置";
        }
        lastLocationCell.setValue(String.format("送至: %s", address));
    }

//    //获取购物车数量
//    private void requestShopCarCountData() {
//        if (UserConfig.isClientLogined()) {
//            Log.i("用户guid", UserConfig.getClientUserEntity().getGuid());
//            Map<String, String> args = new FacadeArgs.MapBuilder()
//                    .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).build();
//            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetBuyCarCount", args);
//            protocol.withToken(FacadeToken.getInstance().getAuthToken());
//            Message message = new Message.MessageBuilder()
//                    .withProtocol(protocol).build();
//            FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
//
//                @Override
//                public void onTokenTimeout(Message msg) {
//                    needHideProgress();
//                    Log.e("GetBuyCarCount", "ERROR");
//                }
//
//                @Override
//                public void onResult(Message msg, Message errorMsg) {
//                    needHideProgress();
//                    if (errorMsg == null) {
//                        ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
//                        try {
//                            JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
//                            String buycount = jsonObject.getString("BUYCOUNT");
//                            //shoppingCartItem.setIcon(Integer.parseInt(buycount));
//                            sumCount = Integer.parseInt(buycount);
//                            updateShoppingCartCount(sumCount);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Log.e("GetBuyCarCount", "ERROR");
//                    }
//                }
//            });
//        } else {
//
//        }
//    }

}
