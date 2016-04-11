package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.CheckBox;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.db.entity.FavoritesEntity;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.ui.cells.FavoritesTipCell;
import com.romens.yjk.health.ui.cells.MedicineListCell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by siery on 15/12/17.
 */
public class FavoritesAdapter extends FavoritesBaseAdapter {

    public FavoritesAdapter(Context context, FavoritesCellDelegate delegate) {
        super(context, delegate);
    }

    @Override
    public FavoritesEntity getItem(int position) {
        return favoritesEntities.get(position);
    }

    @Override
    protected String getEmptyText() {
        return "暂无收藏";
    }

    @Override
    public int getItemViewType(int position) {
        if(favoritesEntities.isEmpty()){
            return 2;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        int count = favoritesEntities == null ? 0 : favoritesEntities.size();
        return count > 0 ? count : 1;
    }
}
