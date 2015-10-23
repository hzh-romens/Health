package com.romens.yjk.health.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.romens.android.log.FileLog;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.entity.AllOrderEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/22.
 */
public class BaseExpandableAdapter extends BaseExpandableListAdapter {

    protected List<List<AllOrderEntity>> typeEntitiesList;
    protected List<String> typeList;
    protected Context adapterContext;
    protected String userGuid = UserGuidConfig.USER_GUID;
    private ProgressDialog progressDialog;

    public BaseExpandableAdapter(Context adapterContext, List<AllOrderEntity> orderEntities) {
        this.adapterContext = adapterContext;
        classifyEntity(orderEntities);
    }

    public void setOrderEntities(List<AllOrderEntity> orderEntities) {
        classifyEntity(orderEntities);
    }

    private void classifyEntity(List<AllOrderEntity> orderEntities) {
        typeList = new ArrayList<>();
        for (int i = 0; i < orderEntities.size(); i++) {
            boolean flag = true;
            String drugStroe = orderEntities.get(i).getOrderNo();
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
                if (drugStroe.equals(orderEntities.get(i).getOrderNo())) {
                    tempList.add(orderEntities.get(i));
                }
            }
            typeEntitiesList.add(tempList);
        }
    }

    @Override
    public int getGroupCount() {
        return typeEntitiesList.size() * 2 - 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == typeEntitiesList.size() + 1) {
            return 0;
        }
        return groupPosition % 2 == 0 ? typeEntitiesList.get(groupPosition / 2).size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return typeEntitiesList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return typeEntitiesList.get(groupPosition / 2).get(childPosition);
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
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getGroupType(int groupPosition) {
        return groupPosition % 2;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void needShowProgress(String progressText) {
        if (progressDialog != null) {
            return;
        }
        progressDialog = new ProgressDialog(adapterContext);
        progressDialog.setMessage(progressText);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void needHideProgress() {
        if (progressDialog == null) {
            return;
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(adapterContext.getPackageName(), e);
        }
        progressDialog = null;
    }
}
