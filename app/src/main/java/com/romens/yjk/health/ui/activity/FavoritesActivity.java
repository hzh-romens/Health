package com.romens.yjk.health.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.gc.materialdesign.views.CheckBox;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.FavoritesDao;
import com.romens.yjk.health.db.entity.FavoritesEntity;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.adapter.FavoritesAdapter;
import com.romens.yjk.health.ui.cells.AllSelectCell;
import com.romens.yjk.health.ui.fragment.ShoppingServiceFragment;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/12/17.
 */
public class FavoritesActivity extends BaseActivity implements AppNotificationCenter.NotificationCenterDelegate {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView listView;
    private FavoritesAdapter adapter;

    private AllSelectCell allSelectCell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShoppingServiceFragment.instance(getSupportFragmentManager());
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onAddMedicineFavorite);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onRemoveMedicineFavorite);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onShoppingCartChanged);

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBar.setTitle("我的收藏");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });
        setContentView(content, actionBar);

        FrameLayout frameLayout = new FrameLayout(this);
        content.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        refreshLayout = new SwipeRefreshLayout(this);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                changeProgress(false);
                UIOpenHelper.syncFavorites(FavoritesActivity.this);
                updateGoodsFavorites(true);
            }
        });
        frameLayout.addView(refreshLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 0, 0, 48));

        listView = new RecyclerView(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        allSelectCell = new AllSelectCell(this);
        allSelectCell.setActionDelegate(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getSelectedItemCount() > 0) {
                    ArrayList<String> medicines = adapter.getSelectedItems();
                    tryRemoveMedicineFavorites(medicines);
                } else {
                    Toast.makeText(FavoritesActivity.this, "请选择需要移除收藏的商品!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        allSelectCell.setAllCheckBoxDelegate(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(CheckBox view, boolean check) {
                adapter.switchSelectAll(check);
            }
        });
        frameLayout.addView(allSelectCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));

        adapter = new FavoritesAdapter(this, new FavoritesAdapter.FavoritesCellDelegate() {
            @Override
            public void onCellClick(int position) {
                FavoritesEntity entity = adapter.getItem(position);
                UIOpenHelper.openMedicineActivity(FavoritesActivity.this, entity.getMerchandiseId());
            }

            @Override
            public void onAddShoppingCart(FavoritesEntity entity) {
                ShoppingServiceFragment.instance(getSupportFragmentManager()).tryAddToShoppingCart(entity.getMerchandiseId(), entity.getPrice());
            }

            @Override
            public void onSelectedChanged() {
                updateAllSelectCell();
            }
        });
        listView.setAdapter(adapter);
        updateAllSelectCell();
        updateGoodsFavorites(true);

        UIOpenHelper.syncFavorites(this);
    }

    private void changeProgress(boolean progress) {
        refreshLayout.setRefreshing(progress);
    }

    private void updateAllSelectCell() {
        boolean isAllSelected = adapter.isAllSelected();
        int selectedCount = adapter.getSelectedItemCount();
        allSelectCell.setValue(isAllSelected, selectedCount, R.drawable.ic_delete_grey600_24dp, selectedCount > 0 ? 0xffe51c23 : 0, true);
    }

    private void updateGoodsFavorites(boolean clear) {
        FavoritesDao favoritesDao = DBInterface.instance().openReadableDb().getFavoritesDao();
        List<FavoritesEntity> favoritesEntities = favoritesDao.loadAll();
        adapter.bindData(favoritesEntities, clear);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == AppNotificationCenter.onRemoveMedicineFavorite) {
            needHideProgress();
            updateGoodsFavorites(false);
        } else if (id == AppNotificationCenter.onMedicineFavoriteChanged) {
            updateGoodsFavorites(false);
        } else if (id == AppNotificationCenter.onMedicineFavoriteChanged) {
            updateGoodsFavorites(true);
        }
    }

    private void tryRemoveMedicineFavorites(ArrayList<String> medicineId) {
        if (medicineId != null && medicineId.size() <= 0) {
            return;
        }
        needShowProgress("正在移除所选收藏商品...");
        ShoppingServiceFragment.instance(getSupportFragmentManager()).removeFavoritesList(medicineId);
    }

    @Override
    public void onDestroy() {

        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onAddMedicineFavorite);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onRemoveMedicineFavorite);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onShoppingCartChanged);
        super.onDestroy();
    }
}
