package com.romens.yjk.health.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by anlc on 2015/9/18.
 * 我的订单中全部页面的adapter
 */
public class AllOrderAdpter extends RecyclerView.Adapter<AllOrderAdpter.OrderViewHolder> {


    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class OrderViewHolder extends RecyclerView.ViewHolder{

        public OrderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
