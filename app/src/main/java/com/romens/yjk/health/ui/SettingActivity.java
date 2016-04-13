package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;

/**
 * Created by anlc on 2016/2/23.
 */
public class SettingActivity extends DarkActionBarActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);
        actionBar.setTitle("设置");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                super.onItemClick(i);
                if (i == -1) {
                    finish();
                }
            }
        });

        setRow();
        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelection(R.drawable.list_selector);
        listView.setAdapter(new MyAdapter());
        container.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(SettingActivity.this, "正在开发，敬请期待!", Toast.LENGTH_SHORT).show();
                } else if (position == 1) {
                    Toast.makeText(SettingActivity.this, "正在开发，敬请期待!", Toast.LENGTH_SHORT).show();
                } else if (position == 2) {
                    UserConfig.clearUser();
                    UserConfig.clearConfig();
                    FacadeToken.getInstance().expired();
//                    userEntity = null;
//                    updateData();
                    AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, -100000);
                    setResult(UserGuidConfig.RESPONSE_SETTING_TO_HOMEMY);
                    finish();
                }
            }
        });
    }

    public ListView getListView() {
        return listView;
    }

    private int rowCount;
    private int changePasswordRow;
    private int checkupRow;
    private int exitRow;

    private void setRow() {
        changePasswordRow = rowCount++;
        checkupRow = rowCount++;
        exitRow = rowCount++;
    }

    @Override
    protected String getActivityName() {
        return "设置";
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextSettingsCell(SettingActivity.this);
            }
            TextSettingsCell cell = (TextSettingsCell) convertView;
            if (position == changePasswordRow) {
                cell.setText("修改密码", true);
            } else if (position == checkupRow) {
                cell.setText("检查更新", true);
            } else if (position == exitRow) {
                cell.setText("退出登录", true);
            }
            return convertView;
        }
    }
}
