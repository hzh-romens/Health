package com.romens.yjk.health.ui;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.location.core.AMapLocException;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.FragmentViewPagerAdapter;
import com.romens.android.ui.cells.TextIconCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.LocationAddressHelper;
import com.romens.yjk.health.core.LocationHelper;
import com.romens.yjk.health.helper.MonitorHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.cells.AccountSettingCell;
import com.romens.yjk.health.ui.cells.HomeTabsCell;
import com.romens.yjk.health.ui.cells.LastLocationCell;
import com.romens.yjk.health.ui.fragment.HomeDiscoveryFragment;
import com.romens.yjk.health.ui.fragment.HomeFocusFragment;
import com.romens.yjk.health.ui.fragment.HomeHealthNewFragment;
import com.romens.yjk.health.ui.fragment.HomeMyNewFragment;
import com.romens.yjk.health.ui.fragment.ShopCarFragment;
import com.romens.yjk.health.ui.fragment.ShoppingServiceFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2016/2/23.
 */
public class HomeNewActivity extends BaseActivity implements AppNotificationCenter.NotificationCenterDelegate {

    private LastLocationCell lastLocationCell;
    private ViewPager viewPager;
    private HomePagerAdapter pagerAdapter;
    private ActionBarMenuItem shoppingCartItem;

    private DrawerLayout drawerLayout;
    private HomeMyNewFragment homeMyFragment;
    private ListView listView;
    public ImageView accountSettingIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginSuccess);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onShoppingCartChanged);

        ShoppingServiceFragment.instance(getSupportFragmentManager());

        setContentView(R.layout.activity_home, R.id.action_bar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });

        initSettingView();

        LinearLayout content = (LinearLayout) findViewById(R.id.content_layout);
        final ActionBar actionBar = getMyActionBar();

        FrameLayout frameLayout = new FrameLayout(this);
        content.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        viewPager = new ViewPager(this);
        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 0, 0, 64));

        HomeTabsCell tabsCell = new HomeTabsCell(this);
        frameLayout.addView(tabsCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
        tabsCell.setViewPager(viewPager);

        actionBar.setBackButtonImage(R.drawable.ic_app_icon);
        final ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.ic_menu_search);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == 0) {
                    UIOpenHelper.openSearchActivity(HomeNewActivity.this);
                }
            }
        });

        accountSettingIcon = new ImageView(HomeNewActivity.this);
        accountSettingIcon.setVisibility(View.GONE);
        accountSettingIcon.setImageResource(R.drawable.ic_setting);
        accountSettingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        actionBar.addView(accountSettingIcon, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.RIGHT, 8, 0, 48, 0));

        //定位
        lastLocationCell = new LastLocationCell(this);
        lastLocationCell.setClickable(true);
        lastLocationCell.setBackgroundResource(R.drawable.list_selector);
        lastLocationCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIOpenHelper.openUserLocationActivity(HomeNewActivity.this);
            }
        });
        actionBar.addView(lastLocationCell, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT,
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
                if (position == 4 && homeMyFragment.getUserEntity() != null) {
                    accountSettingIcon.setVisibility(View.VISIBLE);
                } else {
                    accountSettingIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //requestShopCarCountData();
        setupConfig();
        initLastLocation();
        UIOpenHelper.syncFavorites(this);
        MonitorHelper.checkUpdate(this);
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
        titles.add("购物车");
        titles.add("我的");
        return titles;
    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFocusFragment());
        fragments.add(new HomeHealthNewFragment());
        fragments.add(new HomeDiscoveryFragment());
        fragments.add(new ShopCarFragment());
        fragments.add(homeMyFragment = new HomeMyNewFragment());
        return fragments;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == AppNotificationCenter.loginSuccess) {
            UIOpenHelper.syncFavorites(HomeNewActivity.this);
        } else if (id == AppNotificationCenter.onShoppingCartChanged) {
            if (shoppingCartItem != null) {
                int count = (int) args[0];
                updateShoppingCartCount(count);
            }
        } else if (id == AppNotificationCenter.onLastLocationChanged) {
            updateLastLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLastLocation();
    }

    @Override
    public void onDestroy() {
        stopLocation();
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.loginSuccess);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onShoppingCartChanged);
        super.onDestroy();
    }

    private void updateShoppingCartCount(int count) {
        Bitmap shoppingCartCountBitmap = ShoppingCartUtils.createShoppingCartIcon(HomeNewActivity.this, R.drawable.ic_shopping_cart_white_24dp, count);
        shoppingCartItem.setIcon(shoppingCartCountBitmap);
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
        AMapLocation location = LocationHelper.getLastLocation(HomeNewActivity.this);
        String address = location == null ? null : location.getAddress();
        if (TextUtils.isEmpty(address)) {
            address = "无法获取当前位置";
        }
        lastLocationCell.setValue(address);
    }

    private LocationManagerProxy mAMapLocationManager;

    private void initLastLocation() {
        mAMapLocationManager = LocationManagerProxy.getInstance(this);
        mAMapLocationManager.setGpsEnable(true);
            /*
             * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
			 */
        // Location API定位采用GPS和网络混合定位方式，时间最短是2000毫秒
        mAMapLocationManager.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 50, new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        if (aMapLocation != null) {
                            AMapLocException exception = aMapLocation.getAMapException();
                            if (exception == null || exception.getErrorCode() == 0) {
                                LocationHelper.updateLastLocation(HomeNewActivity.this, aMapLocation);
                            }
                        }
                    }

                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
    }

    private void stopLocation() {
        if (mAMapLocationManager != null) {
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }

    public void initSettingView() {
        setRow();
        listView = (ListView) findViewById(R.id.list_view);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelection(R.drawable.list_selector);
        listView.setAdapter(new MyAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(HomeNewActivity.this, "正在开发，敬请期待!", Toast.LENGTH_SHORT).show();
                } else if (position == 1) {
                    UserConfig.clearUser();
                    UserConfig.clearConfig();
                    FacadeToken.getInstance().expired();
                    homeMyFragment.setUserEntity(null);
                    AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, -100000);
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                    accountSettingIcon.setVisibility(View.GONE);
                }
            }
        });
    }

    private int rowCount;
    private int changePasswordRow;
    //    private int checkupRow;
    private int exitRow;

    private void setRow() {
        changePasswordRow = rowCount++;
//        checkupRow = rowCount++;
        exitRow = rowCount++;
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextIconCell(HomeNewActivity.this);
            }
            TextIconCell cell = (TextIconCell) convertView;
            cell.setTextColor(0xff212121);
            cell.setValueTextColor(ResourcesConfig.textPrimary);
            if (position == changePasswordRow) {
                cell.setIconText(R.drawable.ic_change_password, "修改密码", true);
            }/* else if (position == checkupRow) {
                cell.setIconText(R.drawable.ic_checkup, "检查更新", false);
            } */ else if (position == exitRow) {
                cell.setIconText(R.drawable.ic_exit, "退出登录", true);
            }
            return convertView;
        }
    }
}

