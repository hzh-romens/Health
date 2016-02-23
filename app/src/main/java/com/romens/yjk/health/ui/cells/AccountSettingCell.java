package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2016/2/22.
 */
public class AccountSettingCell extends FrameLayout {

    private ListView listView;
    private Context context;

    public AccountSettingCell(Context context) {
        super(context);
        this.context = context;
        setRow();
        setBackgroundColor(getResources().getColor(R.color.white));
        listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelection(R.drawable.list_selector);
        listView.setAdapter(new MyAdapter());
        addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
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
                convertView = new TextSettingsCell(context);
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
