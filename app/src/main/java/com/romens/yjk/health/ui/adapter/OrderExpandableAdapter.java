package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.cells.KeyAndViewCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/9/24.
 * 订单页面中可扩展的listview的Adapter
 */
public class OrderExpandableAdapter extends BaseExpandableAdapter {

    public OrderExpandableAdapter(Context adapterContext, List<AllOrderEntity> orderEntities) {
        super(adapterContext, orderEntities);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(adapterContext).inflate(R.layout.list_item_order, null);
        TextView titleTextView = (TextView) view.findViewById(R.id.order_title);
        TextView moneyTextView = (TextView) view.findViewById(R.id.order_money);
        TextView specTextView = (TextView) view.findViewById(R.id.order_date);
//        TextView countTextView = (TextView) view.findViewById(R.key.order_count);

        AllOrderEntity entity = typeEntitiesList.get(groupPosition).get(childPosition);
        titleTextView.setText(entity.getGoodsName());
//        countTextView.setText("x" + entity.getMerCount());
        moneyTextView.setText("￥" + entity.getOrderPrice());
        specTextView.setText(entity.getCreateDate());
        return view;
    }
}