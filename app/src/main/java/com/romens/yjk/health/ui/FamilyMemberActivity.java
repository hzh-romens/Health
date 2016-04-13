package com.romens.yjk.health.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.FamilyMemberDao;
import com.romens.yjk.health.db.entity.FamilyMemberEntity;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.AvatarAndInfoCell;
import com.romens.yjk.health.ui.utils.TransformDateUitls;

import java.util.Date;
import java.util.List;

/**
 * Created by anlc on 2015/11/9.
 */
public class FamilyMemberActivity extends DarkActionBarActivity {

    private ListView listView;
    private List<FamilyMemberEntity> entitiesList;
    private FamilyMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        container.setBackgroundResource(R.color.line_color);
        setContentView(container, actionBar);
        actionBarEvent();
//        TextSettingsCell textSettingsCell = new TextSettingsCell(this);
//        textSettingsCell.setText("成员列表", true);
//        textSettingsCell.setTextColor(R.color.theme_sub_title);
//        container.addView(textSettingsCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        initData();

        listView = new ListView(this);
        listView.setDividerHeight(0);
        listView.setDivider(null);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelection(R.drawable.list_selector);
        adapter = new FamilyMemberAdapter(this, entitiesList);
        listView.setAdapter(adapter);
        container.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isFromAddRemind()) {
                    Intent intent = new Intent(FamilyMemberActivity.this, AddNewRemindActivity.class);
                    intent.putExtra("remindUser", entitiesList.get(position).getName());
                    setResult(UserGuidConfig.RESPONSE_MEMBER_TO_REMIND, intent);
                    finish();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeDialogView(position);
                return true;
            }
        });
    }

    public void removeDialogView(final int position) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        TextView textView = new TextView(this);

        textView.setBackgroundResource(R.drawable.bg_white);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER);
        textView.setText("删除");
        textView.setTextColor(getResources().getColor(R.color.theme_primary));
        textView.setPadding(AndroidUtilities.dp(32), AndroidUtilities.dp(8), AndroidUtilities.dp(32), AndroidUtilities.dp(8));
        LinearLayout.LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        infoViewParams.weight = 1;
        infoViewParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(infoViewParams);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDb(entitiesList.get(position));
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setContentView(textView);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        adapter.setEntitiesList(entitiesList);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected String getActivityName() {
        return "家庭成员";
    }

    private void deleteDb(FamilyMemberEntity entity) {
        if (entity.isDefault() == 1) {
            Toast.makeText(this, "默认成员不能删除", Toast.LENGTH_SHORT).show();
            return;
        }
        FamilyMemberDao familyMemberDao = DBInterface.instance().openWritableDb().getFamilyMemberDao();
        familyMemberDao.delete(entity);
        entitiesList = familyMemberDao.loadAll();
        adapter.setEntitiesList(entitiesList);
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        FamilyMemberDao familyMemberDao = DBInterface.instance().openWritableDb().getFamilyMemberDao();
        entitiesList = familyMemberDao.loadAll();
        if (entitiesList != null && entitiesList.size() <= 0) {
            FamilyMemberEntity entity = new FamilyMemberEntity();
            entity.setName("我");
            entity.setSex("男");
            entity.setBirthday(TransformDateUitls.getYearDate(new Date().getTime()) + "日出生");
            entity.setAge("0");
            entity.setIsDefault(1);
            familyMemberDao.insert(entity);
            entitiesList = familyMemberDao.loadAll();
        }
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("家庭成员");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(0, R.drawable.ic_add_white_24dp);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                    startActivity(new Intent(FamilyMemberActivity.this, FamilyMemberAddActivity.class));
                }
            }
        });
    }

    public boolean isFromAddRemind() {
        Intent intent = getIntent();
        return intent.getBooleanExtra("isFromAddRemind", false);
    }

    class FamilyMemberAdapter extends BaseAdapter {

        private Context context;
        private List<FamilyMemberEntity> entitiesList;

        public void setEntitiesList(List<FamilyMemberEntity> entitiesList) {
            this.entitiesList = entitiesList;
        }

        public FamilyMemberAdapter(Context context, List<FamilyMemberEntity> entitiesList) {
            this.context = context;
            this.entitiesList = entitiesList;
        }

        @Override
        public int getCount() {
            return entitiesList.size();
        }

        @Override
        public Object getItem(int position) {
            return entitiesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new AvatarAndInfoCell(context);
            }
            AvatarAndInfoCell cell = (AvatarAndInfoCell) convertView;
            cell.setBackgroundColor(Color.WHITE);
            FamilyMemberEntity entity = entitiesList.get(position);
            String subTitle = entity.getBirthday();
            cell.setTitleAndSubTitle(entity.getName(), subTitle, true);
            return convertView;
        }
    }
}
