package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;

/**
 * Created by AUSU on 2015/10/14.
 */
public class ADMedicinalDetailCell extends FrameLayout {
    private TextView tv_owner_medicinalName, tv_owner_storeName, tv_owner_medicinalPrice,
            tv_owner_storeAddress, tv_owner_inventory, tv_owner_medicinalDetail;
    private BackupImageView iv_owner_store;

    public ADMedicinalDetailCell(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_medicinaldetail_info, null);
        tv_owner_inventory= (TextView) view.findViewById(R.id.tv_owner_inventory);
        tv_owner_medicinalDetail= (TextView) view.findViewById(R.id.tv_owner_medicinalDetail);
        tv_owner_medicinalName= (TextView) view.findViewById(R.id.tv_owner_medicinalName);
        tv_owner_medicinalPrice= (TextView) view.findViewById(R.id.tv_owner_medicinalPrice);
        tv_owner_storeAddress= (TextView) view.findViewById(R.id.tv_owner_storeAddress);
        tv_owner_storeName= (TextView) view.findViewById(R.id.tv_owner_storeName);
        iv_owner_store= (BackupImageView) view.findViewById(R.id.iv_owner_store);
        addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }
    public void setValue(String owner_inventory,String owner_medicinalDetail,String owner_medicinalName
            ,String owner_medicinalPrice,String owner_storeAddress,String owner_storeName,String url){
        tv_owner_storeAddress.setText(owner_storeAddress);
        tv_owner_storeName.setText(owner_storeName);
        tv_owner_medicinalName.setText(owner_medicinalName);
        tv_owner_inventory.setText("库存:"+owner_inventory);
        tv_owner_medicinalPrice.setText("¥"+owner_medicinalPrice);
        tv_owner_medicinalDetail.setText(owner_medicinalDetail);
        if("".equals(url)||url==null){

        }else{
            iv_owner_store.setImage(url,null,null);
        }
    }
}
