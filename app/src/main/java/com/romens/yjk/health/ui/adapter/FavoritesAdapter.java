package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.CheckBox;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.FavoritesEntity;
import com.romens.yjk.health.helper.ShoppingHelper;
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
    private final Map<String, Integer> favoritesSelects = new HashMap<>();

    static class Holder extends RecyclerView.ViewHolder {

        public Holder(View view) {
            super(view);
        }
    }

    public interface FavoritesCellDelegate {
        void onCellClick(int position);

        void onAddShoppingCart(FavoritesEntity entity);

        void onSelectedChanged();
    }

    public FavoritesAdapter(Context context, FavoritesCellDelegate delegate) {
        this.context = context;
        favoritesCellDelegate = delegate;
        emptyIcon = context.getResources().getDrawable(R.drawable.no_img_upload);
    }

    public void bindData(List<FavoritesEntity> data, boolean clear) {
        favoritesEntities.clear();
        if (data != null && data.size() > 0) {
            favoritesEntities.addAll(data);
        }
        if (clear) {
            favoritesSelects.clear();
        } else {
            int size = favoritesEntities.size();
            String idTemp;
            for (int i = 0; i < size; i++) {
                idTemp = favoritesEntities.get(i).getId();
                if (favoritesSelects.containsKey(idTemp)) {
                    favoritesSelects.put(idTemp, i);
                }
            }
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MedicineListCell cell = new MedicineListCell(viewGroup.getContext());
        cell.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        return new Holder(cell);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder.itemView instanceof MedicineListCell) {
            MedicineListCell cell = (MedicineListCell) viewHolder.itemView;
            cell.setCellDelegate(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoritesCellDelegate != null) {
                        favoritesCellDelegate.onCellClick(i);
                    }
                }
            });
            FavoritesEntity entity = getItem(i);
            boolean checked = isSelect(entity.getId());
            SpannableStringBuilder priceStr = new SpannableStringBuilder();
            priceStr.append(ShoppingHelper.formatPrice(new BigDecimal(entity.getPrice())));
            priceStr.append(String.format(" (%s)", entity.getMedicineSpec()));
            CharSequence memberPriceStr = ShoppingHelper.createMemberPriceInfo(new BigDecimal(entity.getMemberPrice()));
            cell.setValue(true, checked, entity.getPicSmall(), emptyIcon, entity.getMedicineName(), "", priceStr, memberPriceStr, true, true);
            cell.enableAddShoppingCartBtn(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoritesCellDelegate != null) {
                        favoritesCellDelegate.onAddShoppingCart(getItem(i));
                    }
                }
            });
            cell.setCheckBoxDelegate(new CheckBox.OnCheckListener() {
                @Override
                public void onCheck(CheckBox view, boolean check) {
                    switchSelect(i, check);
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return favoritesEntities == null ? 0 : favoritesEntities.size();
    }

    public boolean isSelect(String id) {
        return favoritesSelects.containsKey(id);
    }

    public void switchSelectAll(boolean selected) {
        favoritesSelects.clear();
        if (selected) {
            final int size = favoritesEntities.size();
            for (int i = 0; i < size; i++) {
                favoritesSelects.put(getItem(i).getId(), i);
            }
        }
        if (favoritesCellDelegate != null) {
            favoritesCellDelegate.onSelectedChanged();
        }
        notifyDataSetChanged();
    }

    public boolean isAllSelected() {
        int selectedCount = getSelectedItemCount();
        int allCount = getItemCount();
        return selectedCount == allCount;
    }

    public int getSelectedItemCount() {
        int selectedCount = favoritesSelects.size();
        return selectedCount;
    }

    public ArrayList<String> getSelectedItems() {
        ArrayList<String> items = new ArrayList<>();
        for (Integer value :
                favoritesSelects.values()) {
            items.add(getItem(value).getMerchandiseId());
        }
        return items;
    }

    private void switchSelect(int position, boolean checked) {
        FavoritesEntity entity = getItem(position);
        String id = entity.getId();
        if (!checked) {
            if (favoritesSelects.containsKey(id)) {
                favoritesSelects.remove(id);
            }
        } else {
            favoritesSelects.put(id, position);
        }
        if (favoritesCellDelegate != null) {
            favoritesCellDelegate.onSelectedChanged();
        }
        //notifyDataSetChanged();
    }
}
