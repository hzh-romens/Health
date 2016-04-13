package com.romens.yjk.health.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.FragmentViewPagerAdapter;
import com.romens.android.ui.widget.SlidingFixTabLayout;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.FavoritesDao;
import com.romens.yjk.health.db.entity.FavoritesEntity;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.adapter.CollectAdapter;
import com.romens.yjk.health.ui.cells.CollectDrawerCell;
import com.romens.yjk.health.ui.cells.ImageAndTextCell;
import com.romens.yjk.health.ui.cells.IsSelectCell;
import com.romens.yjk.health.ui.fragment.ShoppingServiceFragment;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/14.
 * 收藏页面
 */
public class CollectActivity extends DarkActionBarActivity implements AppNotificationCenter.NotificationCenterDelegate {

    private SlidingFixTabLayout tabLayout;
    private ViewPager viewPager;
    private CollectPagerAdapter pagerAdapter;

    private final int DRUG_TYPE = 0;
    private final int DRUG_STROY_TYPE = 1;

    private ListView listView;
    private CollectAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    //    private LinearLayout container;
    private ImageAndTextCell attachView;
    private IsSelectCell isSelectCell;

    private DrawerLayout contentView;
    private FrameLayout rightLayout;

    private String userGuid;

    @Override
    public void onDestroy() {

        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onAddMedicineFavorite);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onRemoveMedicineFavorite);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onShoppingCartChanged);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShoppingServiceFragment.instance(getSupportFragmentManager());
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onAddMedicineFavorite);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onRemoveMedicineFavorite);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onShoppingCartChanged);

        userGuid = UserGuidConfig.USER_GUID;
        contentView = new DrawerLayout(this);
        contentView.addView(subContentView(), new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));

        rightLayout = getDrawerLayout();
        DrawerLayout.LayoutParams drawLayoutParams = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);
        drawLayoutParams.gravity = Gravity.RIGHT;
        rightLayout.setLayoutParams(drawLayoutParams);
        contentView.addView(rightLayout);
        setContentView(contentView);
    }

    private FrameLayout getDrawerLayout() {
        FrameLayout drawerLayout = new FrameLayout(this);
        CollectDrawerCell collectDrawerCell = new CollectDrawerCell(this);
        collectDrawerCell.setOnActionBarClickListener(new CollectDrawerCell.onActionBarClickListener() {
            @Override
            public void onItemClick(int itemIndex) {
                if (itemIndex == -1) {
                    contentView.closeDrawer(rightLayout);
                } else if (itemIndex == 0) {
                    Toast.makeText(CollectActivity.this, "click", Toast.LENGTH_SHORT).show();
                }
            }
        });
        drawerLayout.addView(collectDrawerCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return drawerLayout;
    }

    private LinearLayout subContentView() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
//        setContentView(container);
        actionBarEvent(actionBar);
//        addFragment(container);
        // initData();
        addCellView(container);
        refreshContentView();

        return container;
    }

    private void refreshContentView() {
//        if (entities != null && entities.size() > 0) {
//            attachView.setVisibility(View.GONE);
//            isSelectCell.setVisibility(View.VISIBLE);
//            refreshLayout.setVisibility(View.VISIBLE);
//        } else {
//            refreshLayout.setVisibility(View.GONE);
//            isSelectCell.setVisibility(View.GONE);
//            attachView.setVisibility(View.VISIBLE);
//        }

    }

    private void addCellView(LinearLayout container) {
        attachView = new ImageAndTextCell(this);
        attachView.setImageAndText(R.drawable.no_collect_img, "您还没有关注过药品");
        LinearLayout.LayoutParams layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.setMargins(AndroidUtilities.dp(0), AndroidUtilities.dp(100), AndroidUtilities.dp(0), AndroidUtilities.dp(0));
        attachView.setLayoutParams(layoutParams);
        container.addView(attachView);
        haveRefreshCollectView(container);
    }

    private void haveRefreshCollectView(LinearLayout container) {
        refreshLayout = new SwipeRefreshLayout(this);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        LinearLayout.LayoutParams refreshlayoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        refreshlayoutParams.weight = 1;
        refreshLayout.setLayoutParams(refreshlayoutParams);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });
        container.addView(refreshLayout);

        listView = new ListView(this);
        listView.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        refreshLayout.addView(listView);
        adapter = new CollectAdapter(this);
        listView.setAdapter(adapter);

        isSelectCell = new IsSelectCell(this);
        isSelectCell.setGravity(Gravity.BOTTOM);
        isSelectCell.setBackgroundColor(Color.TRANSPARENT);
        container.addView(isSelectCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        isSelectCell.setInfo("全选", false);
        isSelectCell.setRightBtnTxt("删除");
        isSelectCell.needTopDivider(true);
//        isSelectCell.setViewClick(new IsSelectCell.onViewClick() {
//            @Override
//            public void onImageViewClick() {
//                boolean isSelect = isSelectCell.changeSelect();
//                for (int i = 0; i < entities.size(); i++) {
//                    entities.get(i).setIsSelect(isSelect);
//                }
//                adapter.setEntities(entities);
//            }
//
//            @Override
//            public void rightBtnClick() {
//                entities = adapter.getFavoritesEntities();
//                CollectHelper.getInstance().delCollect(CollectActivity.this, delEntity(entities));
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGoodsFavorites();
    }

    @Override
    protected String getActivityName() {
        return "我的收藏";
    }

    private void actionBarEvent(ActionBar actionBar) {
        actionBar.setTitle("我的收藏");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                super.onItemClick(i);
                if (i == -1) {
                    finish();
                } /*else if (i == 0) {
                    Toast.makeText(CollectActivity.this, "filter", Toast.LENGTH_SHORT).show();
                    contentView.openDrawer(GravityCompat.END);
                }*/
            }
        });
    }

    private void updateGoodsFavorites() {
        FavoritesDao favoritesDao = DBInterface.instance().openReadableDb().getFavoritesDao();
        List<FavoritesEntity> favoritesEntities = favoritesDao.loadAll();
        adapter.bindData(favoritesEntities);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == AppNotificationCenter.onRemoveMedicineFavorite) {
            needHideProgress();
            updateGoodsFavorites();
        } else if (id == AppNotificationCenter.onMedicineFavoriteChanged) {
            updateGoodsFavorites();
        }
    }

//    private void tryRemoveMedicineFavorites(String... medicineId) {
//        needShowProgress("正在移除所选收藏商品...");
//        ShoppingServiceFragment.instance(getSupportFragmentManager()).removeFavoritesList(medicineId);
//    }

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
