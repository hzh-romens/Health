package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.OrderEvaluateActivity;
import com.romens.yjk.health.ui.cells.KeyAndValueCell;
import com.romens.yjk.health.ui.utils.TransformDateUitls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/9/24.
 * 订单页面中可扩展的listview的Adapter
 */
public class OrderExpandableAlreadyCompleteAdapter extends BaseExpandableAdapter {

    public OrderExpandableAlreadyCompleteAdapter(Context adapterContext, List<AllOrderEntity> orderEntities) {
        super(adapterContext, orderEntities);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        int type = getGroupType(groupPosition);
        if (type == 0) {
            if (convertView == null) {
                convertView = new KeyAndValueCell(adapterContext);
            }
            KeyAndValueCell cell = (KeyAndValueCell) convertView;
            cell.setKeyAndValue("订单编号：" + typeList.get(groupPosition / 2), "交易完成", true);
            cell.setValueTextColor(0xfff06292);
        } else if (type == 1) {
            if (convertView == null) {
                convertView = new ShadowSectionCell(adapterContext);
            }
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(adapterContext).inflate(R.layout.list_item_order_complete, null);
        TextView titleTextView = (TextView) view.findViewById(R.id.order_title);
        TextView moneyTextView = (TextView) view.findViewById(R.id.order_money);
        TextView dateTextView = (TextView) view.findViewById(R.id.order_date);
//        TextView countTextView = (TextView) view.findViewById(R.id.order_count);

        Button buyAgainBtn = (Button) view.findViewById(R.id.order_all_buy_again);
        Button evaluateBtn = (Button) view.findViewById(R.id.order_all_evaluate_btn);
        AllOrderEntity entity = typeEntitiesList.get(groupPosition / 2).get(childPosition);
        titleTextView.setText(entity.getGoodsName());
//        countTextView.setText("x" + entity.getMerCount());
        moneyTextView.setText("￥" + entity.getOrderPrice());
//      dateTextView.setText("2015-12-15 08:09");
        TransformDateUitls.getDate(entity.getCreateDate());
        dateTextView.setText(entity.getCreateDate());
        evaluateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(adapterContext, OrderEvaluateActivity.class);
//              Intent intent = new Intent(adapterContext, intentActivity);
                intent.putExtra("orderEntity", typeEntitiesList.get(groupPosition / 2).get(childPosition));
                adapterContext.startActivity(intent);
            }
        });
        buyAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(adapterContext, "click-->再来一单", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}