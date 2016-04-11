package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.AndroidUtilities;
import com.romens.android.log.FileLog;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.TextInfoCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.FavoritesDao;
import com.romens.yjk.health.db.entity.FavoritesEntity;
import com.romens.yjk.health.helper.ShoppingHelper;
import com.romens.yjk.health.ui.cells.FavoritesTipCell;
import com.romens.yjk.health.ui.cells.MedicineListCell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by siery on 15/12/20.
 */
public abstract class FavoritesBaseAdapter extends RecyclerView.Adapter {
    private Context context;
    private FavoritesCellDelegate favoritesCellDelegate;
    private Drawable emptyIcon;
    protected final List<FavoritesEntity> favoritesEntities = new ArrayList<>();

    protected boolean searching;
    private Timer searchTimer;

    protected Context mContext;
    protected boolean isDestroy = false;

    private FavoritesDao favoritesDao = DBInterface.instance().openReadableDb().getFavoritesDao();

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

    public void destroy() {
        isDestroy = true;
    }

    public FavoritesBaseAdapter(Context context, FavoritesCellDelegate delegate) {
        this.context = context;
        favoritesCellDelegate = delegate;
        emptyIcon = context.getResources().getDrawable(R.drawable.no_img_upload);
    }

    public void searchDelayed(final String query) {
        if (query == null || query.length() == 0) {
            favoritesEntities.clear();
            notifyDataSetChanged();
        } else {
            try {
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            searchTimer = new Timer();
            searchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        searchTimer.cancel();
                        searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            searchLocalDBWithQuery(query);
                        }
                    });
                }
            }, 200, 500);
        }
    }

    private void searchLocalDBWithQuery(String query) {
        if (isDestroy) {
            return;
        }
        if (searching) {
            searching = false;
        }
        searching = true;
        List<FavoritesEntity> searchResult = favoritesDao.queryBuilder().where(FavoritesDao.Properties.MedicineName.like(String.format("%%%s%%",query))).list();
        searching = false;
        bindData(searchResult);
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

    public abstract FavoritesEntity getItem(int position);

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
        } else if (i == 2) {
            TextInfoCell cell = new TextInfoCell(viewGroup.getContext());
            cell.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        int viewType = getItemViewType(i);
        if (viewType == 0) {
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
            SpannableStringBuilder priceStr = new SpannableStringBuilder();
            priceStr.append(ShoppingHelper.formatPrice(new BigDecimal(entity.getPrice())));
            priceStr.append(String.format(" (%s)", entity.getMedicineSpec()));
            CharSequence memberPriceStr = ShoppingHelper.createMemberPriceInfo(new BigDecimal(entity.getMemberPrice()));
            cell.setValue(true, true, entity.getPicSmall(), emptyIcon, entity.getMedicineName(), "", priceStr, memberPriceStr, true, true);
            cell.enableAddShoppingCartBtn(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoritesCellDelegate != null) {
                        favoritesCellDelegate.onAddShoppingCart(getItem(i));
                    }
                }
            });
            cell.setFavoritesDelegate(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoritesCellDelegate != null) {
                        favoritesCellDelegate.onRemoveFavorites(getItem(i));
                    }
                }
            });
        }else if (viewType == 2) {
            TextInfoCell cell = (TextInfoCell) viewHolder.itemView;
            String emptyText = getEmptyText();
            cell.setText(emptyText);
        }
    }

    protected abstract String getEmptyText();

    @Override
    public long getItemId(int position) {
        return position;
    }

}
