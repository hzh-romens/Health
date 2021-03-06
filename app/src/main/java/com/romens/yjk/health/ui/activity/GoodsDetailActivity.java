package com.romens.yjk.health.ui.activity;


import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.fasterxml.jackson.databind.JsonNode;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.romens.android.AndroidUtilities;
import com.romens.android.log.FileLog;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.android.ui.cells.EmptyCell;
import com.romens.android.ui.cells.HeaderCell;
import com.romens.android.ui.cells.LoadingCell;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextInfoCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.common.GoodsFlag;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.LocationHelper;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.HistoryDao;
import com.romens.yjk.health.db.entity.HistoryEntity;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.GoodsCommentEntity;
import com.romens.yjk.health.model.MedicineGoodsItem;
import com.romens.yjk.health.model.MedicineSaleStoreEntity;
import com.romens.yjk.health.model.MedicineServiceModeEntity;
import com.romens.yjk.health.ui.ShoppingCartUtils;
import com.romens.yjk.health.ui.base.LightActionBarActivity;
import com.romens.yjk.health.ui.cells.GoodsCommentCell;
import com.romens.yjk.health.ui.cells.MedicineImagesCell;
import com.romens.yjk.health.ui.cells.MedicineMainCell;
import com.romens.yjk.health.ui.cells.MedicinePriceCell;
import com.romens.yjk.health.ui.cells.MedicinePropertyCell;
import com.romens.yjk.health.ui.cells.MedicineServiceModesCell;
import com.romens.yjk.health.ui.cells.MedicineStoreCell;
import com.romens.yjk.health.ui.cells.TextNavInfoCell;
import com.romens.yjk.health.ui.fragment.ShoppingServiceFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.DeleteQuery;

/**
 * Created by siery on 15/12/14.
 */
public class GoodsDetailActivity extends LightActionBarActivity implements AppNotificationCenter.NotificationCenterDelegate {

    public static final String ARGUMENTS_KEY_ID = "medicine_goods_id";
    public static final String ARGUMENTS_KEY_CHECK_NEAR_STORE = "check_near_store";

    private static final int REQUEST_CODE_LOGIN_OPEN_SHOPPINGCART = 0;

    private ProgressBarDeterminate progressView;
    private FrameLayout fragmentView;

    private MedicineImagesCell goodsImageCell;

    private FrameLayout mapViewClip;
    private GoodsDetailAdapter adapter;
    private ListView listView;

    private int markerTop;

    private boolean firstWas = false;

    private int overScrollHeight = MedicineImagesCell.defaultHeight;//AndroidUtilities.dp(MedicineImagesCell.defaultHeight);
    // AndroidUtilities.displaySize.x;
    // - AndroidUtilities.getCurrentActionBarHeight() - AndroidUtilities.dp(66);

    private int halfHeight;


    private String goodsId;
    private boolean checkNearStore = true;

    private ActionBarMenuItem refreshMenuItem;
    private ActionBarMenuItem shoppingCartMenuItem;

    private int goodsFlag = GoodsFlag.NORMAL;


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
        Bundle arguments = getIntent().getExtras();
        //商品标识
        goodsFlag = arguments.getInt(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG, GoodsFlag.NORMAL);

        goodsId = arguments.getString(ARGUMENTS_KEY_ID, "");
        checkNearStore = arguments.getBoolean(ARGUMENTS_KEY_CHECK_NEAR_STORE, true);
        final ActionBar actionBar = new ActionBar(this);
        actionBar.setAllowOverlayTitle(true);
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setTitle("");

        fragmentView = new FrameLayout(this) {
            private boolean first = true;

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);

                if (changed) {
                    fixLayoutInternal(first);
                    first = false;
                }
            }
        };


        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        FrameLayout topContainer = new FrameLayout(this);
        topContainer.addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));

        progressView = new ProgressBarDeterminate(this);
        progressView.setBackgroundColor(ResourcesConfig.primaryColor);
        progressView.setMax(100);
        progressView.setMin(0);
        progressView.setVisibility(View.INVISIBLE);
        topContainer.addView(progressView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 2, Gravity.BOTTOM));

        content.addView(topContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        listView = new ListView(this);
        //解决魅族滑动问题
        listView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        listView.setAdapter(adapter = new GoodsDetailAdapter(this));
        listView.setVerticalScrollBarEnabled(false);
        listView.setDividerHeight(0);
        listView.setDivider(null);
        fragmentView.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                ActionBar actionBar1 = getMyActionBar();
                if (actionBar1 != null) {
                    if (firstVisibleItem > 1) {
                        setActionBarTitle(actionBar1, currMedicineGoodsItem == null ? "" : currMedicineGoodsItem.name);
                    } else {
                        setActionBarTitle(actionBar1, "");
                    }
                }
                if (totalItemCount == 0) {
                    return;
                }
                updateClipView(firstVisibleItem);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleListItemClick(position);
            }
        });
        adapter.setOverScrollHeight(overScrollHeight);

        mapViewClip = new FrameLayout(this);
        fragmentView.addView(mapViewClip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));

        goodsImageCell = new MedicineImagesCell(this);

        View shadow = new View(this);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        mapViewClip.addView(shadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(3), Gravity.LEFT | Gravity.BOTTOM));

        content.addView(fragmentView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        setContentView(content, actionBar);

        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 1) {
                    UIOpenHelper.openShoppingCartActivityForCheckLogin(GoodsDetailActivity.this, REQUEST_CODE_LOGIN_OPEN_SHOPPINGCART);
                } else if (id == 2) {
                    requestMedicineGoodsData();
                }
            }
        });
        shoppingCartMenuItem = actionBarMenu.addItem(1, R.drawable.ic_shopping_cart_grey600_24dp);
        refreshMenuItem = actionBarMenu.addItem(2, R.drawable.ic_refresh_grey600_24dp);
        int count = ShoppingServiceFragment.instance(getSupportFragmentManager()).getShoppingCartCount();
        Bitmap shoppingCartCountBitmap = ShoppingCartUtils.createShoppingCartIcon(GoodsDetailActivity.this, R.drawable.ic_shopping_cart_grey600_24dp, count);
        shoppingCartMenuItem.setIcon(shoppingCartCountBitmap);

        updateDate();

        requestMedicineGoodsData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN_OPEN_SHOPPINGCART) {
            if (resultCode == Activity.RESULT_OK) {
                UIOpenHelper.openShoppingCartActivity(GoodsDetailActivity.this);
            }
        }
    }

    private void handleListItemClick(int position) {
        if (position == serviceCallCenterRow) {
            //Toast.makeText(GoodsDetailActivity.this, "敬请期待!", Toast.LENGTH_SHORT).show();
            UIOpenHelper.openCallCenterFromGoodsPage(GoodsDetailActivity.this, goodsId);
        } else if (position == serviceMedicineManualRow) {
            UIOpenHelper.openMedicineManualActivity(GoodsDetailActivity.this, currMedicineGoodsItem.guid, currMedicineGoodsItem.name);
        } else if (position == otherStoresRow) {
            expendSaleStores = true;
            updateDate();
        }
    }

    private void showProgress(boolean progress) {
        progressView.setVisibility(progress ? View.VISIBLE : View.INVISIBLE);
    }


    private void updateClipView(int firstVisibleItem) {
        int height = 0;
        int top = 0;
        View child = listView.getChildAt(0);
        if (child != null) {
            if (firstVisibleItem == 0) {
                top = child.getTop();
                height = overScrollHeight + (top < 0 ? top : 0);
                halfHeight = (top < 0 ? top : 0) / 2;
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mapViewClip.getLayoutParams();
            if (layoutParams != null) {
                if (height <= 0) {
                    if (goodsImageCell.getVisibility() == View.VISIBLE) {
                        goodsImageCell.setVisibility(View.INVISIBLE);
                        mapViewClip.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (goodsImageCell.getVisibility() == View.INVISIBLE) {
                        goodsImageCell.setVisibility(View.VISIBLE);
                        mapViewClip.setVisibility(View.VISIBLE);
                    }
                }
                if (Build.VERSION.SDK_INT >= 11) {
                    mapViewClip.setTranslationY(Math.min(0, top));
                    goodsImageCell.setTranslationY(Math.max(0, -top / 2));

                    if (goodsImageCell != null) {
                        layoutParams = (FrameLayout.LayoutParams) goodsImageCell.getLayoutParams();
                        if (layoutParams != null && layoutParams.height != overScrollHeight + AndroidUtilities.dp(10)) {
                            layoutParams.height = overScrollHeight + AndroidUtilities.dp(10);
                            goodsImageCell.setPadding(0, 0, 0, AndroidUtilities.dp(10));
                            goodsImageCell.setLayoutParams(layoutParams);
                        }
                    }
                } else {
                    markerTop = 0;
                    layoutParams.height = height;
                    mapViewClip.setLayoutParams(layoutParams);

                    if (goodsImageCell != null) {
                        layoutParams = (FrameLayout.LayoutParams) goodsImageCell.getLayoutParams();
                        if (layoutParams != null) {
                            layoutParams.topMargin = halfHeight;
                            layoutParams.height = overScrollHeight + AndroidUtilities.dp(10);
                            goodsImageCell.setPadding(0, 0, 0, AndroidUtilities.dp(10));
                            goodsImageCell.setLayoutParams(layoutParams);
                        }
                    }
                }
            }
        }
    }

    private void fixLayoutInternal(final boolean resume) {
        if (listView != null) {
            int height = 0;//(getMyActionBar().getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.getCurrentActionBarHeight();
            //int viewHeight = calendarView.getMeasuredHeight();
            int viewHeight = MedicineImagesCell.defaultHeight;//AndroidUtilities.dp(MedicineImagesCell.defaultHeight);
            overScrollHeight = resume ? overScrollHeight : viewHeight - height;
            //overScrollHeight = resume ? overScrollHeight : viewHeight - AndroidUtilities.dp(66) - height;

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
            layoutParams.topMargin = height;
            listView.setLayoutParams(layoutParams);
            layoutParams = (FrameLayout.LayoutParams) mapViewClip.getLayoutParams();
            layoutParams.topMargin = height;
            layoutParams.height = overScrollHeight;
            mapViewClip.setLayoutParams(layoutParams);

            adapter.setOverScrollHeight(overScrollHeight);
            layoutParams = (FrameLayout.LayoutParams) goodsImageCell.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = overScrollHeight + AndroidUtilities.dp(10);
                if (goodsImageCell != null) {
                    goodsImageCell.setPadding(0, 0, 0, AndroidUtilities.dp(10));
                }
                goodsImageCell.setLayoutParams(layoutParams);
            }
            adapter.notifyDataSetChanged();

            if (resume) {
                updateClipView(0);
//                listView.setSelectionFromTop(0, -(int) (AndroidUtilities.dp(56) * 2.5f + AndroidUtilities.dp(36 + 66)));
//                updateClipView(listView.getFirstVisiblePosition());
//                listView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        listView.setSelectionFromTop(0, -(int) (AndroidUtilities.dp(56) * 2.5f + AndroidUtilities.dp(36 + 66)));
//                        updateClipView(listView.getFirstVisiblePosition());
//                    }
//                });
            } else {
                updateClipView(listView.getFirstVisiblePosition());
            }
        }
    }

    int sumCount;

    private void tryAddMedicineFavorites() {
        isFavorites = true;
        updateDate();
        ShoppingServiceFragment.instance(getSupportFragmentManager()).addFavorites(goodsId);
    }

    private void tryRemoveMedicineFavorites() {
        isFavorites = false;
        updateDate();
        ShoppingServiceFragment.instance(getSupportFragmentManager()).removeFavorites(goodsId);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == AppNotificationCenter.onAddMedicineFavorite) {
            if (args.length > 0) {
                String changedId = args[0].toString();
                if (TextUtils.equals(goodsId, changedId)) {
                    int isSuccess = (int) args[1];
                    if (isSuccess == 0) {
                        Snackbar.make(getMyActionBar(),
                                "加入收藏夹失败",
                                Snackbar.LENGTH_LONG).setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tryAddMedicineFavorites();
                            }
                        }).show();
                    } else {
                        isFavorites = true;
                        updateDate();
                    }
                }
            }
        } else if (id == AppNotificationCenter.onRemoveMedicineFavorite) {
            if (args.length > 0) {
                String changedId = args[0].toString();
                if (TextUtils.equals(goodsId, changedId)) {
                    int isSuccess = (int) args[1];
                    if (isSuccess == 0) {
                        Snackbar.make(getMyActionBar(),
                                "从收藏夹移除失败",
                                Snackbar.LENGTH_LONG).setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tryRemoveMedicineFavorites();
                            }
                        }).show();
                    } else {
                        isFavorites = false;
                        updateDate();
                    }
                }
            }
        } else if (id == AppNotificationCenter.onMedicineFavoriteChanged) {
            updateGoodsFavorites();
            updateDate();
        } else if (id == AppNotificationCenter.onShoppingCartChanged) {
            if (shoppingCartMenuItem != null) {
                int count = (int) args[0];
                sumCount = sumCount + count;
                if (sumCount < 0) {
                    sumCount = 0;
                }
                Bitmap shoppingCartCountBitmap = ShoppingCartUtils.createShoppingCartIcon(GoodsDetailActivity.this, R.drawable.ic_shopping_cart_grey600_24dp, count);
                shoppingCartMenuItem.setIcon(shoppingCartCountBitmap);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected String getActivityName() {
        return String.format("商品详细页[%s][%s]",goodsId==null?"":goodsId,goodsFlag==GoodsFlag.MEDICARE?"医保药品":"药品");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!AndroidUtilities.isTablet()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        try {
            if (goodsImageCell.getParent() instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) goodsImageCell.getParent();
                viewGroup.removeView(goodsImageCell);
            }
        } catch (Exception e) {
            FileLog.e("romens", e);
        }
        if (mapViewClip != null) {
            mapViewClip.addView(goodsImageCell, 0, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, overScrollHeight + AndroidUtilities.dp(10), Gravity.TOP | Gravity.LEFT));
            updateClipView(listView.getFirstVisiblePosition());
        } else {
            fragmentView.addView(goodsImageCell, 0, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        }
        fixLayoutInternal(true);
    }

    private void requestMedicineGoodsData() {
        currMedicineGoodsItem = null;
        currMedicineGoodsServiceModes.clear();
        updateDate();
        if (TextUtils.isEmpty(goodsId)) {
            return;
        }
        showProgress(true);
        Map<String, Object> args = new HashMap<>();
        args.put("GUID", goodsId);
        args.put("FLAG", GoodsFlag.checkFlagForArg(goodsFlag));
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodsInfo", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(GoodsDetailActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        showProgress(false);
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (response != null && response.size() > 0) {
                                currMedicineGoodsItem = new MedicineGoodsItem(response.get(0));
                                updateGoodsFavorites();
                                initMedicineServiceModes();
                                AddToHistory(currMedicineGoodsItem);
                            }
                            loadMedicineSaleStores();
                            loadCommentData();
                        }
                        updateDate();
                    }
                }).build();
        ConnectManager.getInstance().request(GoodsDetailActivity.this, connect);

//        Message message = new Message.MessageBuilder().withProtocol(protocol).build();
//        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
//            @Override
//            public void onTokenTimeout(Message message) {
//                showProgress(false);
//            }
//
//            @Override
//            public void onResult(Message msg, Message errorMsg) {
//
//            }
//        });
    }

    private void updateGoodsFavorites() {
        isFavorites = DBInterface.instance().isFavorite(goodsId);
    }

    private void initMedicineServiceModes() {
        currMedicineGoodsServiceModes.add(new MedicineServiceModeEntity(R.drawable.ic_check_circle_grey600_18dp, 0xff42bd41, "正品保证"));
        //currMedicineGoodsServiceModes.add(new MedicineServiceModeEntity(R.drawable.ic_check_circle_grey600_18dp, 0xff42bd41, "免运费"));
        currMedicineGoodsServiceModes.add(new MedicineServiceModeEntity(R.drawable.ic_check_circle_grey600_18dp, 0xff42bd41, "货到付款"));
        currMedicineGoodsServiceModes.add(new MedicineServiceModeEntity(R.drawable.ic_check_circle_grey600_18dp, 0xff42bd41, "在线支付"));
    }

    private int rowCount = 1;
    private int goodsEmptyRow;
    private int goodsMainRow;
    private int goodsPriceRow;
    //促销信息
    private int goodsSalePromotionRow;
    private int goodsCDRow;
    private int goodsSpecRow;
    //运费信息
    private int goodsShippingCostRow;
    private int tipInfoRow;
    private int goodsServiceModesRow;

    private int dividerRow;

    private int storeSectionRow;
    private int storeRow;
    private int otherStoresRow;
    private int otherStoresBeginRow;
    private int otherStoresEndRow;
    private int storeDividerRow;

    private int serviceSectionRow;
    private int serviceMedicineManualRow;
    private int serviceCallCenterRow;

    private int commentDividerRow;
    private int commentSection1Row;
    private int commentLoadingRow;
    private int commentEmptyRow;
    private int commentBeginRow;
    private int commentEndRow;
    //private int commentMoreRow;

    private int bottomDividerRow;


    //当前商品对象
    private MedicineGoodsItem currMedicineGoodsItem;

    private boolean isFavorites = false;
    //支持的购买方式
    private final List<MedicineServiceModeEntity> currMedicineGoodsServiceModes = new ArrayList<>();
    //附近其他在售药店
    private final List<MedicineSaleStoreEntity> saleStoreEntities = new ArrayList<>();
    //是否点击查看更多在售药店
    private boolean expendSaleStores = false;

    //相关评论
    private final List<GoodsCommentEntity> goodsCommentEntities = new ArrayList<>();
    private boolean loadingGoodsComment = false;

    private void updateDate() {
        rowCount = 1;
        clearRow();
        if (currMedicineGoodsItem != null) {
            goodsImageCell.bindData(currMedicineGoodsItem.largeImages);
            goodsEmptyRow = -1;
            goodsMainRow = rowCount++;
            goodsPriceRow = rowCount++;

            if (currMedicineGoodsItem.hasSalesPromotionDesc()) {
                goodsSalePromotionRow = rowCount++;
            }

            goodsCDRow = rowCount++;
            goodsSpecRow = rowCount++;
            if (currMedicineGoodsItem.hasShippingCostsDesc()) {
                goodsShippingCostRow = rowCount++;
            }
            if (currMedicineGoodsItem.hasTipDescText()) {
                tipInfoRow = rowCount++;
            }
            goodsServiceModesRow = rowCount++;

            dividerRow = rowCount++;
            storeSectionRow = rowCount++;
            storeRow = rowCount++;

            otherStoresRow = -1;
            otherStoresBeginRow = -1;
            otherStoresEndRow = -1;

            if (saleStoreEntities.size() > 0) {
                if (expendSaleStores) {
                    otherStoresRow = -1;
                    otherStoresBeginRow = rowCount;
                    rowCount += saleStoreEntities.size();
                    otherStoresEndRow = rowCount - 1;
                } else {
                    otherStoresRow = rowCount++;
                }
            }
            storeDividerRow = rowCount++;

            serviceSectionRow = rowCount++;
            serviceMedicineManualRow = rowCount++;
            serviceCallCenterRow = rowCount++;

            commentDividerRow = rowCount++;
            commentSection1Row = rowCount++;
            if (loadingGoodsComment) {
                commentLoadingRow = rowCount++;
            } else {
                if (goodsCommentEntities.size() <= 0) {
                    commentEmptyRow = rowCount++;
                } else {
                    commentBeginRow = rowCount;
                    rowCount += goodsCommentEntities.size();
                    commentEndRow = rowCount;
                    //commentMoreRow = rowCount++;
                }
            }

        } else {
            goodsImageCell.bindData(new ArrayList<String>());
            if (TextUtils.isEmpty(goodsId)) {
                goodsEmptyRow = rowCount++;
            }
        }
        bottomDividerRow = rowCount++;
        adapter.notifyDataSetChanged();
    }

    private void clearRow() {
        goodsEmptyRow = -1;
        goodsMainRow = -1;
        goodsPriceRow = -1;
        goodsCDRow = -1;
        goodsSpecRow = -1;
        goodsShippingCostRow = -1;
        goodsSalePromotionRow = -1;
        tipInfoRow = -1;
        goodsServiceModesRow = -1;

        dividerRow = -1;

        storeSectionRow = -1;
        storeRow = -1;
        otherStoresRow = -1;
        otherStoresBeginRow = -1;
        otherStoresEndRow = -1;
        storeDividerRow = -1;

        serviceSectionRow = -1;
        serviceMedicineManualRow = -1;
        serviceCallCenterRow = -1;

        commentDividerRow = -1;
        commentSection1Row = -1;
        commentLoadingRow = -1;
        commentEmptyRow = -1;
        commentBeginRow = -1;
        commentEndRow = -1;
        //commentMoreRow = -1;

        bottomDividerRow = -1;
    }

    /**
     * 添加商品到购物车
     *
     * @param goodsItem
     */
    private void addGoodsToShoppingCart(MedicineGoodsItem goodsItem) {
        if (goodsFlag == GoodsFlag.MEDICARE) {
            ShoppingServiceFragment.instance(getSupportFragmentManager()).tryAddToShoppingCartForMedicare(goodsItem);
        } else {
            ShoppingServiceFragment.instance(getSupportFragmentManager()).tryAddToShoppingCart(goodsItem);
        }
    }

    class GoodsDetailAdapter extends BaseFragmentAdapter {
        private int overScrollHeight;
        private Context adapterContext;

        public GoodsDetailAdapter(Context context) {
            adapterContext = context;
        }

        public void setOverScrollHeight(int value) {
            overScrollHeight = value;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == bottomDividerRow) {
                return 0;
            } else if (position == goodsMainRow) {
                return 2;
            } else if (position == goodsPriceRow) {
                return 3;
            } else if (position == goodsCDRow || position == goodsSpecRow || position == goodsShippingCostRow || position == goodsSalePromotionRow || position == tipInfoRow) {
                return 4;
            } else if (position == storeRow || (position >= otherStoresBeginRow && position <= otherStoresEndRow)) {
                return 5;
            } else if (position == storeSectionRow || position == serviceSectionRow || position == commentSection1Row) {
                return 6;
            } else if (position == serviceMedicineManualRow || position == serviceCallCenterRow || position == otherStoresRow) {
                return 7;
            } else if (position == goodsServiceModesRow) {
                return 8;
            } else if (position == goodsEmptyRow || position == commentEmptyRow) {
                return 9;
            } else if (position == commentLoadingRow) {
                return 10;
            } else if (position >= commentBeginRow && position <= commentEndRow) {
                return 11;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 12;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            if (i == serviceMedicineManualRow || i == serviceCallCenterRow || i == otherStoresRow) {
                return true;
            }
            return false;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            int viewType = getItemViewType(i);
            if (viewType == 0) {
                if (view == null) {
                    view = new EmptyCell(adapterContext);
                }
                EmptyCell cell = (EmptyCell) view;
                if (i == 0) {
                    cell.setHeight(overScrollHeight);
                } else if (i == bottomDividerRow) {
                    cell.setHeight(AndroidUtilities.dp(32));
                }
            } else if (viewType == 1) {
                if (view == null) {
                    view = new ShadowSectionCell(adapterContext);
                }
            } else if (viewType == 2) {
                if (view == null) {
                    view = new MedicineMainCell(adapterContext);
                }
                MedicineMainCell cell = (MedicineMainCell) view;
                CharSequence name = ShoppingHelper.createShoppingCartGoodsName(currMedicineGoodsItem.name, goodsFlag == GoodsFlag.MEDICARE);
                cell.setValue(name, currMedicineGoodsItem.detailDescription, isFavorites, false);
                cell.setFavoritesDelegate(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFavorites) {
                            tryRemoveMedicineFavorites();
                        } else {
                            tryAddMedicineFavorites();
                        }
                    }
                });
            } else if (viewType == 3) {
                if (view == null) {
                    view = new MedicinePriceCell(adapterContext);
                }
                MedicinePriceCell cell = (MedicinePriceCell) view;
                cell.setValue(currMedicineGoodsItem.marketPrice, currMedicineGoodsItem.userPrice, currMedicineGoodsItem.totalSaledCount, true);
            } else if (viewType == 4) {
                if (view == null) {
                    view = new MedicinePropertyCell(adapterContext);
                }
                MedicinePropertyCell cell = (MedicinePropertyCell) view;
                cell.setSmallStyle(true);
                cell.setValueTextColor();
                cell.setMultilineValue(false);
                cell.setBackgroundColor(ResourcesConfig.transparentColor);
                if (i == goodsCDRow) {
                    cell.setTextAndValue("产地", currMedicineGoodsItem.cd, true);
                } else if (i == goodsSpecRow) {
                    cell.setTextAndValue("规格", currMedicineGoodsItem.spec, true);
                } else if (i == goodsSalePromotionRow) {
                    cell.setMultilineValue(true);
                    cell.setValueTextColor(ResourcesConfig.shoppingAccent);
                    cell.setTextAndValue("促销", currMedicineGoodsItem.getSalesPromotionDesc(), true);
                } else if (i == goodsShippingCostRow) {
                    cell.setMultilineValue(true);
                    cell.setTextAndValue("运费", currMedicineGoodsItem.getShippingCostsDesc(), true);
                } else if (i == tipInfoRow) {
                    cell.setMultilineValue(true);
                    cell.setBackgroundColor(ResourcesConfig.tipColor);
                    cell.setTextAndValue("提示", currMedicineGoodsItem.getTipDescText(), true);
                }
            } else if (viewType == 5) {
                if (view == null) {
                    view = new MedicineStoreCell(adapterContext);
                }
                MedicineStoreCell cell = (MedicineStoreCell) view;
                if (i == storeRow) {
                    cell.setValue(currMedicineGoodsItem.shopIcon, currMedicineGoodsItem.shopName, currMedicineGoodsItem.shopAddress, currMedicineGoodsItem.storeCount, currMedicineGoodsItem.userPrice, true);
                    cell.setAddShoppingCartDelegate(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addGoodsToShoppingCart(currMedicineGoodsItem);
                        }
                    });
                } else {
                    int storeIndex = i - otherStoresBeginRow;
                    MedicineSaleStoreEntity storeEntity = saleStoreEntities.get(storeIndex);
                    cell.setValue(storeEntity.shopIcon, storeEntity.name, storeEntity.address, storeEntity.storeCount, currMedicineGoodsItem.userPrice, i != otherStoresEndRow);
                    //cell.setDistance(storeEntity.distance);
                    cell.setAddShoppingCartDelegate(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addGoodsToShoppingCart(currMedicineGoodsItem);
                        }
                    });
                }
            } else if (viewType == 6) {
                if (view == null) {
                    view = new HeaderCell(adapterContext);
                }
                HeaderCell cell = (HeaderCell) view;
                cell.setTextColor(ResourcesConfig.bodyText3);
                if (i == storeSectionRow) {
                    cell.setText("在售药店");
                } else if (i == serviceSectionRow) {
                    cell.setText("服务");
                } else if (i == commentSection1Row) {
                    cell.setText("买家评价");
                }
            } else if (viewType == 7) {
                if (view == null) {
                    view = new TextNavInfoCell(adapterContext);
                }
                TextNavInfoCell cell = (TextNavInfoCell) view;
                if (i == serviceMedicineManualRow) {
                    cell.setTextColor(ResourcesConfig.textPrimary);
                    cell.setValueTextColor(ResourcesConfig.bodyText3);
                    cell.setText("药品使用说明书", true, true);
                } else if (i == serviceCallCenterRow) {
                    cell.setTextColor(ResourcesConfig.textPrimary);
                    cell.setValueTextColor(ResourcesConfig.bodyText3);
                    cell.setTextAndValue("联系客服", "", true, true);
                } else if (i == otherStoresRow) {
                    cell.setTextColor(ResourcesConfig.bodyText3);
                    cell.setValueTextColor(ResourcesConfig.bodyText3);
                    String value = String.format("(%d家)", saleStoreEntities.size());
                    cell.setTextAndValue("更多附近在售药店", value, true, true);
                }
//                else if (i == commentMoreRow) {
//                    cell.setTextColor(ResourcesConfig.bodyText3);
//                    cell.setValueTextColor(ResourcesConfig.bodyText3);
//                    cell.setText("查看更多评价", true, true);
//                }
            } else if (viewType == 8) {
                if (view == null) {
                    view = new MedicineServiceModesCell(adapterContext);
                }
                MedicineServiceModesCell cell = (MedicineServiceModesCell) view;
                cell.setValue(currMedicineGoodsServiceModes, true);
            } else if (viewType == 9) {
                if (view == null) {
                    view = new TextInfoCell(adapterContext);
                }
                TextInfoCell cell = (TextInfoCell) view;
                if (i == goodsEmptyRow) {
                    cell.setText("暂未销售此药品");
                } else if (i == commentEmptyRow) {
                    cell.setText("暂无评论");
                }
            } else if (viewType == 10) {
                if (view == null) {
                    view = new LoadingCell(adapterContext);
                }
            } else if (viewType == 11) {
                if (view == null) {
                    view = new GoodsCommentCell(adapterContext);
                }
                GoodsCommentCell cell = (GoodsCommentCell) view;
                GoodsCommentEntity entity = goodsCommentEntities.get(i - commentBeginRow);
                cell.setValue(entity.qualityLevel, entity.allCount, entity.assessDate, entity.advice, entity.getMemberName(), true);
            }
            return view;
        }
    }


    private void loadMedicineSaleStores() {
        saleStoreEntities.clear();
        if (!checkNearStore) {
            return;
        }
        AMapLocation location = LocationHelper.getLastLocation(GoodsDetailActivity.this);
        Map<String, Object> args = new HashMap<>();
        args.put("MERCHANDISEID", goodsId);
        args.put("LONGITUDE", location.getLongitude());
        args.put("LATITUDE", location.getLatitude());
        args.put("PAGE", 0);
        args.put("COUNT", 5);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "SaleInShop", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(GoodsDetailActivity.this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                saleStoreEntities.clear();
                updateDate();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                boolean hasError = false;
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        MedicineSaleStoreEntity storeTemp;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            storeTemp = new MedicineSaleStoreEntity(jsonArray.getJSONObject(i));
                            //商品已返回在售药店
                            if (!TextUtils.equals(storeTemp.id, currMedicineGoodsItem.shopId)) {
                                saleStoreEntities.add(storeTemp);
                            }
                        }
                        updateDate();
                    } catch (JSONException e) {
                        hasError = true;
                    }
                } else {
                    hasError = true;
                }
                if (hasError) {
                    saleStoreEntities.clear();
                    updateDate();
                }
            }
        });
    }

    private void loadingGoodsComment(boolean loading) {
        loadingGoodsComment = loading;
        updateDate();
    }

    private void loadCommentData() {
        goodsCommentEntities.clear();
        loadingGoodsComment(true);

        Map<String, Object> args = new HashMap<>();
        args.put("MERCHANDISEID", goodsId);
        args.put("PAGE", 0);
        args.put("COUNT", 5);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetAssessment", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                loadingGoodsComment(false);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                loadingGoodsComment(false);
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            goodsCommentEntities.add(new GoodsCommentEntity(jsonArray.getJSONObject(i)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateDate();
            }
        });
    }

    public void AddToHistory(MedicineGoodsItem item) {
        HistoryEntity historyEntity = HistoryEntity.toEntity(item, goodsFlag);
        HistoryDao historyDao = DBInterface.instance().openWritableDb().getHistoryDao();
        DeleteQuery<HistoryEntity> historyEntityDeleteQuery = historyDao.queryBuilder().where(HistoryDao.Properties.Guid.eq(item.guid)).buildDelete();
        historyEntityDeleteQuery.executeDeleteWithoutDetachingEntities();
        historyDao.insertOrReplace(historyEntity);
    }


}
