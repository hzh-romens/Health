package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.model.ADPagerEntity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADPagerCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/8/14.
 */
public class ADPagerControl extends ADBaseControl {
    public static final int TYPE=0;
    private final List<ADPagerEntity> adPagerEntities = new ArrayList<>();

    public ADPagerControl bindModel(List<ADPagerEntity> entities) {
        adPagerEntities.clear();
        if (entities != null && entities.size() > 0) {
            adPagerEntities.addAll(entities);
        }
        return this;
    }

    public static ADHolder createViewHolder(Context context) {
        ADPagerCell adPagerCell = new ADPagerCell(context);
        adPagerCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(80)));
        return new ADHolder(adPagerCell);
    }

    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        ADPagerCell cell = (ADPagerCell) holder.itemView;
        ADPagerAdapter adPagerAdapter = new ADPagerAdapter(context);
        adPagerAdapter.bindData(adPagerEntities);
        cell.setAdapter(adPagerAdapter);
        cell.start();
    }

    @Override
    public int getType() {
        return TYPE;
    }


    static class ADPagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<ADPagerEntity> adPagerEntities = new ArrayList<>();
        private final List<BackupImageView> viewList = new ArrayList<>();

        public ADPagerAdapter(Context context) {
            mContext = context;
        }

        public void bindData(List<ADPagerEntity> entities) {
            adPagerEntities.clear();
            if (entities != null && entities.size() > 0) {
                adPagerEntities = entities;
            }
            final int size = adPagerEntities.size();
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
            ADPagerEntity entity = adPagerEntities.get(position);
            BackupImageView pager = viewList.get(position);
            container.addView(pager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
            pager.setImage(entity.value, null, null);
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
