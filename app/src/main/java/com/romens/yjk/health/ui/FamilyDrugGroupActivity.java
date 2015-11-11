package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.FamilyDrugGroupDao;
import com.romens.yjk.health.db.entity.FamilyDrugGroupEntity;
import com.romens.yjk.health.ui.cells.AvatarAndInfoCell;
import com.romens.yjk.health.ui.cells.ImgAndValueCell;

import java.util.List;

/**
 * Created by anlc on 2015/11/9.
 */
public class FamilyDrugGroupActivity extends BaseActivity {

    private ListView listView;
    private List<FamilyDrugGroupEntity> entityList;
    private FamilyDrugGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        container.setBackgroundResource(R.color.line_color);
        setContentView(container, actionBar);
        actionBarEvent();

        initData();
        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelection(R.drawable.list_selector);
        adapter = new FamilyDrugGroupAdapter(this, entityList);
        listView.setAdapter(adapter);
        container.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isFromAddremimd()) {
                    Intent intent = new Intent(FamilyDrugGroupActivity.this, AddNewRemindActivity.class);
                    intent.putExtra("drugGroup_drug", entityList.get(position).getDrugName());
                    setResult(UserGuidConfig.RESPONSE_DRUGGROUP_TO_REMIND,intent);
                }
            }
        });
    }

    public boolean isFromAddremimd() {
        return getIntent().getBooleanExtra("isFromAddRemindDrug", false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UserGuidConfig.RESPONSE_SEARCH_TO_DRUGGROUP) {
            SearchActivityNew.SearchResultEntity entity = (SearchActivityNew.SearchResultEntity) data.getSerializableExtra("searchDrugEntity");
            FamilyDrugGroupEntity drugGroupEntity = new FamilyDrugGroupEntity();
            drugGroupEntity.setDrugGuid(entity.guid);
            drugGroupEntity.setRemark(entity.spec);
            drugGroupEntity.setDrugName(entity.name);
            FamilyDrugGroupDao familyDrugGroupDao = DBInterface.instance().openWritableDb().getFamilyDrugGroupDao();
            familyDrugGroupDao.insert(drugGroupEntity);
            entityList = familyDrugGroupDao.loadAll();
            adapter.setEntityList(entityList);
        }
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("家庭药箱");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                }
            }
        });
    }

    private int rowCount;
    private int addDrugRow;
    private int drupTitleRow;

    public void initData() {
        rowCount = 0;
        FamilyDrugGroupDao drugGroupDao = DBInterface.instance().openReadableDb().getFamilyDrugGroupDao();
        entityList = drugGroupDao.loadAll();

        addDrugRow = rowCount++;
        if (entityList.size() <= 0) {
            drupTitleRow = -1;
        } else {
            drupTitleRow = rowCount++;
        }
    }

    class FamilyDrugGroupAdapter extends BaseAdapter {

        private Context context;
        private List<FamilyDrugGroupEntity> entityList;

        public void setEntityList(List<FamilyDrugGroupEntity> entityList) {
            this.entityList = entityList;
            notifyDataSetChanged();
        }

        public FamilyDrugGroupAdapter(Context context, List<FamilyDrugGroupEntity> entityList) {
            this.context = context;
            this.entityList = entityList;
        }

        @Override
        public int getCount() {
            return rowCount + entityList.size();
        }

        @Override
        public Object getItem(int position) {
            return entityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == addDrugRow) {
                return 1;
            } else if (position == drupTitleRow) {
                return 2;
            } else {
                return 3;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 1) {
                if (convertView == null) {
                    convertView = new ImgAndValueCell(context);
                }
                ImgAndValueCell cell = (ImgAndValueCell) convertView;
                cell.setData(R.drawable.add_png, "搜索并添加药物", true);
                cell.setBackgroundColor(Color.WHITE);
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FamilyDrugGroupActivity.this, SearchActivityNew.class);
                        intent.putExtra("fromFramilyDrugGroupTag", true);
                        startActivityForResult(intent, UserGuidConfig.REQUEST_SEARCH);
                    }
                });
            } else if (type == 2) {
                if (convertView == null) {
                    convertView = new TextSettingsCell(context);
                }
                TextSettingsCell cell = (TextSettingsCell) convertView;
                cell.setText("常备药(" + entityList.size() + ")", true);
                cell.setTextColor(R.color.theme_sub_title);
            } else if (type == 3) {
                if (convertView == null) {
                    convertView = new AvatarAndInfoCell(context);
                }
                AvatarAndInfoCell cell = (AvatarAndInfoCell) convertView;
                cell.setTitleAndSubTitle(entityList.get(position).getDrugName(), entityList.get(position).getRemark(), true);
                cell.setRightImgResource(R.drawable.ic_ab_other);
                cell.setDivider(true, AndroidUtilities.dp(80), 0);
            }
            return convertView;
        }
    }
}
