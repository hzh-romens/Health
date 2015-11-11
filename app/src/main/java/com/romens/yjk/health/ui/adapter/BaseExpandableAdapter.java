package com.romens.yjk.health.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.log.FileLog;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.cells.KeyAndViewCell;
import com.romens.yjk.health.ui.fragment.BaseFragment;
import com.romens.yjk.health.ui.fragment.OrderFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        if (typeEntitiesList != null) {
            typeEntitiesList.clear();
        }
        if (typeList != null) {
            typeList.clear();
        }
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
//        int type = getGroupType(groupPosition);
//        if (type == 0) {
        if (convertView == null) {
            convertView = new KeyAndViewCell(adapterContext);
        }
        KeyAndViewCell cell = (KeyAndViewCell) convertView;
        cell.setKeyAndRightText("订单编号：" + typeList.get(groupPosition), typeEntitiesList.get(groupPosition).get(0).getOrderStatuster(), true);
        cell.setTextViewColor(adapterContext.getResources().getColor(R.color.order_statu_color));
        cell.setKeyTextColor(adapterContext.getResources().getColor(R.color.theme_title));
//        } else if (type == 1) {
//            if (convertView == null) {
//                convertView = new ShadowSectionCell(adapterContext);
//            }
//        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
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

    public void requestOrderList(String userGuid, final int fragmentType) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("ORDERSTATUS", fragmentType + "");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "getMyOrders", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(adapterContext, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(adapterContext, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    setOrderData(responseProtocol.getResponse(),fragmentType);
                }
                if (errorMsg == null) {
                } else {
                    android.util.Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    public void setOrderData(List<LinkedTreeMap<String, String>> response,int fragmentType) {
        int count = response == null ? -1 : response.size();
        if (count < 0) {
            return;
        } else if (count == 0) {
            Fragment fragment = OrderFragment.getThisFragment((FragmentActivity) adapterContext, fragmentType + "");
            OrderFragment orderFragment = (OrderFragment) fragment;
            orderFragment.clearListEntities();
            orderFragment.refershContentView();
        }
        List<AllOrderEntity> orderEntities = new ArrayList<>();
        for (LinkedTreeMap<String, String> item : response) {
            AllOrderEntity entity = AllOrderEntity.mapToEntity(item);
            orderEntities.add(entity);
        }
        setOrderEntities(orderEntities);
        notifyDataSetChanged();
    }
}
