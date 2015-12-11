package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.model.ADImageEntity;
import com.romens.yjk.health.model.ADImageListEntity;
import com.romens.yjk.health.ui.MedicinalDetailActivity;
import com.romens.yjk.health.ui.activity.ADWebActivity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADImagesCell;

/**
 * Created by siery on 15/8/14.
 */
public class ADImagesControl extends ADBaseControl {
    private ADImageListEntity adImageListEntity;


    public ADImagesControl bindModel(ADImageListEntity entity) {
        adImageListEntity = entity;
        return this;
    }

    public static ADHolder createViewHolder(Context context) {
        ADImagesCell cell = new ADImagesCell(context);
        cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        return new ADHolder(cell);
    }

    @Override
    public void bindViewHolder(final Context context, ADHolder holder) {
        ADImagesCell cell = (ADImagesCell) holder.itemView;
        cell.setADImageCellDelegate(new ADImagesCell.ADImageCellDelegate() {
            @Override
            public void onImageClick(ADImageEntity entity) {
                handleImageClick(context, entity);
            }
        });
        cell.setValue(adImageListEntity);
    }

    @Override
    public int getType() {
        return ControlType.TYPE_AD_IMAGE;
    }

    private void handleImageClick(Context context, ADImageEntity entity) {
        int type = entity.getType();
        String action = entity.getAction();
        if (entity != null) {
            Intent intent = new Intent(context, MedicinalDetailActivity.class);
            intent.putExtra("guid", entity.id);
            context.startActivity(intent);
        }

//        if (type == 0) {
//            if (!TextUtils.isEmpty(action)) {
//                String url = createADWebUrl(action, FacadeToken.getInstance().getAuthToken(), entity.id);
//                Bundle bundle = new Bundle();
//                bundle.putString(ADWebActivity.ARGUMENTS_KEY_TARGET_URL, url);
//                onActionForADWeb(context, bundle);
//            }
//        }
    }
}
