package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.DrugStoreEntity;
import com.romens.yjk.health.ui.cells.DrugStoreCell;
import com.romens.yjk.health.ui.components.logger.Log;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by AUSU on 2015/9/16.
 * 在线药店
 */
public class DrugStoreAdapter extends RecyclerView.Adapter{
    private List<DrugStoreEntity> mDdata;
    private Context mContext;
    public DrugStoreAdapter(List<DrugStoreEntity> data,Context context){
        this.mDdata=data;
        this.mContext=context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DrugStoreCell drugStoreCell=new DrugStoreCell(mContext);
        drugStoreCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(90)));
        return new DrugStoreHolder(drugStoreCell);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DrugStoreEntity entity = mDdata.get(position);
        DrugStoreHolder drugStoreHolder= (DrugStoreHolder) holder;
        DrugStoreCell drugStoreCell = (DrugStoreCell) drugStoreHolder.itemView;
        drugStoreCell.setValue(entity.getName(), entity.getInfo(), entity.getUrl());
    }


    public class DrugStoreHolder extends RecyclerView.ViewHolder{
       public DrugStoreHolder(View view){
           super(view);
       }
    }

    @Override
    public int getItemCount() {
        return mDdata==null?0:mDdata.size();
    }

}
