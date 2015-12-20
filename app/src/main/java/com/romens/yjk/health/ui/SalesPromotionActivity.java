package com.romens.yjk.health.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ADProductEntity;
import com.romens.yjk.health.model.ADSalesListEntity;
import com.romens.yjk.health.ui.cells.ProductCell;
import com.romens.yjk.health.ui.cells.SalesPromotionADCell;
import com.romens.yjk.health.ui.components.CollectionView;
import com.romens.yjk.health.ui.components.CollectionViewCallbacks;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by siery on 15/8/17.
 */
public class SalesPromotionActivity extends BaseActivity implements CollectionViewCallbacks {
    private static final int ANIM_DURATION = 250;
    private static final int AD_GROUP_ID = 0;

    private CollectionView collectionView;
    private ADSalesListEntity adSalesListEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);
        collectionView = new CollectionView(this);
        collectionView.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        collectionView.setInternalPadding(AndroidUtilities.dp(8));
        collectionView.setClipToPadding(false);
        collectionView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        content.addView(collectionView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });

        collectionView.setCollectionAdapter(this);
        ADSalesListEntity entity = createDemoData();
        CollectionView.Inventory inventory = prepareInventory(entity);
        collectionView.updateInventory(inventory, true);
        animateReload();
        setADName(entity.name);
        setADPrimaryColor(0xffe5e5e5);
    }

    public void animateReload() {
        collectionView.setAlpha(0);
        collectionView.animate().alpha(1).setDuration(ANIM_DURATION).setInterpolator(new DecelerateInterpolator());
    }

    /**
     * 配置促销猪蹄色
     *
     * @param color
     */
    private void setADPrimaryColor(int color) {
        collectionView.setBackgroundColor(color);
        ActionBar actionBar = getMyActionBar();
        actionBar.setBackgroundColor(color);
        actionBar.setItemsBackground(R.drawable.bar_selector);
    }

    private void setADName(String name) {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle(name);
    }

    private CollectionView.Inventory prepareInventory(ADSalesListEntity _adSalesListEntity) {
        adSalesListEntity = _adSalesListEntity;
        CollectionView.InventoryGroup adGroup = null;
        ArrayList<CollectionView.InventoryGroup> productGroups = new ArrayList<>();
        HashMap<String, CollectionView.InventoryGroup> productGroupsByName = new HashMap<>();

        int nextGroupId = AD_GROUP_ID + 1000;
        int dataIndex = -1;
        final int displayCols = 2;

        if (!TextUtils.isEmpty(adSalesListEntity.iconUrl)) {
            ++dataIndex;
            adGroup = new CollectionView.InventoryGroup(AD_GROUP_ID)
                    .setDisplayCols(1)
                    .setShowHeader(false).setHeaderLabel("");
            adGroup.addItemWithCustomDataIndex(dataIndex);
        }

        int size = adSalesListEntity.productList.size();
        CollectionView.InventoryGroup group;

        ArrayList<CollectionView.InventoryGroup> list = productGroups;
        HashMap<String, CollectionView.InventoryGroup> map = productGroupsByName;
        group = new CollectionView.InventoryGroup(nextGroupId++)
                .setDisplayCols(displayCols)
                .setShowHeader(false)
                .setHeaderLabel("");
        map.put("ProductList", group);
        list.add(group);
        for (int i = 0; i < size; i++) {
            ++dataIndex;
            // Create group, if it doesn't exist yet
            group.addItemWithCustomDataIndex(dataIndex);
        }

        // prepare the final groups list
        ArrayList<CollectionView.InventoryGroup> groups = new ArrayList<CollectionView.InventoryGroup>();
        if (adGroup != null) {
            groups.add(adGroup);
        }
        groups.addAll(productGroups);
        // finally, assemble the inventory and we're done
        CollectionView.Inventory inventory = new CollectionView.Inventory();
        for (CollectionView.InventoryGroup g : groups) {
            inventory.addGroup(g);
        }
        return inventory;
    }

    private void getADPrimaryColor(Bitmap bitmap) {
        Palette.Builder builder = Palette.from(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant =
                        palette.getVibrantSwatch();
                setADPrimaryColor(vibrant.getRgb());
            }
        });
    }

    @Override
    public View newCollectionHeaderView(Context context, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindCollectionHeaderView(Context context, View view, int groupId, String headerLabel) {

    }

    @Override
    public View newCollectionItemView(Context context, int groupId, ViewGroup parent) {
        if (groupId == AD_GROUP_ID) {
            SalesPromotionADCell cell = new SalesPromotionADCell(context);
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            cell.setImageLoadCallback(new SalesPromotionADCell.ImageLoadCallback() {
                @Override
                public void onFinished(Bitmap bitmap) {
                    getADPrimaryColor(bitmap);
                }
            });
            return cell;
        }
        ProductCell cell = new ProductCell(context);
        cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        return cell;
    }

    @Override
    public void bindCollectionItemView(Context context, View view, int groupId, int indexInGroup, int dataIndex, Object tag) {
        if (groupId == AD_GROUP_ID) {
            SalesPromotionADCell cell = (SalesPromotionADCell) view;
            cell.setImage(adSalesListEntity.iconUrl, null, null);
        } else {
            ADProductEntity entity = adSalesListEntity.productList.get(indexInGroup);
            ProductCell cell = (ProductCell) view;
            cell.setProductCellDelegate(new ProductCell.ProductCellDelegate() {
                @Override
                public void onCellClick(Bundle arguments) {

                }
            });
            cell.setValue(entity.icon, entity.name, entity.oldPrice, entity.price);
            Bundle arguments = new Bundle();
            arguments.putString("ID", entity.id);
            cell.setArguments(arguments);
        }
    }

    private ADSalesListEntity createDemoData() {
        ADSalesListEntity entity = new ADSalesListEntity("八月十五促销", "http://img1.imgtn.bdimg.com/it/u=3238452814,3804376663&fm=21&gp=0.jpg");
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊胃药胶囊胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));
        entity.addProduct(new ADProductEntity("0", "http://img.yy960.com/2013/03/20130326132534.JPG", "胃药胶囊", "", "$21.5"));

        return entity;
    }
}
