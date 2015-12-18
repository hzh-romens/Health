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
public class FavoritesAdapter extends RecyclerView.Adapter {
    private Context context;
    private FavoritesCellDelegate favoritesCellDelegate;
    private Drawable emptyIcon;
    private final List<FavoritesEntity> favoritesEntities = new ArrayList<>();

    static class Holder extends RecyclerView.ViewHolder {

        public Holder(View view) {
            super(view);
        }
    }

    public interface FavoritesCellDelegate {
        void onCellClick(int position);

        void onAddShoppingCart(FavoritesEntity entity);

        void onRemoveFavorites(FavoritesEntity entity);
    }

    public FavoritesAdapter(Context context, FavoritesCellDelegate delegate) {
        this.context = context;
        favoritesCellDelegate = delegate;
        emptyIcon = context.getResources().getDrawable(R.drawable.no_img_upload);
    }

    public void bindData(List<FavoritesEntity> data) {
        favoritesEntities.clear();
        if (data != null && data.size() > 0) {
            favoritesEntities.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<FavoritesEntity> getFavoritesEntities() {
        return favoritesEntities;
    }

    public FavoritesEntity getItem(int position) {
        return favoritesEntities.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            MedicineListCell cell = new MedicineListCell(viewGroup.getContext());
            cell.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        } else if (i == 1) {
            FavoritesTipCell cell = new FavoritesTipCell(viewGroup.getContext());
            cell.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder.itemView instanceof MedicineListCell) {
            MedicineListCell cell = (MedicineListCell) viewHolder.itemView;
            cell.setCellDelegate(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoritesCellDelegate != null) {
                        favoritesCellDelegate.onCellClick(i - 1);
                    }
                }
            });
            FavoritesEntity entity = getItem(i - 1);
            SpannableStringBuilder priceStr = new SpannableStringBuilder();
            priceStr.append(ShoppingHelper.formatPrice(new BigDecimal(entity.getPrice())));
            priceStr.append(String.format(" (%s)", entity.getMedicineSpec()));
            CharSequence memberPriceStr = ShoppingHelper.createMemberPriceInfo(new BigDecimal(entity.getMemberPrice()));
            cell.setValue(true, true, entity.getPicSmall(), emptyIcon, entity.getMedicineName(), "", priceStr, memberPriceStr, true, true);
            cell.enableAddShoppingCartBtn(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoritesCellDelegate != null) {
                        favoritesCellDelegate.onAddShoppingCart(getItem(i - 1));
                    }
                }
            });
            cell.setFavoritesDelegate(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoritesCellDelegate != null) {
                        favoritesCellDelegate.onRemoveFavorites(getItem(i - 1));
                    }
                }
            });
        } else if (viewHolder.itemView instanceof FavoritesTipCell) {
            FavoritesTipCell cell = (FavoritesTipCell) viewHolder.itemView;
            cell.setValue("小提示:点击 [favorites] 可以取消收藏!", "[favorites]", R.drawable.ic_favorite_white_24dp, true);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int count = favoritesEntities == null ? 0 : favoritesEntities.size();
        return count > 0 ? (count + 1) : count;
    }
}
