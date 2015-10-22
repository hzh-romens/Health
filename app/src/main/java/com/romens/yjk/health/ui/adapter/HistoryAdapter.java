package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.io.image.ImageManager;
import com.romens.android.io.image.ImageUtils;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.HistoryEntity;
import com.romens.yjk.health.model.GoodsListEntity;
import com.romens.yjk.health.ui.cells.IsSelectCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/14.
 */
public class HistoryAdapter extends BaseExpandableListAdapter {

    private List<List<HistoryEntity>> typeEntitiesList;
    private List<String> typeList;
    private Context adapterContext;

    public void setOrderEntities(List<HistoryEntity> orderEntities) {
        classifyEntity(orderEntities);
    }

    public HistoryAdapter(Context context) {
        this.adapterContext = context;
        classifyEntity(new ArrayList<HistoryEntity>());
    }

    private void classifyEntity(List<HistoryEntity> orderEntities) {
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
            List<HistoryEntity> tempList = new ArrayList<>();
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
        ParentHolder parentHolder=null;
        if (convertView == null) {
            convertView=LayoutInflater.from(adapterContext).inflate(R.layout.list_item_group,null);
            parentHolder=new ParentHolder();
            parentHolder.name= (TextView) convertView.findViewById(R.id.name);
            parentHolder.empty_view=convertView.findViewById(R.id.empty_view);
            convertView.setTag(parentHolder);
        }else{
            parentHolder= (ParentHolder) convertView.getTag();
        }
        if(groupPosition==0){
            parentHolder.empty_view.setVisibility(View.GONE);
        }else{
            parentHolder.empty_view.setVisibility(View.VISIBLE);
        }
        parentHolder.name.setText(typeList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder=null;
        if (convertView == null) {
            childHolder=new ChildHolder();
            LayoutInflater inflater = LayoutInflater.from(adapterContext);
            convertView = inflater.inflate(R.layout.list_item_shop_list_history, null);
            childHolder.iv= (ImageView) convertView.findViewById(R.id.iv);
            childHolder.shop= (ImageView) convertView.findViewById(R.id.shop);
            childHolder.name= (TextView) convertView.findViewById(R.id.name);
            childHolder.discountPrice= (TextView) convertView.findViewById(R.id.discountPrice);
            childHolder.realPrice= (TextView) convertView.findViewById(R.id.realPrice);
            childHolder.comment= (TextView) convertView.findViewById(R.id.comment);
            childHolder.linear_item= (LinearLayout) convertView.findViewById(R.id.linear_item);
            childHolder.saleCount= (TextView) convertView.findViewById(R.id.saleCount);
            convertView.setTag(childHolder);
        }else{
            childHolder= (ChildHolder) convertView.getTag();
        }


        final HistoryEntity entity = typeEntitiesList.get(groupPosition).get(childPosition);

        Drawable defaultDrawables =  childHolder.iv.getDrawable();
        ImageManager.loadForView(adapterContext, childHolder.iv, entity.getImgUrl(), defaultDrawables, defaultDrawables);
        childHolder.name.setText(entity.getMedicinalName());
        childHolder.realPrice.setTextColor(adapterContext.getResources().getColor(R.color.md_red_400));
        childHolder.realPrice.setText("¥" + entity.getCurrentPrice());
        childHolder.realPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        childHolder.discountPrice.setText("¥" + entity.getDiscountPrice());
        childHolder.shop.setImageDrawable(adapterContext.getResources().getDrawable(R.drawable.ic_list_shopcaricon));
        childHolder.shop.setVisibility(View.GONE);
        childHolder.comment.setText(entity.getCommentCount()+"条评论");
        childHolder.saleCount.setText(entity.getSaleCount()+"件已售");
        return convertView;
    }
    class ChildHolder{
        private ImageView iv,shop;
        private TextView name,discountPrice,realPrice,comment,saleCount;
        private LinearLayout linear_item;
    }
    class ParentHolder{
        private TextView name;
        private View empty_view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public List<HistoryEntity> getSelectEntities() {
        List<HistoryEntity> entities = new ArrayList<>();
        for (List<HistoryEntity> list : typeEntitiesList) {
            for (HistoryEntity entity : list) {
                if (entity.isSelect()) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }
}
