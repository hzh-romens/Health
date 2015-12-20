package com.romens.yjk.health.ui.adapter;

import android.content.Context;

import com.romens.yjk.health.db.entity.FavoritesEntity;

/**
 * Created by siery on 15/12/20.
 */
public class FavoritesSearchAdapter extends FavoritesBaseAdapter {

    public FavoritesSearchAdapter(Context context, FavoritesCellDelegate delegate) {
        super(context, delegate);
    }

    @Override
    public FavoritesEntity getItem(int position) {
        return favoritesEntities.get(position);
    }

    @Override
    protected String getEmptyText() {
        return "无搜索结果";
    }

    @Override
    public int getItemViewType(int position) {
        if (favoritesEntities.isEmpty()) {
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
