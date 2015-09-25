package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.EatDrugUserDao;
import com.romens.yjk.health.db.entity.EatDrugUserEntity;
import com.romens.yjk.health.ui.cells.AvatarEditTextCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/9/11.
 * 添加用药提醒中的选择用户页面
 */
public class ChooseUserActivity extends BaseActivity {

    private ListView userListView;
    private UserListViewAdapter userListViewAdapter;
    private List<EatDrugUserEntity> userEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container);
        actionBarEven(actionBar);

        initData();

        userListView = new ListView(this);
        userListView.setDivider(null);
        userListView.setDividerHeight(0);
        userListView.setVerticalScrollBarEnabled(false);
        userListViewAdapter = new UserListViewAdapter(this);
        userListView.setAdapter(userListViewAdapter);
        container.addView(userListView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChooseUserActivity.this, AddRemindActivity.class);
                intent.putExtra("userName", userEntities.get(position).getName());
                setResult(11, intent);
                finish();
            }
        });
    }

    private void initData() {
        userEntities = new ArrayList<>();
        EatDrugUserEntity entity = new EatDrugUserEntity();
        entity.setName("我");
        userEntities.add(entity);
        for (EatDrugUserEntity dbEntity : queryDb()) {
            userEntities.add(dbEntity);
        }
    }

    private void writeDb(EatDrugUserEntity entity) {
        EatDrugUserDao eatDrugUserDao = DBInterface.instance().openReadableDb().getEatDrugUserDao();
        eatDrugUserDao.insert(entity);
    }

    private void updateDb(EatDrugUserEntity entity) {
        EatDrugUserDao eatDrugUserDao = DBInterface.instance().openReadableDb().getEatDrugUserDao();
        eatDrugUserDao.update(entity);
    }

    private List<EatDrugUserEntity> queryDb() {
        EatDrugUserDao eatDrugUserDao = DBInterface.instance().openReadableDb().getEatDrugUserDao();
        return eatDrugUserDao.queryBuilder().list();
    }

    private void deleteDb(EatDrugUserEntity entity) {
        EatDrugUserDao eatDrugUserDao = DBInterface.instance().openReadableDb().getEatDrugUserDao();
        eatDrugUserDao.delete(entity);
    }

    private void actionBarEven(ActionBar actionBar) {
        actionBar.setTitle("选择成员");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.ic_add_grey600_24dp);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    for (EatDrugUserEntity entity : userEntities) {
                        updateDb(entity);
                    }
                    finish();
                } else if (i == 0) {
                    EatDrugUserEntity entity = new EatDrugUserEntity();
                    entity.setName("请输入用户姓名");
                    writeDb(entity);
                    userEntities.add(entity);
                    userListViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    class UserListViewAdapter extends BaseFragmentAdapter {

        private Context context;

        public UserListViewAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return userEntities.size();
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = new AvatarEditTextCell(context);
            }
            AvatarEditTextCell cell = (AvatarEditTextCell) view;
            cell.setValue(userEntities.get(i).getName());
            cell.setActionImageResource(R.drawable.ic_ab_fwd_delete);
            cell.setAvatarEditTextCellDelegate(new AvatarEditTextCell.AvatarEditTextCellDelegate() {
                @Override
                public void onValueChanged(String value) {

                }

                @Override
                public void onAction() {
                    deleteDb(userEntities.get(i));
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }
}
