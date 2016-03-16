package com.romens.yjk.health.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.entity.UserAttributeEntity;
import com.romens.yjk.health.helper.LabelHelper;
import com.romens.yjk.health.model.PersonalEntity;
import com.romens.yjk.health.ui.AccountSettingActivity;
import com.romens.yjk.health.ui.cells.TextDetailSelectCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/12/19.
 */
public class UserLabelsActivity extends BaseActivity {

    private ListView listView;
    private ListAdapter adapter;

    private final List<UserAttributeEntity> userAttributes = new ArrayList<>();

    private List<String[]> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 0) {
                    Gson gson = new Gson();
                    String result = gson.toJson(userAttributes);
                    Intent intent = new Intent(UserLabelsActivity.this, AccountSettingActivity.class);
                    intent.putExtra("userLabelResultJson", result);
                    setResult(UserGuidConfig.RESPONSE_USERLABELS_TO_ACCOUNTSETTING, intent);
                    finish();
                }
            }
        });
        setContentView(content, actionBar);

        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.ic_done);
        actionBar.setTitle("详细信息");

        listView = new ListView(this);
        content.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelector(R.drawable.list_selector);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        listView.setAdapter(adapter = new ListAdapter(this));
        bindData();

        iniData();
    }

    private void iniData() {
        data = new ArrayList<>();
        String[] arr = new String[]{"无", "有"};
        String[] arr1 = new String[]{"低盐", "低脂", "低糖", "高盐", "高脂", "高糖"};
        String[] arr2 = new String[]{"早起早睡", "早起晚睡", "晚起早睡", "晚起晚睡", "熬夜经常", "作息不规律"};
        String[] arr3 = new String[]{"抽烟", "喝酒", "不吃早餐", "饱食"};
        data.add(arr);
        data.add(arr1);
        data.add(arr2);
        data.add(arr3);
    }

    public void bindData() {
        userAttributes.clear();
        Intent intent = getIntent();
        PersonalEntity entity = (PersonalEntity) intent.getSerializableExtra("personEntity");
        if (entity.getHASINHERITED() != null && entity.getHASINHERITED().equals("1")) {
            userAttributes.add(new UserAttributeEntity("heredity", "有无遗传病史").addValue("1", "有"));
        } else {
            userAttributes.add(new UserAttributeEntity("heredity", "有无遗传病史").addValue("0", "无"));
        }
        if (entity.getHASSERIOUS() != null && entity.getHASSERIOUS().equals("1")) {
            userAttributes.add(new UserAttributeEntity("history", "有无病史").addValue("0", "无"));
        } else {
            userAttributes.add(new UserAttributeEntity("history", "有无病史").addValue("1", "有"));
        }
        if (entity.getHASGUOMIN() != null && entity.getHASGUOMIN().equals("1")) {
            userAttributes.add(new UserAttributeEntity("allergy", "是否过敏").addValue("0", "无"));
        } else {
            userAttributes.add(new UserAttributeEntity("allergy", "是否过敏").addValue("1", "有"));
        }
        userAttributes.add(new UserAttributeEntity("preference", "饮食偏好"));
        if (entity.getFOODHOBBY() != null) {
            String[] result = entity.getFOODHOBBY().split(",");
            for (int i = 0; i < result.length; i++) {
                userAttributes.get(3).addValue(i + "", result[i]);
            }
        }
        userAttributes.add(new UserAttributeEntity("habit", "作息习惯"));
        if (entity.getSLEEPHOBBY() != null) {
            String[] result = entity.getSLEEPHOBBY().split(",");
            for (int i = 0; i < result.length; i++) {
                userAttributes.get(4).addValue(i + "", result[i]);
            }
        }
        userAttributes.add(new UserAttributeEntity("other", "其他"));
        if (entity.getOTHER() != null) {
            String[] result = entity.getOTHER().split(",");
            for (int i = 0; i < result.length; i++) {
                userAttributes.get(5).addValue(i + "", result[i]);
            }
        }
        adapter.notifyDataSetChanged();
    }

    class ListAdapter extends BaseFragmentAdapter {
        private Context adapterContext;

        public ListAdapter(Context context) {
            adapterContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public int getCount() {
            return userAttributes.size();
        }

        @Override
        public UserAttributeEntity getItem(int i) {
            return userAttributes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            int type = getItemViewType(position);
            if (type == 0) {
                if (view == null) {
                    view = new TextDetailSelectCell(adapterContext);
                }
                TextDetailSelectCell cell = (TextDetailSelectCell) view;
                cell.setMultilineDetail(true);

                final UserAttributeEntity entity = getItem(position);
                if (entity.valuesDesc.size() > 1 && entity.valuesDesc.get(0).equals("")) {
                    entity.valuesDesc.remove(0);
                }
                if (entity.valuesDesc.size() > 0 && entity.valuesDesc.get(0).equals("")) {
                    cell.setTextAndValue(entity.name, "", true);
                } else {
                    CharSequence labels = LabelHelper.createChipForUserInfoLabels(entity.valuesDesc);
                    cell.setTextAndValue(entity.name, labels, true);
                }
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position < 3) {
                            showSingleChooseView(data.get(0), entity);
                        } else if (position == 3) {
                            showMulitChooseView(data.get(1), entity);
                        } else if (position == 4) {
                            showMulitChooseView(data.get(2), entity);
                        } else if (position == 5) {
                            showMulitChooseView(data.get(3), entity);
                        }
                    }
                });
            }
            return view;
        }
    }

    public void showSingleChooseView(String[] data, final UserAttributeEntity entity) {
        new AlertDialog.Builder(this).setTitle(entity.name)
                .setSingleChoiceItems(data, Integer.parseInt(entity.values.get(0)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        entity.clear();
                        if (which == 0) {
                            entity.addValue("0", "无");
                        } else {
                            entity.addValue("1", "有");
                        }
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void showMulitChooseView(final String[] data, final UserAttributeEntity entity) {
        final boolean[] selectFlag = new boolean[data.length];
        for (int i = 0; i < data.length; i++) {
            if (entity.valuesDesc.contains(data[i])) {
                selectFlag[i] = true;
            }
        }
        new AlertDialog.Builder(this).setTitle(entity.name)
                .setMultiChoiceItems(data, selectFlag, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            entity.addValue(which + "", data[which]);
                        } else {
                            entity.remove(which + "", data[which]);
                        }
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        }).show();
    }
}
