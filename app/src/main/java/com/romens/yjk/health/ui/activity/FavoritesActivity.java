package com.romens.yjk.health.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.FavoritesDao;
import com.romens.yjk.health.db.entity.FavoritesEntity;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.adapter.FavoritesAdapter;
import com.romens.yjk.health.ui.adapter.FavoritesSearchAdapter;
import com.romens.yjk.health.ui.fragment.ShoppingServiceFragment;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.List;

/**
 * Created by siery on 15/12/17.
 */
public class FavoritesActivity extends LightActionBarActivity implements AppNotificationCenter.NotificationCenterDelegate {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView listView;
    private FavoritesAdapter adapter;
    private FavoritesSearchAdapter searchAdapter;

    private boolean searchWas;
    private boolean searching;
    private ActionBarMenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShoppingServiceFragment.instance(getSupportFragmentManager());
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onAddMedicineFavorite);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onRemoveMedicineFavorite);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onMedicineFavoriteChanged);

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 2) {
                    if (adapter.getItemCount() > 0) {
                        new AlertDialog.Builder(FavoritesActivity.this)
                                .setTitle("我的收藏")
                                .setMessage("是否清空我的收藏?")
                                .setNegativeButton("清空", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        clearFavorites();
                                    }
                                }).setPositiveButton("取消", null).create().show();
                    } else {
                        Toast.makeText(FavoritesActivity.this, "我的收藏空空如也...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        setContentView(content, actionBar);

        ActionBarMenu actionBarMenu = actionBar.createMenu();
        searchMenuItem = addActionBarSearchItem(actionBarMenu, 1, new ActionBarMenuItem.ActionBarMenuItemSearchListener() {

            @Override
            public boolean canCollapseSearch() {
                return true;
            }

            @Override
            public void onSearchCollapse() {
                searching = false;
                searchWas = false;
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                refreshLayout.setEnabled(!searchWas);
            }

//            @Override
//            public void onSearchExpand() {
//                refreshLayout.setEnabled(false);
//            }

            @Override
            public void onTextChanged(EditText editText) {
                if (searchAdapter == null) {
                    return;
                }
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    searchWas = true;
                    if (listView != null) {
                        listView.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                    }
                }
                searchAdapter.searchDelayed(text);
                refreshLayout.setEnabled(!searchWas);
            }
        });
        searchMenuItem.getSearchField().setHint("输入商品名,比如:阿司匹林");
        actionBarMenu.addItem(2, R.drawable.ic_delete_grey600_24dp);
        setActionBarTitle(actionBar, "我的收藏");

        refreshLayout = new SwipeRefreshLayout(this);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                changeProgress(false);
                UIOpenHelper.syncFavorites(FavoritesActivity.this);
                updateGoodsFavorites();
            }
        });
        content.addView(refreshLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new RecyclerView(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

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
            public void onRemoveFavorites(FavoritesEntity entity) {
                tryRemoveMedicineFavorites(entity.getMerchandiseId());
            }
        });
        searchAdapter = new FavoritesSearchAdapter(this, new FavoritesAdapter.FavoritesCellDelegate() {
            @Override
            public void onCellClick(int position) {
                FavoritesEntity entity = searchAdapter.getItem(position);
                UIOpenHelper.openMedicineActivity(FavoritesActivity.this, entity.getMerchandiseId());
            }

            @Override
            public void onAddShoppingCart(FavoritesEntity entity) {
                ShoppingServiceFragment.instance(getSupportFragmentManager()).tryAddToShoppingCart(entity.getMerchandiseId(), entity.getPrice());
            }

            @Override
            public void onRemoveFavorites(FavoritesEntity entity) {
                tryRemoveMedicineFavorites(entity.getMerchandiseId());
            }
        });
        listView.setAdapter(adapter);
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && searching && searchWas) {
                    AndroidUtilities.hideKeyboard(getCurrentFocus());
                }
            }
        });
        updateGoodsFavorites();

        UIOpenHelper.syncFavorites(this);
    }

    private void changeProgress(boolean progress) {
        refreshLayout.setRefreshing(progress);
    }

    private void clearFavorites() {
        needShowProgress("正在清空我的收藏...");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "DeleteMyFavour", null);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                needHideProgress();
                Snackbar.make(getMyActionBar(), "清空我的收藏失败",
                        Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearFavorites();
                    }
                }).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    if (TextUtils.isEmpty(response)) {
                        clearGoodsFavorites();
                        return;
                    }
                }
                Snackbar.make(getMyActionBar(), "清空我的收藏失败",
                        Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearFavorites();
                    }
                }).show();
            }
        });
    }

    private void updateGoodsFavorites() {
        FavoritesDao favoritesDao = DBInterface.instance().openReadableDb().getFavoritesDao();
        List<FavoritesEntity> favoritesEntities = favoritesDao.loadAll();
        adapter.bindData(favoritesEntities);
    }

    private void clearGoodsFavorites() {
        FavoritesDao favoritesDao = DBInterface.instance().openWritableDb().getFavoritesDao();
        favoritesDao.deleteAll();
        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onMedicineFavoriteChanged);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == AppNotificationCenter.onRemoveMedicineFavorite) {
            needHideProgress();
            updateGoodsFavorites();
        } else if (id == AppNotificationCenter.onMedicineFavoriteChanged) {
            updateGoodsFavorites();
        } else if (id == AppNotificationCenter.onMedicineFavoriteChanged) {
            updateGoodsFavorites();
        }
    }

    private void tryRemoveMedicineFavorites(String medicineId) {
        if (TextUtils.isEmpty(medicineId)) {
            return;
        }
        needShowProgress("正在移除所选收藏商品...");
        ShoppingServiceFragment.instance(getSupportFragmentManager()).removeFavorites(medicineId);
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onAddMedicineFavorite);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onRemoveMedicineFavorite);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onMedicineFavoriteChanged);
        if (searchAdapter != null) {
            searchAdapter.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        ActionBar actionBar = getMyActionBar();
        if (actionBar.isSearchFieldVisible()) {
            actionBar.closeSearchField();
            return;
        }
        finish();
    }
}
