package com.romens.yjk.health.ui.controls;

import android.content.Context;

import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADMedicinalDetailCell;

/**
 * Created by AUSU on 2015/10/14.
 */
public class ADMedicinalDetailControl extends ADBaseControl {
    private String inventory, medicinalDetail, medicinalName, medicinalPrice, storeAddress, storeName;

    public ADMedicinalDetailControl bindModle(String owner_inventory, String owner_medicinalDetail, String owner_medicinalName
            , String owner_medicinalPrice, String owner_storeAddress, String owner_storeName) {
        inventory = owner_inventory;
        medicinalDetail = owner_medicinalDetail;
        medicinalName = owner_medicinalName;
        medicinalPrice = owner_medicinalPrice;
        storeAddress = owner_storeAddress;
        storeName = owner_storeName;
        return this;
    }

    @Override
    public void bindViewHolder(Context context, ADHolder holder) {
        ADMedicinalDetailCell adMedicinalDetailCell = (ADMedicinalDetailCell) holder.itemView;
        adMedicinalDetailCell.setValue(inventory, medicinalDetail, medicinalName, medicinalPrice, storeAddress, storeName);
    }

    public static ADHolder createViewHolder(Context context) {
        ADMedicinalDetailCell cell = new ADMedicinalDetailCell(context);
        return new ADHolder(cell);
    }

    @Override
    public int getType() {
        return 6;
    }
}
