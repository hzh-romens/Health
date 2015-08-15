package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.model.ADFunctionEntity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.components.AttachView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/8/14.
 */
public class ADFunctionControl extends ADBaseControl {
    private final List<ADFunctionEntity> adFunctionEntities = new ArrayList<>();

    public ADFunctionControl bindModel(List<ADFunctionEntity> entities) {
        adFunctionEntities.clear();
        if (entities != null && entities.size() > 0) {
            adFunctionEntities.addAll(entities);
        }
        return this;
    }

    public static ADHolder createViewHolder(Context context) {
        AttachView attachView = new AttachView(context);
        attachView.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        return new ADHolder(attachView);
    }

    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        AttachView cell = (AttachView) holder.itemView;
        int size = adFunctionEntities.size();
        CharSequence[] items = new CharSequence[size];
        int itemIcons[] = new int[size];
        for (int i = 0; i < size; i++) {
            items[i] = adFunctionEntities.get(i).name;
            itemIcons[i] = adFunctionEntities.get(i).iconResId;
        }
        cell.bindData(itemIcons, items);
    }

    @Override
    public int getType() {
        return ControlType.TYPE_AD_FUNCTION;
    }
}
