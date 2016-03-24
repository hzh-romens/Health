package com.romens.yjk.health.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
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
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.SearchResultEntity;
import com.romens.yjk.health.ui.cells.AvatarAndInfoCell;
import com.romens.yjk.health.ui.cells.ImgAndValueCell;
import com.romens.yjk.health.ui.utils.DialogUtils;

import java.util.ArrayList;
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

        initData(true);
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
                    intent.putExtra("drugGroup_drug", entityList.get(position - 2).getDrugName());
                    setResult(UserGuidConfig.RESPONSE_DRUGGROUP_TO_REMIND, intent);
                    finish();
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
            SearchResultEntity entity = (SearchResultEntity) data.getSerializableExtra("searchDrugEntity");
            FamilyDrugGroupEntity drugGroupEntity = new FamilyDrugGroupEntity();
            drugGroupEntity.setDrugGuid(entity.id);
            drugGroupEntity.setRemark(entity.type);
            drugGroupEntity.setDrugName(entity.name);
            FamilyDrugGroupDao familyDrugGroupDao = DBInterface.instance().openWritableDb().getFamilyDrugGroupDao();
            entityList = familyDrugGroupDao.loadAll();
            boolean flag = true;
            for (FamilyDrugGroupEntity item : entityList) {
                if (item.getDrugName().equals(drugGroupEntity.getDrugName())) {
                    Toast.makeText(FamilyDrugGroupActivity.this, "该药品已在药箱", Toast.LENGTH_SHORT).show();
                    flag = false;
                }
            }
            if (flag) {
                familyDrugGroupDao.insert(drugGroupEntity);
            }
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

    public void initData(boolean isFirst) {
        rowCount = 0;
        if (isFirst) {
            FamilyDrugGroupDao drugGroupDao = DBInterface.instance().openReadableDb().getFamilyDrugGroupDao();
            entityList = drugGroupDao.loadAll();
        }

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
            initData(false);
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
        public boolean isEnabled(int position) {
            return position != drupTitleRow;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
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
                        UIOpenHelper.openSearchActivity(FamilyDrugGroupActivity.this, true);
//                        Intent intent = new Intent(FamilyDrugGroupActivity.this, SearchActivityNew.class);
//                        intent.putExtra("fromFramilyDrugGroupTag", true);
//                        startActivityForResult(intent, UserGuidConfig.REQUEST_SEARCH);
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
                final AvatarAndInfoCell cell = (AvatarAndInfoCell) convertView;
                cell.setBackgroundColor(Color.WHITE);
                cell.setTitleAndSubTitle(entityList.get(position - 2).getDrugName(), entityList.get(position - 2).getRemark(), true);
                cell.setRightImgResource(R.drawable.ic_more_vert_grey600_24dp);
                cell.setDivider(true, AndroidUtilities.dp(60), 0);
                cell.setOnRightViewClickListener(new AvatarAndInfoCell.OnRightViewClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopEditRemark(view, position - 2);
                    }
                });
            }
            return convertView;
        }

        public void showPopEditRemark(View view, final int position) {
            final PopupWindow popupWindow = new PopupWindow(AndroidUtilities.dp(100), LayoutHelper.WRAP_CONTENT);
            FrameLayout frameLayout = new FrameLayout(context);

            List<String> list = new ArrayList<>();
//            list.add("备注");
            list.add("删除");
            ListView listView = new ListView(context);
            listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list));

            FrameLayout.LayoutParams remarkViewParams = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
            frameLayout.addView(listView, remarkViewParams);

            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.bg_popupwindow));
            popupWindow.setContentView(frameLayout);
            popupWindow.showAsDropDown(view, 1, 1);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                    /*if (i == 0) {
                        showEditRemark(position);
                        popupWindow.dismiss();
                    } else*/
                    if (i == 0) {
                        deleteDb(entityList.get(position));
                        popupWindow.dismiss();
                    }
                }
            });
        }

        public void showEditRemark(final int position) {
            final AlertDialog dialog = new AlertDialog.Builder(context).create();

            View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_remark, null);
            final MaterialEditText editText = (MaterialEditText) view.findViewById(R.id.remark_edit);
            TextView confirmBtn = (TextView) view.findViewById(R.id.remark_confirm);
            TextView cancelBtn = (TextView) view.findViewById(R.id.remark_cancel);

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String remark = editText.getText().toString().trim();
                    if (null == remark || remark.equals("")) {
                        Toast.makeText(context, "请输入备注", Toast.LENGTH_SHORT).show();
                    } else {
                        FamilyDrugGroupEntity entity = entityList.get(position);
                        entity.setRemark(remark);
                        updataDb(entity);
                        dialog.dismiss();
                    }
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setView(view);
            dialog.show();
        }
    }

    public void deleteDb(FamilyDrugGroupEntity entity) {
        FamilyDrugGroupDao drugGroupDao = DBInterface.instance().openWritableDb().getFamilyDrugGroupDao();
        drugGroupDao.delete(entity);
        entityList = drugGroupDao.loadAll();
        adapter.setEntityList(entityList);
    }

    public void updataDb(FamilyDrugGroupEntity entity) {
//        FamilyDrugGroupDao drugGroupDao = DBInterface.instance().openWritableDb().getFamilyDrugGroupDao();
//        drugGroupDao.update(entity);
//        entityList = drugGroupDao.loadAll();
//        adapter.setEntityList(entityList);
    }
}
