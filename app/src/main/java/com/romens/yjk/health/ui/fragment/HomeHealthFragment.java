package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.DrugGroupDao;
import com.romens.yjk.health.db.entity.DrugGroupEntity;
import com.romens.yjk.health.ui.ShopListActivity;
import com.romens.yjk.health.ui.cells.ADDiseaseCell.ADDisease;
import com.romens.yjk.health.ui.cells.DrugChildCell;
import com.romens.yjk.health.ui.cells.DrugGroupCell;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by siery on 15/9/12.
 */
public class HomeHealthFragment extends AppFragment {
    private ExpandableListView listView;
    private ListAdapter adapter;
    private List<ADDisease> adDiseaseList = new ArrayList<>();
    public static final String ARGUMENTS_KEY_ID = "key_id";
    public static final String ARGUMENTS_KEY_NAME = "key_name";

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout root = new FrameLayout(context);
        listView = new ExpandableListView(context);
        listView.setDrawSelectorOnTop(true);
        listView.setSelector(R.drawable.list_selector);
        listView.setGroupIndicator(null);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setChildDivider(null);
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                listView.setSelectedGroup(groupPosition);
            }
        });
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                onDrugChildClick(groupPosition, childPosition);
                return true;
            }
        });
        root.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return root;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        adapter = new ListAdapter();
        listView.setAdapter(adapter);
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        onDataChanged();
        requestData();
        bindADDiseaseData();
    }

    private void onDrugChildClick(int groupPosition, int childPosition) {
        final int currGroupPosition = adapter.getCurrentGroupPosition(groupPosition);
        DrugGroupEntity childNode = adapter.getChild(currGroupPosition, childPosition);
        if (childNode != null) {
            //Intent intent = new Intent(getActivity(), IllnessActivity.class);
            Intent intent = new Intent(getActivity(), ShopListActivity.class);
            Bundle arguments = new Bundle();
            arguments.putString(ARGUMENTS_KEY_ID, childNode.getId());
            arguments.putString(ARGUMENTS_KEY_NAME, childNode.getName());
            intent.putExtras(arguments);
            startActivity(intent);
        }
    }

    private void bindADDiseaseData() {
//        adDiseaseRow = 0;
//        adDiseaseTitle = "秋季易发的疾病";
//        adDiseaseSubTitle = "进入秋季后我们人体的消化功能逐渐下降,因此肠道的抗病能力也开始减弱,容易引发疾病.";
//        adDiseaseList.clear();
//        adDiseaseList.add(new ADDisease("2", "感冒", 0));
//        adDiseaseList.add(new ADDisease("0", "急性胃肠炎", 0));
//        adDiseaseList.add(new ADDisease("4", "习惯性便秘", 0));
//        adDiseaseList.add(new ADDisease("1", "口腔溃疡", 0));
//        adDiseaseList.add(new ADDisease("7", "颈腰椎疼痛", 0));
//        adDiseaseList.add(new ADDisease("3", "皮肤瘙痒", 0));
//        adDiseaseList.add(new ADDisease("5", "鼻炎", 0));
//        adDiseaseList.add(new ADDisease("6", "防秋燥", 0));
//
//
//        adapter.notifyDataSetChanged();
//        listView.smoothScrollToPosition(0);
    }

    private void changeRefreshUI(boolean refreshing) {

    }

    private void requestData() {
        changeRefreshUI(true);
        Map<String, Object> args = new HashMap<>();
        int lastTime = DBInterface.instance().getDrugGroupDataLastTime();
       // args.put("LASTTIME", lastTime);
       // args.put("GUID","");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetMedicineKind", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .withProtocol(protocol)
                .build();
        FacadeClient.request(getActivity(), message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                changeRefreshUI(false);
                bindData(null);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                changeRefreshUI(false);
                if (errorMsg == null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    bindData(responseProtocol.getResponse());
                }
            }
        });
    }

    private void bindData(List<LinkedTreeMap<String, String>> nodes) {
        List<DrugGroupEntity> needDb = new ArrayList<>();
        DrugGroupEntity entityTemp;
        for (LinkedTreeMap<String, String> item : nodes) {
            entityTemp = new DrugGroupEntity();
            entityTemp.setId(item.get("ID"));
            entityTemp.setName(item.get("NAME"));
            entityTemp.setPID(item.get("PID"));
            entityTemp.setSortIndex(item.get("ORDERINDEX"));
            entityTemp.setCreated((int) Calendar.getInstance().getTimeInMillis());
            entityTemp.setUpdated((int) Calendar.getInstance().getTimeInMillis());
            needDb.add(entityTemp);
        }
        if (needDb.size() > 0) {
            DrugGroupDao userDao = DBInterface.instance().openWritableDb().getDrugGroupDao();
            userDao.insertOrReplaceInTx(needDb);
        }
        onDataChanged();
    }

    private void onDataChanged() {
        List<DrugGroupEntity> entities = DBInterface.instance().loadAllDrugGroup();
        List<DrugGroupEntity> groupNodes = new ArrayList<>();
        HashMap<String, List<DrugGroupEntity>> childNodes = new HashMap<>();
        for (DrugGroupEntity entity :
                entities) {
            if (TextUtils.isEmpty(entity.getPID())) {
                groupNodes.add(entity);
                if (!childNodes.containsKey(entity.getId())) {
                    childNodes.put(entity.getId(), new ArrayList<DrugGroupEntity>());
                }
            } else {
                if (!childNodes.containsKey(entity.getPID())) {
                    childNodes.put(entity.getPID(), new ArrayList<DrugGroupEntity>());
                }
                childNodes.get(entity.getPID()).add(entity);
            }
        }
        adapter.bindData(groupNodes, childNodes);
        adapter.notifyDataSetChanged();
        listView.smoothScrollToPosition(0);
    }

    public class ListAdapter extends BaseExpandableListAdapter {
        private List<DrugGroupEntity> mGroupNodes = new ArrayList<>();
        private HashMap<String, List<DrugGroupEntity>> mChildNodes = new HashMap<>();

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
            int count = mGroupNodes.size();
            return count;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            final int currGroupPosition = getCurrentGroupPosition(groupPosition);
            List<DrugGroupEntity> childNodes = findChildNodes(currGroupPosition);
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
        public int getGroupType(int groupPosition) {
            return 0;
        }

        @Override
        public int getGroupTypeCount() {
            return 1;
        }


        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final int type = getGroupType(groupPosition);
            final int currGroupPosition = getCurrentGroupPosition(groupPosition);
            if (type == 0) {
                if (convertView == null) {
                    convertView = new DrugGroupCell(getActivity());
                }
                DrugGroupEntity groupNode = getGroup(currGroupPosition);
                DrugGroupCell cell = (DrugGroupCell) convertView;
                String groupDesc = getGroupDesc(currGroupPosition);
                cell.setValue(groupNode.getName(), groupDesc, isExpanded, true);
            }
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            convertView.setLayoutParams(lp);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new DrugChildCell(getActivity());
            }
            final int currGroupPosition = getCurrentGroupPosition(groupPosition);
            DrugGroupEntity childNode = getChild(currGroupPosition, childPosition);
            ((DrugChildCell) convertView).setValue(childNode.getName(), !isLastChild);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            convertView.setLayoutParams(lp);
            return convertView;
        }

        public int getCurrentGroupPosition(int groupPosition) {
            int position = groupPosition;
            return position;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


}