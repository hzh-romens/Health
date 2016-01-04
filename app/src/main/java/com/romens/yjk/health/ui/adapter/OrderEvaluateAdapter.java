package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.OrderEvaluateDetailActivity;
import com.romens.yjk.health.ui.cells.KeyAndViewCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/9/24.
 * 订单页面中可扩展的listview的Adapter
 */
public class OrderEvaluateAdapter extends BaseExpandableAdapter {

    public OrderEvaluateAdapter(Context adapterContext, List<AllOrderEntity> orderEntities) {
        super(adapterContext, orderEntities);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(adapterContext).inflate(R.layout.list_item_order, null);
        }
        TextView titleTextView = (TextView) convertView.findViewById(R.id.order_title);
        TextView moneyTextView = (TextView) convertView.findViewById(R.id.order_money);
        TextView specTextView = (TextView) convertView.findViewById(R.id.order_date);
        BackupImageView medicineImg = (BackupImageView) convertView.findViewById(R.id.order_img);
//        TextView countTextView = (TextView) view.findViewById(R.key.order_count);

        final AllOrderEntity entity = typeEntitiesList.get(groupPosition).get(childPosition);
        titleTextView.setText(entity.getGoodsName());
//        countTextView.setText("x" + entity.getMerCount());
        moneyTextView.setText("￥" + entity.getOrderPrice());
        specTextView.setText(entity.getCreateDate());
        if (entity.getPicSmall() != null) {
            medicineImg.setImageUrl(entity.getPicSmall(), null, null);
        }else {
            medicineImg.setImageResource(R.drawable.no_img_upload);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(adapterContext, OrderEvaluateDetailActivity.class);
                intent.putExtra("evaluateDetailEntity", entity);
                adapterContext.startActivity(intent);
            }
        });
        return convertView;
    }
}