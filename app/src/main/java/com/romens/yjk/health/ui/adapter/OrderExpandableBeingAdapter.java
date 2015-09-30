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

import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.OrderEvaluateActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/9/24.
 * 订单页面中可扩展的listview的Adapter
 */
public class OrderExpandableBeingAdapter extends BaseExpandableListAdapter {

    private List<List<AllOrderEntity>> typeEntitiesList;
    private List<String> typeList;
    private Context adapterContext;

//    private int leftButtonBackGround;
//    private int rightButtonBackGround;
//    private Class intentActivity;

    public OrderExpandableBeingAdapter(Context context, List<AllOrderEntity> orderEntities) {
        this.adapterContext = context;
//        this.leftButtonBackGround = leftButtonBackGround;
//        this.rightButtonBackGround = rightButtonBackGround;
//        this.intentActivity = intentActivity;
        classifyEntity(orderEntities);
    }

    private void classifyEntity(List<AllOrderEntity> orderEntities) {
        typeList = new ArrayList<>();
        for (int i = 0; i < orderEntities.size(); i++) {
            boolean flag = true;
            String drugStroe = orderEntities.get(i).getDrugStroe();
            for (int j = 0; j < typeList.size(); j++) {
                if (drugStroe.equals(typeList.get(j))) {
                    flag = false;
                }
            }
            if (flag) {
                typeList.add(drugStroe);
            }
        }
        typeEntitiesList = new ArrayList<>();
        for (String drugStroe : typeList) {
            List<AllOrderEntity> tempList = new ArrayList<>();
            for (int i = 0; i < orderEntities.size(); i++) {
                if (drugStroe.equals(orderEntities.get(i).getDrugStroe())) {
                    tempList.add(orderEntities.get(i));
                }
            }
            typeEntitiesList.add(tempList);
        }
    }

    @Override
    public int getGroupCount() {
        return typeEntitiesList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return typeEntitiesList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return typeEntitiesList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return typeEntitiesList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextSettingsCell(adapterContext);
        }
        TextSettingsCell cell = (TextSettingsCell) convertView;
        cell.setTextAndValue(typeList.get(groupPosition), "待评价", true);
        return cell;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(adapterContext).inflate(R.layout.list_item_order_complete, null);
        BackupImageView leftImageView = (BackupImageView) view.findViewById(R.id.order_img);
        TextView titleTextView = (TextView) view.findViewById(R.id.order_title);
        TextView specTextView = (TextView) view.findViewById(R.id.order_spec);
        TextView moneyTextView = (TextView) view.findViewById(R.id.order_money);
        TextView dateTextView = (TextView) view.findViewById(R.id.order_date);
        TextView countTextView = (TextView) view.findViewById(R.id.order_count);

        Button buyAgainBtn = (Button) view.findViewById(R.id.order_all_buy_again);
        Button evaluateBtn = (Button) view.findViewById(R.id.order_all_evaluate_btn);

        String url = "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg";
        leftImageView.setImageUrl(url, "64_64", null);
        titleTextView.setText(typeEntitiesList.get(groupPosition).get(childPosition).getGoodsName());
        specTextView.setText("12g*10袋");
        countTextView.setText("x2");
        moneyTextView.setText(typeEntitiesList.get(groupPosition).get(childPosition).getOrderPrice());
        dateTextView.setText("2015-12-15 08:09");
        evaluateBtn.setBackgroundResource(R.drawable.order_complete);
        evaluateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(adapterContext, "click-->确认订单", Toast.LENGTH_SHORT).show();
            }
        });
        buyAgainBtn.setBackgroundResource(R.drawable.order_follow);
        buyAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(adapterContext, "click-->订单跟踪", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}