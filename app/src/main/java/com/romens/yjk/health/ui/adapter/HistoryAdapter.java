package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.GoodsListEntity;
import com.romens.yjk.health.ui.cells.IsSelectCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/14.
 */
public class HistoryAdapter extends BaseExpandableListAdapter {

    private List<List<GoodsListEntity>> typeEntitiesList;
    private List<String> typeList;
    private Context adapterContext;

    public void setOrderEntities(List<GoodsListEntity> orderEntities) {
        classifyEntity(orderEntities);
    }

    public HistoryAdapter(Context context) {
        this.adapterContext = context;
        classifyEntity(new ArrayList<GoodsListEntity>());
    }

    private void classifyEntity(List<GoodsListEntity> orderEntities) {
        typeList = new ArrayList<>();
        for (int i = 0; i < orderEntities.size(); i++) {
            boolean flag = true;
            String drugStroe = orderEntities.get(i).getShopName();
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
            List<GoodsListEntity> tempList = new ArrayList<>();
            for (int i = 0; i < orderEntities.size(); i++) {
                if (drugStroe.equals(orderEntities.get(i).getShopName())) {
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new IsSelectCell(adapterContext);
        }
        final IsSelectCell cell = (IsSelectCell) convertView;
        cell.setInfo(typeList.get(groupPosition), true);
        cell.setClick(new IsSelectCell.SelectClick() {
            @Override
            public void onClick() {
                boolean isSelect = cell.changeSelect();
                List<GoodsListEntity> list = typeEntitiesList.get(groupPosition);
                for (GoodsListEntity entity : list) {
                    entity.setIsSelect(isSelect);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(adapterContext);
            convertView = inflater.inflate(R.layout.list_item_order_deatil, null);
        }
        final ImageView isSelectImg = (ImageView) convertView.findViewById(R.id.order_select_img);
        BackupImageView leftImg = (BackupImageView) convertView.findViewById(R.id.order_img);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.order_title);
        TextView moneyTextView = (TextView) convertView.findViewById(R.id.order_money);
        TextView specTextView = (TextView) convertView.findViewById(R.id.order_spec);
        TextView countTextView = (TextView) convertView.findViewById(R.id.order_count);

        final GoodsListEntity entity = typeEntitiesList.get(groupPosition).get(childPosition);
        if (entity != null) {
            titleTextView.setText(entity.getShopName());
            countTextView.setText("x" + entity.getBuyCount());
            moneyTextView.setText(entity.getGoodsPrice());
            specTextView.setText(entity.getSpec());
            leftImg.setImageUrl(entity.getGoodsUrl(), "64_64", null);
        }
        isSelectImg.setVisibility(View.VISIBLE);
        isSelectImg.setImageResource(R.drawable.control_address_undeafult);
        if (entity.isSelect()) {
            isSelectImg.setImageResource(R.drawable.control_address_deafult);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity.isSelect()) {
                    isSelectImg.setImageResource(R.drawable.control_address_undeafult);
                    entity.setIsSelect(false);
                    return;
                }
                isSelectImg.setImageResource(R.drawable.control_address_deafult);
                entity.setIsSelect(true);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public List<GoodsListEntity> getSelectEntities() {
        List<GoodsListEntity> entities = new ArrayList<>();
        for (List<GoodsListEntity> list : typeEntitiesList) {
            for (GoodsListEntity entity : list) {
                if (entity.isSelect()) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }
}
