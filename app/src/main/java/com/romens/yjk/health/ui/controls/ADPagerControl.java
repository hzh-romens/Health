package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.ADPagerEntity;
import com.romens.yjk.health.ui.MedicinalDetailActivity;
import com.romens.yjk.health.ui.activity.ADWebActivity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADPagerCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/8/14.
 */
public class ADPagerControl extends ADBaseControl {
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
        //adPagerCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        adPagerCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        return new ADHolder(adPagerCell);
    }

    @Override
    public void bindViewHolder(final Context context, ADHolder holder) {
        ADPagerCell cell = (ADPagerCell) holder.itemView;
        ADPagerAdapter adPagerAdapter = new ADPagerAdapter(context);
        adPagerAdapter.bindData(adPagerEntities);
        cell.setAdapter(adPagerAdapter);
        cell.start();
    }

    @Override
    public int getType() {
        return ControlType.TYPE_AD_PAGER;
    }


    static class ADPagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<ADPagerEntity> adPagerEntities = new ArrayList<>();
        private final List<CloudImageView> viewList = new ArrayList<>();
        private ADPageCellDelegate cellDelegate;

        public ADPagerAdapter(Context context) {
            mContext = context;
        }

        public void setADPageCellDelegate(ADPageCellDelegate delegate) {
            cellDelegate = delegate;
        }

        public void bindData(List<ADPagerEntity> entities) {
            adPagerEntities.clear();
            if (entities != null && entities.size() > 0) {
                adPagerEntities = entities;
            }
            final int size = adPagerEntities.size();
            viewList.clear();
            for (int i = 0; i < size; i++) {
                viewList.add(CloudImageView.create(mContext));
            }
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView(viewList.get(position));
        }


        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            final ADPagerEntity entity = adPagerEntities.get(position);
            CloudImageView pager = viewList.get(position);
            pager.setClickable(true);
            pager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handlePageCellClick(container.getContext(), entity);
                }
            });
            container.addView(pager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
            pager.setImagePath(entity.value);
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

        private void handlePageCellClick(Context context, ADPagerEntity entity) {
            int type = entity.getType();
            String action = entity.getAction();

            if (entity != null) {
//                Intent intent = new Intent(context, MedicinalDetailActivity.class);
//                intent.putExtra("guid", entity.id);
//                context.startActivity(intent);

                UIOpenHelper.openMedicineActivity(context, entity.id);
            }
//            if (type == 0) {
//                if (!TextUtils.isEmpty(action)) {
//                    String url = createADWebUrl(action, FacadeToken.getInstance().getAuthToken(), entity.id);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(ADWebActivity.ARGUMENTS_KEY_TITLE, entity.name);
//                    bundle.putString(ADWebActivity.ARGUMENTS_KEY_TARGET_URL, url);
//                    onActionForADWeb(context, bundle);
//                }
//            }
        }
    }

    public interface ADPageCellDelegate {
        void onPageCellClick(int position);
    }


}
