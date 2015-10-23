package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.OrderEntity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ADOrederCell;
import com.romens.yjk.health.ui.components.logger.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by AUSU on 2015/9/12.
 * 已完成订单界面Adapter
 */
public class CompleteAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<OrderEntity> mData=new ArrayList<>();

    public CompleteAdapter(Context context){
        this.mContext=context;
    }
    public void BindValue(List<OrderEntity> data){
        mData.clear();
        if(data!=null&&data.size()!=0){
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ADOrederCell adOrederCell=new ADOrederCell(mContext);
        adOrederCell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,LayoutHelper.WRAP_CONTENT));
        return new CompleteHolder(adOrederCell);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CompleteHolder completeHolder= (CompleteHolder) holder;
        ADOrederCell adOrederCell= (ADOrederCell) completeHolder.itemView;
        OrderEntity orderEntity = mData.get(position);
        Log.i("是否自行了","");
        adOrederCell.setValue(orderEntity.getName(),orderEntity.getImageUrl(),orderEntity.getComment(),true);
    }


    @Override
    public int getItemCount() {
        if(mData.size()!=0){
            Log.i("长度----",mData.size()+"'");
            return mData.size();
        }
        return  0;
    }

    public class CompleteHolder extends RecyclerView.ViewHolder{
        public CompleteHolder(View view){
            super(view);
        }
    }
}
