package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.DrugGroupEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by siery on 15/8/18.
 */
public class DrugGroupAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<DrugGroupEntity> mGroupNodes = new ArrayList<>();
    private HashMap<String, List<DrugGroupEntity>> mChildNodes = new HashMap<>();


    public DrugGroupAdapter(Context _context) {
        this.context = _context;
    }

    public void bindData(List<DrugGroupEntity> groupNodes, HashMap<String, List<DrugGroupEntity>> childNodes) {
        mGroupNodes.clear();
        mChildNodes.clear();
        mGroupNodes.addAll(groupNodes);
        mChildNodes.putAll(childNodes);
    }

    private List<DrugGroupEntity> findChildNodes(int groupPosition) {
        String groupKey = mGroupNodes.get(groupPosition).getId();
        if (mChildNodes.containsKey(groupKey)) {
            return mChildNodes.get(groupKey);
        }
        return null;
    }

    private String getGroupDesc(int groupPosition) {
        List<DrugGroupEntity> childNodes = findChildNodes(groupPosition);
        int size = childNodes == null ? 0 : childNodes.size();
        size = size > 5 ? 5 : size;
        StringBuffer groupDesc = new StringBuffer();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                groupDesc.append("、");
            }
            groupDesc.append(childNodes.get(i).getName());
        }
        return groupDesc.toString();
    }

    @Override
    public int getGroupCount() {
        return mGroupNodes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<DrugGroupEntity> childNodes = findChildNodes(groupPosition);
        return childNodes == null ? 0 : childNodes.size();
    }

    @Override
    public DrugGroupEntity getGroup(int groupPosition) {
        return mGroupNodes.get(groupPosition);
    }

    @Override
    public DrugGroupEntity getChild(int groupPosition, int childPosition) {
        List<DrugGroupEntity> childNodes = findChildNodes(groupPosition);
        return childNodes.get(childPosition);
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
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_drug_type_group, parent, false);
        }
        DrugGroupEntity entity = getGroup(groupPosition);
        TextView titleView = (TextView) convertView.findViewById(R.id.title);
        titleView.setText(entity.getName());
        String groupDesc = getGroupDesc(groupPosition);
        TextView subTitleView = (TextView) convertView.findViewById(R.id.subtitle);
        subTitleView.setText(groupDesc);
        ImageView stateView = (ImageView) convertView.findViewById(R.id.group_state);
        stateView.setImageResource(isExpanded ? R.drawable.ic_arrow_drop_up_grey600_24dp : R.drawable.ic_arrow_drop_down_grey600_24dp);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_drug_type_child, parent, false);
        }
        DrugGroupEntity entity = getChild(groupPosition, childPosition);
        TextView titleView = (TextView) convertView.findViewById(R.id.drug_type_child_caption);
        titleView.setText(entity.getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
