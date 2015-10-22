package com.romens.yjk.health.ui.controls;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADStoreCell;
import com.romens.yjk.health.ui.components.logger.Log;

/**
 * Created by AUSU on 2015/10/14.
 */
public class ADStoreControls extends ADBaseControl{
    private String mCount,mAddress,mPrice,mName,mDistance,mGuid;
    public ADStoreControls bindModel(String count,String address,String price,String name,String distance,String guid){
        mCount=count;
        mAddress=address;
        mPrice=price;
        mName=name;
        mDistance=distance;
        mGuid=guid;
        return  this;
    }
    @Override
    public void bindViewHolder(final Context context, ADHolder holder) {
        ADStoreCell adStoreCell= (ADStoreCell) holder.itemView;
        adStoreCell.setValue(mCount,mAddress,mPrice,mName,mDistance,"");
        adStoreCell.ItemClickListener(new ADStoreCell.ItemClickBack() {
            @Override
            public void ToStoreListener() {
                //跳转到另一个药品的详情页面
                Toast.makeText(context,"跳转到详情页面",Toast.LENGTH_SHORT).show();
            }
        });
        adStoreCell.ItemClickListener2(new ADStoreCell.ItemClickBack2() {
            @Override
            public void AddToShopCarListener() {
                //加入购物车
                Toast.makeText(context,"加入购物车",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static ADHolder createViewHolder(Context context) {
        ADStoreCell adStoreCell=new ADStoreCell(context);
        return new ADHolder(adStoreCell);
    }

    @Override
    public int getType() {
        return 7;
    }
}
