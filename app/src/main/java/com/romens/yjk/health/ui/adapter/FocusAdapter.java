package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.ADImageCell;
import com.romens.yjk.health.ui.cells.ADPagerCell;
import com.romens.yjk.health.ui.components.AttachView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/8/13.
 */
public class FocusAdapter extends RecyclerView.Adapter<FocusAdapter.FocusHolder> {
    private static final int TYPE_ATTACH = 0;
    private static final int TYPE_AD_IMAGE = 1;
    private static final int TYPE_AD_TOP = 2;
    private Context context;

    public FocusAdapter(Context _context) {
        this.context = _context;
    }

    @Override
    public FocusHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_AD_TOP) {
            ADPagerCell adPagerCell = new ADPagerCell(context);
            adPagerCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,AndroidUtilities.dp(80)));
            return new FocusHolder(adPagerCell);
        } else if (viewType == TYPE_ATTACH) {
            AttachView attachView = new AttachView(context);
            attachView.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new FocusHolder(attachView);
        } else if (viewType == TYPE_AD_IMAGE) {
            ADImageCell adImageCell = new ADImageCell(context);
            adImageCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(100)));
            return new FocusHolder(adImageCell);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(FocusHolder holder, int position) {
        final int viewType = getItemViewType(position);
        if (viewType == TYPE_ATTACH) {
            AttachView cell = (AttachView) holder.itemView;
            CharSequence[] items = new CharSequence[]{
                    "附近药店",
                    "附近药店",
                    "附近药店",
                    "附近药店",
                    "附近药店",
                    "附近药店"
            };
            int itemIcons[] = new int[]{
                    R.drawable.attach_location_states,
                    R.drawable.attach_location_states,
                    R.drawable.attach_location_states,
                    R.drawable.attach_location_states,
                    R.drawable.attach_location_states,
                    R.drawable.attach_location_states
            };
            cell.bindData(itemIcons, items);
        } else if (viewType == TYPE_AD_IMAGE) {
            ADImageCell cell = (ADImageCell) holder.itemView;
            cell.setImage("http://img2.imgtn.bdimg.com/it/u=1892187015,3385961007&fm=21&gp=0.jpg", null, null);
        } else if (viewType == TYPE_AD_TOP) {
            ADPagerCell cell = (ADPagerCell) holder.itemView;
            ADPagerAdapter adPagerAdapter=new ADPagerAdapter(context);
            adPagerAdapter.addAllViews(6);
            cell.setViewPagerAdapter(adPagerAdapter);
            cell.start();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_AD_TOP;
        } else if (position == 1) {
            return TYPE_ATTACH;
        } else if (position == 2) {
            return TYPE_AD_IMAGE;
        }
        return -1;
    }

    public static class FocusHolder extends RecyclerView.ViewHolder {

        public FocusHolder(View itemView) {
            super(itemView);
        }
    }

    static class ADPagerAdapter extends PagerAdapter {

        private Context mContext;
        private final List<BackupImageView> viewList=new ArrayList<>();

        public ADPagerAdapter(Context context) {
            mContext = context;
        }

        public void addAllViews(int size){
            viewList.clear();
            for (int i = 0; i < size; i++) {
                viewList.add(new BackupImageView(mContext));
            }
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView(viewList.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BackupImageView pager = viewList.get(position);
            container.addView(pager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
            pager.setImage("http://img2.imgtn.bdimg.com/it/u=1892187015,3385961007&fm=21&gp=0.jpg", null, null);
            return pager;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
